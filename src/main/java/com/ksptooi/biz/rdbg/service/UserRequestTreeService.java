package com.ksptooi.biz.rdbg.service;

import com.ksptooi.biz.core.service.SessionService;
import com.ksptooi.biz.rdbg.model.userrequest.RemoveUserRequestTreeDto;
import com.ksptooi.biz.rdbg.model.userrequest.UserRequestPo;
import com.ksptooi.biz.rdbg.model.userrequestgroup.UserRequestGroupPo;
import com.ksptooi.biz.rdbg.model.userrequesttree.UserRequestTreePo;
import com.ksptooi.biz.rdbg.model.userrequesttree.dto.AddUserRequestTreeDto;
import com.ksptooi.biz.rdbg.model.userrequesttree.dto.EditUserRequestTreeDto;
import com.ksptooi.biz.rdbg.model.userrequesttree.dto.GetUserRequestTreeDto;
import com.ksptooi.biz.rdbg.model.userrequesttree.dto.MoveUserRequestTreeDto;
import com.ksptooi.biz.rdbg.model.userrequesttree.vo.GetUserRequestTreeVo;
import com.ksptooi.biz.rdbg.repository.UserRequestGroupRepository;
import com.ksptooi.biz.rdbg.repository.UserRequestRepository;
import com.ksptooi.biz.rdbg.repository.UserRequestTreeRepository;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ksptooi.biz.core.service.SessionService.session;


@Service
public class UserRequestTreeService {

    @Autowired
    private UserRequestTreeRepository userRequestTreeRepository;

    @Autowired
    private UserRequestGroupRepository userRequestGroupRepository;

    @Autowired
    private UserRequestRepository userRequestRepository;
    @Autowired
    private SessionService sessionService;


    /**
     * 获取用户请求树
     *
     * @param dto 参数
     * @return 用户请求树
     * @throws AuthException 认证异常
     */
    public List<GetUserRequestTreeVo> getUserRequestTree(GetUserRequestTreeDto dto) throws AuthException {

        Long userId = session().getUserId();

        //获取该用户所拥有的全部树节点
        List<UserRequestTreePo> nodePos = userRequestTreeRepository.getRequestTreeListByUserId(userId);

        //平面节点
        List<GetUserRequestTreeVo> flatNodeVos = new ArrayList<>();

        //先将PO处理为平面节点
        for (UserRequestTreePo item : nodePos) {
            GetUserRequestTreeVo vo = new GetUserRequestTreeVo();
            vo.setId(item.getId());
            vo.setRequestId(null);
            vo.setGroupId(null);
            vo.setParentId(null);
            vo.setType(item.getKind());
            vo.setName(item.getName());
            vo.setMethod(null);
            vo.setSimpleFilterCount(0);
            vo.setLinkForOriginalRequest(0);
            vo.setChildren(new ArrayList<>());

            //如果有父节点则获取父节点ID
            if (item.getParent() != null) {
                vo.setParentId(item.getParent().getId());
            }

            //如果是请求则获取请求方法 0:组 1:请求
            if (item.getKind() == 1) {
                vo.setMethod(item.getRequest().getMethod());

                if (item.getRequest().getOriginalRequest() != null) {
                    vo.setLinkForOriginalRequest(1);
                }

                vo.setRequestId(item.getRequest().getId());
            }

            //如果是组则获取组上的过滤器数量
            if (item.getKind() == 0) {
                vo.setSimpleFilterCount(item.getGroup().getFilters().size());
                vo.setGroupId(item.getGroup().getId());
            }

            flatNodeVos.add(vo);
        }

        //将平面节点转换为树结构
        Map<Long, GetUserRequestTreeVo> idToNode = new HashMap<>();
        for (GetUserRequestTreeVo node : flatNodeVos) {
            idToNode.put(node.getId(), node);
        }

        List<GetUserRequestTreeVo> treeNodeVos = new ArrayList<>();
        for (GetUserRequestTreeVo node : flatNodeVos) {
            Long parentId = node.getParentId();
            if (parentId == null) {
                treeNodeVos.add(node);
                continue;
            }
            GetUserRequestTreeVo parent = idToNode.get(parentId);
            if (parent == null) {
                treeNodeVos.add(node);
                continue;
            }
            parent.getChildren().add(node);
        }

        return treeNodeVos;
    }


