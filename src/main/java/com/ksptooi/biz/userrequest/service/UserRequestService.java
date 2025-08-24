package com.ksptooi.biz.userrequest.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ksptooi.biz.core.model.request.RequestPo;
import com.ksptooi.biz.core.repository.RequestRepository;
import com.ksptooi.biz.user.model.user.UserPo;
import com.ksptooi.biz.user.service.AuthService;
import com.ksptooi.biz.userrequest.model.userrequest.*;
import com.ksptooi.biz.userrequest.model.userrequestgroup.UserRequestGroupPo;
import com.ksptooi.biz.userrequest.model.userrequestlog.UserRequestLogPo;
import com.ksptooi.biz.userrequest.model.userrequesttree.dto.EditUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequesttree.dto.GetUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequesttree.vo.GetUserRequestTreeVo;
import com.ksptooi.biz.userrequest.repository.UserRequestGroupRepository;
import com.ksptooi.biz.userrequest.repository.UserRequestLogRepository;
import com.ksptooi.biz.userrequest.repository.UserRequestRepository;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.http.HttpHeaderVo;
import com.ksptooi.commons.http.RequestSchema;
import com.ksptooi.commons.utils.web.CommonIdDto;
import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static com.ksptool.entities.Entities.as;

@Slf4j
@Service
public class UserRequestService {

    @Autowired
    private UserRequestRepository repository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRequestGroupRepository userRequestGroupRepository;

    @Autowired
    private Gson gson;

    @Autowired
    private UserRequestLogRepository userRequestLogRepository;

    @Autowired
    private UserRequestFilterService userRequestFilterService;

    //hop-by-hop请求头
    private static final Set<String> HOP_BY_HOP_HEADERS = new HashSet<>();

    static {
        HOP_BY_HOP_HEADERS.add("connection");
        HOP_BY_HOP_HEADERS.add("keep-alive");
        HOP_BY_HOP_HEADERS.add("proxy-authenticate");
        HOP_BY_HOP_HEADERS.add("proxy-authorization");
        HOP_BY_HOP_HEADERS.add("te");
        HOP_BY_HOP_HEADERS.add("trailer");
        HOP_BY_HOP_HEADERS.add("transfer-encoding");
        HOP_BY_HOP_HEADERS.add("upgrade");
        HOP_BY_HOP_HEADERS.add("host");
        HOP_BY_HOP_HEADERS.add("content-length");
    }

