package com.ksptooi.biz.userrequest.service;

import com.ksptooi.biz.core.model.request.RequestPo;
import com.ksptooi.biz.core.repository.RequestRepository;
import com.ksptooi.biz.user.service.AuthService;
import com.ksptooi.biz.userrequest.model.userrequest.*;
import com.ksptooi.biz.userrequest.model.userrequestgroup.UserRequestGroupPo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import org.springframework.transaction.annotation.Transactional;

import jakarta.security.auth.message.AuthException;

import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.biz.userrequest.repository.UserRequestRepository;
import com.ksptooi.biz.userrequest.repository.UserRequestGroupRepository;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

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

    /**
     * 保存原始请求为用户请求
     * @param dto 保存请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAsUserRequest(SaveAsUserRequestDto dto) throws BizException,AuthException{

        //查询原始请求
        RequestPo requestPo = requestRepository.findById(dto.getRequestId())
                .orElseThrow(()-> new BizException("原始请求不存在."));

        //创建用户请求
        UserRequestPo userRequestPo = new UserRequestPo();
        userRequestPo.setGroup(null);
        userRequestPo.setOriginalRequest(requestPo);
        userRequestPo.setUser(authService.requireUser());
        userRequestPo.setName(requestPo.getRequestId());//如果未提供名称 则使用请求ID作为名称

        if(StringUtils.isNotBlank(dto.getName())){
            userRequestPo.setName(dto.getName());
        }

        userRequestPo.setSeq(repository.getNextSeq(AuthService.requireUserId(),0));
        repository.save(userRequestPo);
    }


    /**
     * 获取用户请求树
     * @param dto 获取请求参数
     * @return 用户请求树
     */
    public List<GetUserRequestTreeVo> getUserRequestTree(GetUserRequestTreeDto dto) throws AuthException {

        //先获取请求组
        List<UserRequestGroupPo> groupList = userRequestGroupRepository.getUserRequestGroupWithRequests(AuthService.requireUserId());
        List<GetUserRequestTreeVo> treeList = new ArrayList<>();

        //将请求组转换为树形结构
        if(groupList == null || groupList.isEmpty()){
            return treeList;
        }

        Map<Long, GetUserRequestTreeVo> groupNodeMap = new HashMap<>();

        // 第一遍：创建组节点
        for (UserRequestGroupPo groupPo : groupList) {
            GetUserRequestTreeVo groupNode = new GetUserRequestTreeVo();
            groupNode.setId(groupPo.getId());
            groupNode.setParentId(null);

            if(groupPo.getParent()!=null){
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
            if (groupPo.getRequests() == null || groupPo.getRequests().isEmpty()){
                continue;
            }
            for (UserRequestPo req : groupPo.getRequests()) {
                GetUserRequestTreeVo reqNode = new GetUserRequestTreeVo();
                reqNode.setId(req.getId());
                reqNode.setParentId(groupPo.getId());
                reqNode.setType(1);
                reqNode.setName(req.getName());
                if (req.getOriginalRequest() != null && StringUtils.isNotBlank(req.getOriginalRequest().getMethod())){
                    reqNode.setMethod(req.getOriginalRequest().getMethod());
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
            if (req.getOriginalRequest() != null && StringUtils.isNotBlank(req.getOriginalRequest().getMethod())){
                reqNode.setMethod(req.getOriginalRequest().getMethod());
            }
            treeList.add(reqNode);
        }

        // 关键字过滤（可选）
        String keyword = dto != null ? dto.getKeyword() : null;
        if (StringUtils.isBlank(keyword)){
            return treeList;
        }

        List<GetUserRequestTreeVo> filtered = new ArrayList<>();
        for (GetUserRequestTreeVo node : treeList) {
            if (filterTree(node, keyword)){
                filtered.add(node);
            }
        }

        //处理空子级
        for (GetUserRequestTreeVo node : filtered) {
            if (node.getChildren() == null){
                node.setChildren(new ArrayList<>());
            }
        }

        return filtered;
    }

    /**
     * 编辑用户请求树
     * @param dto 编辑请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void editUserRequestTree(EditUserRequestTreeDto dto) throws BizException,AuthException {
        
        //判断树对象类型 0:请求组 1:用户请求
        if(dto.getType() == 0){
            //处理请求组

            UserRequestGroupPo groupPo = userRequestGroupRepository.getRequestGroupByIdAndUserId(dto.getId(),AuthService.requireUserId());

            if(groupPo == null){
                throw new BizException("数据不存在或无权限操作.");
            }

            UserRequestGroupPo parentPo = null;

            if(dto.getParentId() != null){
                parentPo = userRequestGroupRepository.getRequestGroupByIdAndUserId(dto.getParentId(),AuthService.requireUserId());

                if(parentPo == null){
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
        UserRequestPo userRequestPo = repository.getByIdAndUserId(dto.getId(),AuthService.requireUserId());

        if(userRequestPo == null){
            throw new BizException("数据不存在或无权限操作.");
        }

        UserRequestGroupPo parentPo = null;

        if(dto.getParentId() != null){
            parentPo = userRequestGroupRepository.getRequestGroupByIdAndUserId(dto.getParentId(),AuthService.requireUserId());

            if(parentPo == null){
                throw new BizException("父级数据不存在或无权限操作.");
            }
        }

        userRequestPo.setGroup(parentPo);
        userRequestPo.setName(dto.getName());
        userRequestPo.setSeq(dto.getSeq());
        repository.save(userRequestPo);
    }



    @Transactional(rollbackFor = Exception.class)
    public void editUserRequest(EditUserRequestDto dto) throws BizException {
        UserRequestPo updatePo = repository.findById(dto.getId())
                .orElseThrow(()-> new BizException("更新失败,数据不存在."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetUserRequestDetailsVo getUserRequestDetails(CommonIdDto dto) throws BizException {
        UserRequestPo po = repository.findById(dto.getId())
                .orElseThrow(()-> new BizException("更新失败,数据不存在."));
        return as(po,GetUserRequestDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeUserRequest(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if(!dto.isBatch()){
            repository.deleteById(dto.getId());
        }
    }

    private boolean filterTree(GetUserRequestTreeVo node, String keyword){
        if (node == null){
            return false;
        }
        boolean selfMatch = false;
        if (StringUtils.isNotBlank(node.getName()) && StringUtils.containsIgnoreCase(node.getName(), keyword)){
            selfMatch = true;
        }
        if (!selfMatch && StringUtils.isNotBlank(node.getMethod()) && StringUtils.containsIgnoreCase(node.getMethod(), keyword)){
            selfMatch = true;
        }

        if (node.getChildren() == null || node.getChildren().isEmpty()){
            return selfMatch;
        }

        List<GetUserRequestTreeVo> keptChildren = new ArrayList<>();
        for (GetUserRequestTreeVo child : node.getChildren()){
            if (filterTree(child, keyword)){
                keptChildren.add(child);
            }
        }
        node.setChildren(keptChildren);
        if (selfMatch){
            return true;
        }
        return !keptChildren.isEmpty();
    }

    /**
     * 删除用户请求树对象
     * @param dto 删除请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRequestTree(RemoveUserRequestTreeDto dto) throws BizException,AuthException {

        //判断对象类型 0:请求组 1:用户请求
        if(dto.getType() == 0){
            //处理请求组
            UserRequestGroupPo groupPo = userRequestGroupRepository.getRequestGroupByIdAndUserId(dto.getId(),AuthService.requireUserId());
            if(groupPo == null){
                throw new BizException("数据不存在或无权限操作.");
            }
            userRequestGroupRepository.delete(groupPo);
            return;
        }

        //处理用户请求
        UserRequestPo userRequestPo = repository.getByIdAndUserId(dto.getId(),AuthService.requireUserId());
        if(userRequestPo == null){
            throw new BizException("数据不存在或无权限操作.");
        }

        repository.delete(userRequestPo);
    }

}