    /**
     * 新增用户请求树
     *
     * @param dto 参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUserRequestTree(AddUserRequestTreeDto dto) throws BizException, AuthException {

        UserRequestTreePo parentPo = null;

        // 如果父级ID不为空，则查询父级ID
        if (dto.getParentId() != null) {
            parentPo = userRequestTreeRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new BizException("父级ID不存在"));
        }

        UserRequestTreePo userRequestTreePo = new UserRequestTreePo();
        userRequestTreePo.setUser(sessionService.requireUser());
        userRequestTreePo.setParent(parentPo);
        userRequestTreePo.setName(dto.getName());
        userRequestTreePo.setKind(dto.getKind());
        userRequestTreePo.setSeq(userRequestTreeRepository.getMaxSeqInParent(dto.getParentId()));

        //KIND 0:请求组 1:用户请求

        //挂载空请求组
        if (dto.getKind() == 0) {
            UserRequestGroupPo userRequestGroupPo = new UserRequestGroupPo();
            userRequestGroupPo.setTree(userRequestTreePo);
            userRequestGroupPo.setUser(sessionService.requireUser());
            userRequestGroupPo.setName(dto.getName());
            userRequestGroupPo.setDescription(null);
            userRequestTreePo.setGroup(userRequestGroupPo);

            //先保存请求
            var saveGroup = userRequestGroupRepository.save(userRequestGroupPo);
            userRequestTreePo.setGroupId(saveGroup.getId());
        }
        //挂载空用户请求
        if (dto.getKind() == 1) {
            UserRequestPo userRequestPo = new UserRequestPo();
            userRequestPo.setTree(userRequestTreePo);
            userRequestPo.setGroup(null);
            userRequestPo.setOriginalRequest(null);
            userRequestPo.setUser(sessionService.requireUser());
            userRequestPo.setName(dto.getName());
            userRequestPo.setMethod("POST");
            userRequestPo.setUrl("/");
            userRequestPo.setRequestHeaders("{}");
            userRequestPo.setRequestBodyType("application/json;charset=utf-8");
            userRequestPo.setRequestBody("{}");
            userRequestTreePo.setRequest(userRequestPo);

            //先保存请求
            var saveRequest = userRequestRepository.save(userRequestPo);
            userRequestTreePo.setRequestId(saveRequest.getId());
        }

        userRequestTreeRepository.save(userRequestTreePo);
    }

    /**
     * 移动用户请求树
     *
     * @param dto 参数
     * @return 移动成功以后返回新的树结构
     * @throws AuthException 认证异常
     */
    @Transactional(rollbackFor = Exception.class)
    public List<GetUserRequestTreeVo> moveUserRequestTree(MoveUserRequestTreeDto dto) throws BizException, AuthException {

        Long userId = session().getUserId();

        UserRequestTreePo nodePo = userRequestTreeRepository.getNodeByIdAndUserId(dto.getNodeId(), userId);

        if (nodePo == null) {
            throw new BizException("对象节点不存在或无权限访问");
        }
        //同步处理请求
        UserRequestPo requestPo = nodePo.getRequest();

        //构造查询DTO
        GetUserRequestTreeDto queryDto = new GetUserRequestTreeDto();
        queryDto.setKeyword(dto.getKeyword());

        //移动到顶层
        if (dto.getTargetId() == null) {
            nodePo.setParent(null);
            nodePo.setSeq(userRequestTreeRepository.getMaxSeqInParent(null));

            if (requestPo != null) {
                requestPo.setGroup(null);
            }

            userRequestTreeRepository.save(nodePo);
            return getUserRequestTree(queryDto);
        }

        //移动到目标节点
        UserRequestTreePo targetNodePo = userRequestTreeRepository.getNodeByIdAndUserId(dto.getTargetId(), userId);

        if (targetNodePo == null) {
            throw new BizException("目标节点不存在或无权限访问");
        }

        Integer targetSeq = targetNodePo.getSeq();

        //处理移动方式 0:顶部 1:底部 2:内部
        if (dto.getKind() == 0) {
            nodePo.setSeq(targetSeq);

            /*
             * 处理目标节点让位
             * 让位逻辑: 目标节点同级节点下所有节点SEQ+1
             */

            //获取目标节点的父级 如果是NULL则顶级节点让位
            UserRequestTreePo targetParentNodePo = targetNodePo.getParent();

            //将当前正在移动的节点父级设置为目标节点的父级
            nodePo.setParent(targetParentNodePo);

            //处理请求挂载
            if (requestPo != null) {
                //如果目标节点的父级不为空 则需将请求挂载到目标节点的父级
                if (targetParentNodePo != null) {
                    var group = targetParentNodePo.getGroup();
                    if (group != null) {
                        requestPo.setGroup(group);
                    }
                }
                if (targetParentNodePo == null) {
                    requestPo.setGroup(null);
                }
            }

            //处理顶级节点让位
            if (targetParentNodePo == null) {
                List<UserRequestTreePo> rootNodeList = userRequestTreeRepository.getRootNodeListByUserId(userId);
                for (UserRequestTreePo item : rootNodeList) {
                    if (item.getSeq() >= targetSeq) {

                        //跳过当前正在移动的节点
                        if (Objects.equals(item.getId(), nodePo.getId())) {
                            continue;
                        }

                        item.setSeq(item.getSeq() + 1);
                    }
                }
                userRequestTreeRepository.saveAll(rootNodeList);
                userRequestTreeRepository.save(nodePo);
                return getUserRequestTree(queryDto);
            }

            //处理内部节点让位
            List<UserRequestTreePo> targetParentNodeList = targetParentNodePo.getChildren();
            for (UserRequestTreePo item : targetParentNodeList) {
                if (item.getSeq() >= targetSeq) {

                    //跳过当前正在移动的节点
                    if (Objects.equals(item.getId(), nodePo.getId())) {
                        continue;
                    }

                    item.setSeq(item.getSeq() + 1);
                }
            }
            userRequestTreeRepository.saveAll(targetParentNodeList);
            userRequestTreeRepository.save(nodePo);
            return getUserRequestTree(queryDto);
        }

        //移动到目标节点底部
        if (dto.getKind() == 1) {
            nodePo.setSeq(targetSeq);

            /*
             * 处理目标节点让位
             * 让位逻辑: 目标节点同级节点下所有节点SEQ - 1(向前移动)
             */
            //获取目标节点的父级 如果是NULL则顶级节点让位
            UserRequestTreePo targetParentNodePo = targetNodePo.getParent();

            //将当前正在移动的节点父级设置为目标节点的父级
            nodePo.setParent(targetParentNodePo);

            //处理请求挂载
            if (requestPo != null) {
                if (targetParentNodePo != null) {
                    var group = targetParentNodePo.getGroup();
                    if (group != null) {
                        requestPo.setGroup(group);
                    }
                }
                if (targetParentNodePo == null) {
                    requestPo.setGroup(null);
                }
            }

            //处理顶级节点让位
            if (targetParentNodePo == null) {
                List<UserRequestTreePo> rootNodeList = userRequestTreeRepository.getRootNodeListByUserId(userId);
                for (UserRequestTreePo item : rootNodeList) {
                    if (item.getSeq() <= targetSeq) {

                        //跳过当前正在移动的节点
                        if (Objects.equals(item.getId(), nodePo.getId())) {
                            continue;
                        }

                        item.setSeq(item.getSeq() - 1);
                    }
                }
                userRequestTreeRepository.saveAll(rootNodeList);
                userRequestTreeRepository.save(nodePo);
                return getUserRequestTree(queryDto);
            }

            //处理内部节点让位
            List<UserRequestTreePo> targetParentNodeList = targetParentNodePo.getChildren();
            for (UserRequestTreePo item : targetParentNodeList) {
                if (item.getSeq() <= targetSeq) {

                    //跳过当前正在移动的节点
                    if (Objects.equals(item.getId(), nodePo.getId())) {
                        continue;
                    }

                    item.setSeq(item.getSeq() - 1);
                }
            }
            userRequestTreeRepository.saveAll(targetParentNodeList);
            userRequestTreeRepository.save(nodePo);
            return getUserRequestTree(queryDto);
        }

        //移动到目标节点内部
        if (dto.getKind() == 2) {

            //目标节点不是组则直接移动到下一个位置 并处理让位
            if (targetNodePo.getKind() == 1) {
                nodePo.setSeq(targetSeq);


                //获取目标节点的父级 如果是NULL则顶级节点让位
                UserRequestTreePo targetParentNodePo = targetNodePo.getParent();

                //将当前正在移动的节点父级设置为目标节点的父级
                nodePo.setParent(targetParentNodePo);

                //处理请求挂载
                if (requestPo != null) {
                    if (targetParentNodePo != null) {
                        var group = targetParentNodePo.getGroup();
                        if (group != null) {
                            requestPo.setGroup(group);
                        }
                    }
                    if (targetParentNodePo == null) {
                        requestPo.setGroup(null);
                    }
                }

                //处理顶级节点让位
                if (targetParentNodePo == null) {
                    List<UserRequestTreePo> rootNodeList = userRequestTreeRepository.getRootNodeListByUserId(userId);
                    for (UserRequestTreePo item : rootNodeList) {
                        if (item.getSeq() >= targetSeq) {
                            item.setSeq(item.getSeq() + 1);
                        }
                    }
                    userRequestTreeRepository.saveAll(rootNodeList);
                    userRequestTreeRepository.save(nodePo);
                    return getUserRequestTree(queryDto);
                }

                //处理内部节点让位
                List<UserRequestTreePo> targetParentNodeList = targetParentNodePo.getChildren();
                for (UserRequestTreePo item : targetParentNodeList) {
                    if (item.getSeq() >= targetSeq) {

                        //跳过当前正在移动的节点
                        if (Objects.equals(item.getId(), nodePo.getId())) {
                            continue;
                        }

                        item.setSeq(item.getSeq() + 1);
                    }
                }
                userRequestTreeRepository.saveAll(targetParentNodeList);
                userRequestTreeRepository.save(nodePo);
                return getUserRequestTree(queryDto);

            }

            //目标节点是组则移动到组中最后一个位置
            if (targetNodePo.getKind() == 0) {
                nodePo.setParent(targetNodePo);
                nodePo.setSeq(userRequestTreeRepository.getMaxSeqInParent(targetNodePo.getId()));

                //处理请求挂载
                if (requestPo != null) {
                    var group = targetNodePo.getGroup();
                    requestPo.setGroup(group);
                }

                userRequestTreeRepository.save(nodePo);
                return getUserRequestTree(queryDto);
            }

        }

        return getUserRequestTree(queryDto);
    }