    /**
     * 保存原始请求为用户请求
     *
     * @param dto 保存请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAsUserRequest(SaveAsUserRequestDto dto) throws BizException, AuthException {

        //查询原始请求
        RequestPo requestPo = requestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new BizException("原始请求不存在."));

        //创建用户请求
        UserRequestPo userRequestPo = new UserRequestPo();
        userRequestPo.setGroup(null);
        userRequestPo.setOriginalRequest(requestPo);
        userRequestPo.setUser(authService.requireUser());
        userRequestPo.setName(requestPo.getRequestId());//如果未提供名称 则使用请求ID作为名称
        userRequestPo.setMethod(requestPo.getMethod());
        userRequestPo.setUrl(requestPo.getUrl());

        //将请求头转换为列表
        List<HttpHeaderVo> requestHeaders = new ArrayList<>();

        try {
            Type mapType = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> rhm = gson.fromJson(requestPo.getRequestHeaders(), mapType);
            for (Map.Entry<String, String> entry : rhm.entrySet()) {
                HttpHeaderVo httpHeaderVo = new HttpHeaderVo();
                httpHeaderVo.setK(entry.getKey());
                httpHeaderVo.setV(entry.getValue());
                requestHeaders.add(httpHeaderVo);
            }
        } catch (Exception e) {
        }

        userRequestPo.setRequestHeaders(gson.toJson(requestHeaders));

        userRequestPo.setRequestBodyType(requestPo.getRequestBodyType());
        userRequestPo.setRequestBody(requestPo.getRequestBody());

        if (StringUtils.isNotBlank(dto.getName())) {
            userRequestPo.setName(dto.getName());
        }

        //userRequestPo.setSeq(repository.getNextSeq(AuthService.requireUserId()));
        repository.save(userRequestPo);
    }


    /**
     * 获取用户请求树
     *
     * @param dto 获取请求参数
     * @return 用户请求树
     */
    public List<GetUserRequestTreeVo> getUserRequestTree(GetUserRequestTreeDto dto) throws AuthException {

        //先获取请求组
        List<UserRequestGroupPo> groupList = userRequestGroupRepository.getUserRequestGroupWithRequests(AuthService.requireUserId());
        List<GetUserRequestTreeVo> treeList = new ArrayList<>();

        //将请求组转换为树形结构
        if (groupList == null || groupList.isEmpty()) {
            return treeList;
        }

        Map<Long, GetUserRequestTreeVo> groupNodeMap = new HashMap<>();

        // 第一遍：创建组节点
        for (UserRequestGroupPo groupPo : groupList) {
            GetUserRequestTreeVo groupNode = new GetUserRequestTreeVo();
            groupNode.setId(groupPo.getId());
            groupNode.setParentId(null);

            if (groupPo.getParent() != null) {
                groupNode.setParentId(groupPo.getParent().getId());
            }

            groupNode.setType(0);
            groupNode.setName(groupPo.getName());
            groupNode.setChildren(new ArrayList<>());
            groupNodeMap.put(groupPo.getId(), groupNode);
        }

        // 第二遍：挂载父子组关系
        for (UserRequestGroupPo groupPo : groupList) {
            GetUserRequestTreeVo current = groupNodeMap.get(groupPo.getId());
            current.setSimpleFilterCount(groupPo.getFilters().size());
            if (groupPo.getParent() == null) {
                treeList.add(current);
                continue;
            }
            GetUserRequestTreeVo parentNode = groupNodeMap.get(groupPo.getParent().getId());
            if (parentNode == null) {
                treeList.add(current);
                continue;
            }
            parentNode.getChildren().add(current);
        }

        // 第三遍：为每个组挂载请求
        for (UserRequestGroupPo groupPo : groupList) {
            GetUserRequestTreeVo groupNode = groupNodeMap.get(groupPo.getId());
            if (groupNode == null) {
                continue;
            }
            if (groupPo.getRequests() == null || groupPo.getRequests().isEmpty()) {
                continue;
            }
            for (UserRequestPo req : groupPo.getRequests()) {

                GetUserRequestTreeVo reqNode = new GetUserRequestTreeVo();
                reqNode.setId(req.getId());
                reqNode.setParentId(groupPo.getId());
                reqNode.setType(1);
                reqNode.setName(req.getName());
                if (req.getOriginalRequest() != null && StringUtils.isNotBlank(req.getOriginalRequest().getMethod())) {
                    reqNode.setMethod(req.getOriginalRequest().getMethod());
                }

                //处理原始请求
                if (req.getOriginalRequest() == null) {
                    reqNode.setLinkForOriginalRequest(0);
                }
                if (req.getOriginalRequest() != null) {
                    reqNode.setLinkForOriginalRequest(1);
                }

                groupNode.getChildren().add(reqNode);
            }
        }

        //处理不在组中的请求
        List<UserRequestPo> notInGroupRequests = repository.getNotInGroupUserRequestList(authService.requireUser().getId());

        for (UserRequestPo req : notInGroupRequests) {
            GetUserRequestTreeVo reqNode = new GetUserRequestTreeVo();
            reqNode.setId(req.getId());
            reqNode.setType(1);
            reqNode.setName(req.getName());
            if (req.getOriginalRequest() != null && StringUtils.isNotBlank(req.getOriginalRequest().getMethod())) {
                reqNode.setMethod(req.getOriginalRequest().getMethod());
            }

            treeList.add(reqNode);
        }

        // 关键字过滤（可选）
        String keyword = dto != null ? dto.getKeyword() : null;
        if (StringUtils.isBlank(keyword)) {
            return treeList;
        }

        List<GetUserRequestTreeVo> filtered = new ArrayList<>();
        for (GetUserRequestTreeVo node : treeList) {
            if (filterTree(node, keyword)) {
                filtered.add(node);
            }
        }

        //处理空子级
        for (GetUserRequestTreeVo node : filtered) {
            if (node.getChildren() == null) {
                node.setChildren(new ArrayList<>());
            }
        }

        return filtered;
    }