    /**
     * 复制用户请求树
     *
     * @param dto 参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void copyUserRequestTree(CommonIdDto dto) throws BizException, AuthException {

        Long userId = session().getUserId();

        UserRequestTreePo sourceNodePo = userRequestTreeRepository.getNodeByIdAndUserId(dto.getId(), userId);

        if (sourceNodePo == null) {
            throw new BizException("对象节点不存在或无权限访问");
        }

        /*
         * 处理节点基础信息
         * 新复制的节点排序为当前节点排序+1且父级为当前节点的父级
         * 新复制的节点将插入在被复制节点之后的一个槽位,并且后方所有大于等于 新复制节点SEQ 的节点都需要进行让位处理
         * 让位规则: 节点SEQ >= 新复制节点SEQ 则节点SEQ + 1
         */
        var nodeName = sourceNodePo.getName() + " 副本";

        UserRequestTreePo newNodePo = new UserRequestTreePo();
        newNodePo.setUser(sessionService.requireUser());
        newNodePo.setParent(sourceNodePo.getParent());
        newNodePo.setName(nodeName);
        newNodePo.setKind(sourceNodePo.getKind());
        newNodePo.setSeq(sourceNodePo.getSeq() + 1);

        //处理让位
        var sourceParentNodePo = sourceNodePo.getParent();

        //处理顶层节点让位
        if (sourceParentNodePo == null) {
            List<UserRequestTreePo> rootNodeList = userRequestTreeRepository.getRootNodeListByUserId(userId);
            for (UserRequestTreePo item : rootNodeList) {

                //所有SEQ大于等于新复制节点SEQ的节点都需要进行让位处理
                if (item.getSeq() >= newNodePo.getSeq()) {
                    item.setSeq(item.getSeq() + 1);
                }
            }
            //先更新让位节点
            userRequestTreeRepository.saveAll(rootNodeList);
            //插入新复制的节点
            userRequestTreeRepository.save(newNodePo);
        }

        //处理内部节点让位
        if (sourceParentNodePo != null) {
            List<UserRequestTreePo> sourceParentNodeList = sourceParentNodePo.getChildren();
            for (UserRequestTreePo item : sourceParentNodeList) {
                if (item.getSeq() >= newNodePo.getSeq()) {
                    item.setSeq(item.getSeq() + 1);
                }
            }
            //先更新让位节点
            userRequestTreeRepository.saveAll(sourceParentNodeList);
            //插入新复制的节点
            userRequestTreeRepository.save(newNodePo);
        }

        //处理节点关联请求复制
        if (sourceNodePo.getKind() == 1) {
            UserRequestPo newRequestPo = new UserRequestPo();
            newRequestPo.setTree(newNodePo);

            //如果复制的是请求 应获取该请求上级的组并绑定到新的请求
            UserRequestTreePo parentTree = sourceNodePo.getParent();
            if (parentTree != null) {
                newRequestPo.setGroup(parentTree.getGroup());
            }

            newRequestPo.setUser(sessionService.requireUser());
            newRequestPo.setOriginalRequest(null);
            newRequestPo.setName(nodeName);
            newRequestPo.setMethod(null);
            newRequestPo.setUrl(null);
            newRequestPo.setRequestHeaders(null);
            newRequestPo.setRequestBodyType(null);
            newRequestPo.setRequestBody(null);
            newNodePo.setRequest(newRequestPo);

            var requestPo = sourceNodePo.getRequest();
            var originalRequestPo = requestPo.getOriginalRequest();

            newRequestPo.setMethod(requestPo.getMethod());
            newRequestPo.setUrl(requestPo.getUrl());
            newRequestPo.setRequestHeaders(requestPo.getRequestHeaders());
            newRequestPo.setRequestBodyType(requestPo.getRequestBodyType());
            newRequestPo.setRequestBody(requestPo.getRequestBody());
            if (originalRequestPo != null) {
                newRequestPo.setOriginalRequest(originalRequestPo);
            }

            //保存新请求
            var saveRequest = userRequestRepository.save(newRequestPo);
            newNodePo.setRequestId(saveRequest.getId());
        }