    /**
     * 编辑用户请求树
     *
     * @param dto 编辑请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void editUserRequestTree(EditUserRequestTreeDto dto) throws BizException, AuthException {

        //判断树对象类型 0:请求组 1:用户请求
        if (dto.getType() == 0) {
            //处理请求组

            UserRequestGroupPo groupPo = userRequestGroupRepository.getRequestGroupByIdAndUserId(dto.getId(), AuthService.requireUserId());

            if (groupPo == null) {
                throw new BizException("数据不存在或无权限操作.");
            }

            UserRequestGroupPo parentPo = null;

            if (dto.getParentId() != null) {
                parentPo = userRequestGroupRepository.getRequestGroupByIdAndUserId(dto.getParentId(), AuthService.requireUserId());

                if (parentPo == null) {
                    throw new BizException("父级数据不存在或无权限操作.");
                }
            }

            groupPo.setParent(parentPo);
            groupPo.setName(dto.getName());
            groupPo.setSeq(dto.getSeq());


            userRequestGroupRepository.save(groupPo);
            return;
        }

        //处理请求对象
        UserRequestPo userRequestPo = repository.getByIdAndUserId(dto.getId(), AuthService.requireUserId());

        if (userRequestPo == null) {
            throw new BizException("数据不存在或无权限操作.");
        }

        UserRequestGroupPo parentPo = null;

        if (dto.getParentId() != null) {
            parentPo = userRequestGroupRepository.getRequestGroupByIdAndUserId(dto.getParentId(), AuthService.requireUserId());

            if (parentPo == null) {
                throw new BizException("父级数据不存在或无权限操作.");
            }
        }

        userRequestPo.setGroup(parentPo);
        userRequestPo.setName(dto.getName());
        repository.save(userRequestPo);
    }

    public void copyUserRequest(CommonIdDto dto) throws BizException, AuthException {

        UserRequestPo userRequestPo = repository.getByIdAndUserId(dto.getId(), AuthService.requireUserId());

        if (userRequestPo == null) {
            throw new BizException("数据不存在或无权限操作.");
        }

        UserRequestPo copyPo = new UserRequestPo();
        copyPo.setGroup(userRequestPo.getGroup());
        copyPo.setOriginalRequest(userRequestPo.getOriginalRequest());
        copyPo.setUser(userRequestPo.getUser());
        copyPo.setName(userRequestPo.getName());
        copyPo.setMethod(userRequestPo.getMethod());
        copyPo.setUrl(userRequestPo.getUrl());
        copyPo.setRequestHeaders(userRequestPo.getRequestHeaders());
        copyPo.setRequestBodyType(userRequestPo.getRequestBodyType());
        copyPo.setRequestBody(userRequestPo.getRequestBody());
        repository.save(copyPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editUserRequest(EditUserRequestDto dto) throws BizException, AuthException {

        UserRequestPo updatePo = repository.getByIdAndUserId(dto.getId(), AuthService.requireUserId());

        if (updatePo == null) {
            throw new BizException("数据不存在或无权限操作.");
        }

        updatePo.setName(dto.getName());
        updatePo.setMethod(dto.getMethod());
        updatePo.setUrl(dto.getUrl());
        updatePo.setRequestHeaders(gson.toJson(dto.getRequestHeaders()));
        updatePo.setRequestBodyType(dto.getRequestBodyType());
        updatePo.setRequestBody(dto.getRequestBody());
        repository.save(updatePo);
    }


    public GetUserRequestDetailsVo getUserRequestDetails(CommonIdDto dto) throws BizException {
        UserRequestPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询失败,数据不存在."));

        GetUserRequestDetailsVo vo = as(po, GetUserRequestDetailsVo.class);

        if (po.getRequestHeaders() != null) {
            Type listType = new TypeToken<List<HttpHeaderVo>>() {
            }.getType();
            vo.setRequestHeaders(gson.fromJson(po.getRequestHeaders(), listType));
        }

        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeUserRequest(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

    private boolean filterTree(GetUserRequestTreeVo node, String keyword) {
        if (node == null) {
            return false;
        }
        boolean selfMatch = false;
        if (StringUtils.isNotBlank(node.getName()) && StringUtils.containsIgnoreCase(node.getName(), keyword)) {
            selfMatch = true;
        }
        if (!selfMatch && StringUtils.isNotBlank(node.getMethod()) && StringUtils.containsIgnoreCase(node.getMethod(), keyword)) {
            selfMatch = true;
        }

        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            return selfMatch;
        }

        List<GetUserRequestTreeVo> keptChildren = new ArrayList<>();
        for (GetUserRequestTreeVo child : node.getChildren()) {
            if (filterTree(child, keyword)) {
                keptChildren.add(child);
            }
        }
        node.setChildren(keptChildren);
        if (selfMatch) {
            return true;
        }
        return !keptChildren.isEmpty();
    }

    /**
     * 删除用户请求树对象
     *
     * @param dto 删除请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRequestTree(RemoveUserRequestTreeDto dto) throws BizException, AuthException {

        //判断对象类型 0:请求组 1:用户请求
        if (dto.getType() == 0) {
            //处理请求组
            UserRequestGroupPo groupPo = userRequestGroupRepository.getRequestGroupByIdAndUserId(dto.getId(), AuthService.requireUserId());
            if (groupPo == null) {
                throw new BizException("数据不存在或无权限操作.");
            }
            userRequestGroupRepository.delete(groupPo);
            return;
        }

        //处理用户请求
        UserRequestPo userRequestPo = repository.getByIdAndUserId(dto.getId(), AuthService.requireUserId());
        if (userRequestPo == null) {
            throw new BizException("数据不存在或无权限操作.");
        }

        repository.delete(userRequestPo);
    }

    public void sendUserRequest(CommonIdDto dto) throws BizException, AuthException {

        UserPo userPo = authService.requireUser();
        UserRequestPo userRequestPo = repository.getByIdAndUserId(dto.getId(), userPo.getId());

        if (userRequestPo == null) {
            throw new BizException("数据不存在或无权限操作.");
        }

        //创建请求Schema
        RequestSchema requestSchema = new RequestSchema();
        requestSchema.setUrl(userRequestPo.getUrl());
        requestSchema.setMethod(userRequestPo.getMethod());
        requestSchema.parserHeaderFromJson(userRequestPo.getRequestHeaders());
        requestSchema.setBody(userRequestPo.getRequestBody().getBytes(StandardCharsets.UTF_8));
        requestSchema.setContentType(userRequestPo.getRequestBodyType());

        if (!requestSchema.getContentType().contains("application/json")) {
            throw new BizException("只能发送application/json请求");
        }

        HttpClient client = HttpClient.newHttpClient();


        //执行请求过滤器
        userRequestFilterService.doRequestFilter(requestSchema, userRequestPo);

        //创建重放记录
        UserRequestLogPo userRequestLogPo = new UserRequestLogPo();
        userRequestLogPo.setUserRequest(userRequestPo);
        userRequestLogPo.setRequestId(UUID.randomUUID().toString());
        userRequestLogPo.setMethod(requestSchema.getMethod());
        userRequestLogPo.setUrl(requestSchema.getUrl());
        userRequestLogPo.setSource("EAS服务");
        userRequestLogPo.setRequestHeaders(gson.toJson(requestSchema.getHeaders()));
        userRequestLogPo.setRequestBodyLength(requestSchema.getBody().length);
        userRequestLogPo.setRequestBodyType(requestSchema.getContentType());
        userRequestLogPo.setRequestBody(new String(requestSchema.getBody(), StandardCharsets.UTF_8));
        userRequestLogPo.setResponseHeaders("[]");
        userRequestLogPo.setResponseBodyLength(0);
        userRequestLogPo.setResponseBodyType("UNKNOW");
        userRequestLogPo.setResponseBody("{}");
        userRequestLogPo.setStatusCode(-1); //-1为请求失败
        userRequestLogPo.setRedirectUrl(null);
        userRequestLogPo.setStatus(3); //0:正常 1:HTTP失败 2:业务失败 3:连接超时
        userRequestLogPo.setRequestTime(LocalDateTime.now());
        userRequestLogPo.setResponseTime(null);


        try {
            //发送请求
            HttpResponse<String> response = client.send(requestSchema.getBuilder().build(), HttpResponse.BodyHandlers.ofString());

            //解析响应
            String responseBody = response.body();
            userRequestLogPo.setResponseBodyLength(responseBody.length());
            userRequestLogPo.setResponseBodyType(response.headers().firstValue("Content-Type").orElse(""));
            userRequestLogPo.setResponseBody(responseBody);
            userRequestLogPo.setStatusCode(response.statusCode());
            userRequestLogPo.setRedirectUrl(response.headers().firstValue("Location").orElse(null));
            userRequestLogPo.setStatus(0);
            userRequestLogPo.setResponseTime(LocalDateTime.now());


            //处理响应头
            List<HttpHeaderVo> responseHeadersList = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : response.headers().map().entrySet()) {
                HttpHeaderVo responseHeaderVo = new HttpHeaderVo();
                responseHeaderVo.setK(entry.getKey());
                responseHeaderVo.setV(entry.getValue().get(0));
                responseHeadersList.add(responseHeaderVo);
            }

            //处理响应过滤器
            userRequestFilterService.doResponseFilter(response, userRequestPo, userRequestLogPo);

        } catch (IOException | InterruptedException e) {
            userRequestLogRepository.save(userRequestLogPo);
            throw new BizException("发送请求失败", e);
        } finally {
            //保存用户请求记录
            userRequestLogRepository.save(userRequestLogPo);
        }

    }

}