        /*
         * 处理组复制
         * 组复制逻辑:
         * 1.遍历源组下的请求 为每个请求创建新的树节点与请求数据，并将新树节点挂载到新组下
         * 2.遍历源组下关联的过滤器,将过滤器绑定关系复制到新组
         */
        if (sourceNodePo.getKind() == 0) {

            var sourceGroupPo = sourceNodePo.getGroup();

            //为树对象创建组
            var newGroupPo = new UserRequestGroupPo();
            newGroupPo.setTree(newNodePo);
            newGroupPo.setUser(sessionService.requireUser());
            newGroupPo.setName(nodeName);
            newGroupPo.setDescription(sourceGroupPo.getDescription());
            newNodePo.setGroup(newGroupPo);

            //获取源组下的所有对象
            var sourceChildren = sourceNodePo.getChildren();
            var newChildren = new ArrayList<UserRequestTreePo>();

            for (var item : sourceChildren) {

                //跳过对象中的组
                if (item.getKind() == 0) {
                    continue;
                }

                //先为请求创建树对象
                var newRequestTreePo = new UserRequestTreePo();
                newRequestTreePo.setUser(sessionService.requireUser());
                newRequestTreePo.setParent(newNodePo);
                newRequestTreePo.setName(item.getName());
                newRequestTreePo.setKind(1);
                newRequestTreePo.setSeq(item.getSeq());

                var oldRequestPo = item.getRequest();

                if (oldRequestPo == null) {
                    throw new BizException("对象节点所关联的请求不存在 对象ID:" + item.getId());
                }

                //创建请求数据
                var newRequestPo = new UserRequestPo();
                newRequestPo.setTree(newRequestTreePo);
                newRequestPo.setGroup(newGroupPo);
                newRequestPo.setOriginalRequest(oldRequestPo.getOriginalRequest());
                newRequestPo.setUser(sessionService.requireUser());
                newRequestPo.setName(item.getName());
                newRequestPo.setMethod(oldRequestPo.getMethod());
                newRequestPo.setUrl(oldRequestPo.getUrl());
                newRequestPo.setRequestHeaders(oldRequestPo.getRequestHeaders());
                newRequestPo.setRequestBodyType(oldRequestPo.getRequestBodyType());
                newRequestPo.setRequestBody(oldRequestPo.getRequestBody());

                //挂载请求数据
                newRequestTreePo.setRequest(newRequestPo);
                newChildren.add(newRequestTreePo);
            }

            //挂载子节点到新的树对象
            newNodePo.setChildren(newChildren);

            //获取旧组下的所有过滤器并挂载到新创建的组
            var sourceGroupFilters = sourceGroupPo.getFilters();

            //向新组中添加过滤器
            var newGroupFilters = new ArrayList<>(sourceGroupFilters);
            //挂载过滤器
            newGroupPo.setFilters(newGroupFilters);

            //保存新组
            var saveGroup = userRequestGroupRepository.save(newGroupPo);
            newNodePo.setGroupId(saveGroup.getId());
        }

        userRequestTreeRepository.save(newNodePo);
    }

    /**
     * 编辑用户请求树
     *
     * @param dto 参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void editUserRequestTree(EditUserRequestTreeDto dto) throws BizException, AuthException {

        Long userId = session().getUserId();
        UserRequestTreePo nodePo = userRequestTreeRepository.getNodeByIdAndUserId(dto.getId(), userId);

        if (nodePo == null) {
            throw new BizException("对象节点不存在或无权限访问");
        }

        nodePo.setName(dto.getName());

        //如果树节点是组类型 则需要同步更新组信息
        if (nodePo.getKind() == 0) {
            UserRequestGroupPo groupPo = nodePo.getGroup();
            groupPo.setName(dto.getName());
        }

        //如果树节点是请求类型 则需要同步更新请求信息
        if (nodePo.getKind() == 1) {
            UserRequestPo requestPo = nodePo.getRequest();
            requestPo.setName(dto.getName());
        }

        //级联修改
        userRequestTreeRepository.save(nodePo);
    }


    /**
     * 移除用户请求树对象
     *
     * @param dto 参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRequestTree(RemoveUserRequestTreeDto dto) throws BizException, AuthException {

        Long userId = session().getUserId();

        UserRequestTreePo nodePo = userRequestTreeRepository.getNodeByIdAndUserId(dto.getId(), userId);

        if (nodePo == null) {
            throw new BizException("对象节点不存在或无权限访问");
        }

        //有子节点不允许删除
        if (!nodePo.getChildren().isEmpty()) {
            throw new BizException("该节点下有子节点，不允许删除");
        }

        //处理关联组(组与过滤器有多对多关系 则JPA级联删除会同步清除多对多关系)
        if (nodePo.getKind() == 0) {
            UserRequestGroupPo groupPo = nodePo.getGroup();
            userRequestGroupRepository.delete(groupPo);
        }

        //处理关联请求
        if (nodePo.getKind() == 1) {
            UserRequestPo requestPo = nodePo.getRequest();
            userRequestRepository.delete(requestPo);
        }

        //删除节点
        userRequestTreeRepository.delete(nodePo);
    }

}
