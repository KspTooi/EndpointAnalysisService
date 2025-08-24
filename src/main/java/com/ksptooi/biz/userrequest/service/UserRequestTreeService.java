package com.ksptooi.biz.userrequest.service;

import com.ksptooi.biz.user.service.AuthService;
import com.ksptooi.biz.userrequest.model.userrequest.RemoveUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequest.UserRequestPo;
import com.ksptooi.biz.userrequest.model.userrequestgroup.UserRequestGroupPo;
import com.ksptooi.biz.userrequest.model.userrequesttree.UserRequestTreePo;
import com.ksptooi.biz.userrequest.model.userrequesttree.dto.AddUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequesttree.dto.GetUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequesttree.dto.MoveUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequesttree.vo.GetUserRequestTreeVo;
import com.ksptooi.biz.userrequest.repository.UserRequestGroupRepository;
import com.ksptooi.biz.userrequest.repository.UserRequestRepository;
import com.ksptooi.biz.userrequest.repository.UserRequestTreeRepository;
import com.ksptooi.commons.exception.BizException;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserRequestTreeService {

    @Autowired
    private UserRequestTreeRepository userRequestTreeRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRequestGroupRepository userRequestGroupRepository;

    @Autowired
    private UserRequestRepository userRequestRepository;


    /**
     * 获取用户请求树
     *
     * @param dto 参数
     * @return 用户请求树
     * @throws AuthException 认证异常
     */
    public List<GetUserRequestTreeVo> getUserRequestTree(GetUserRequestTreeDto dto) throws AuthException {

        Long userId = AuthService.requireUserId();

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
    public void addUserRequestTree(AddUserRequestTreeDto dto) throws BizException, AuthException {

        UserRequestTreePo parentPo = null;

        // 如果父级ID不为空，则查询父级ID
        if (dto.getParentId() != null) {
            parentPo = userRequestTreeRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new BizException("父级ID不存在"));
        }

        UserRequestTreePo userRequestTreePo = new UserRequestTreePo();
        userRequestTreePo.setUser(authService.requireUser());
        userRequestTreePo.setParent(parentPo);
        userRequestTreePo.setName(dto.getName());
        userRequestTreePo.setKind(dto.getKind());
        userRequestTreePo.setSeq(userRequestTreeRepository.getMaxSeqInParent(dto.getParentId()));

        //KIND 0:请求组 1:用户请求

        //挂载空请求组
        if (dto.getKind() == 0) {
            UserRequestGroupPo userRequestGroupPo = new UserRequestGroupPo();
            userRequestGroupPo.setTree(userRequestTreePo);
            userRequestGroupPo.setUser(authService.requireUser());
            userRequestGroupPo.setParent(null);
            userRequestGroupPo.setName(dto.getName());
            userRequestGroupPo.setDescription(null);
            userRequestGroupPo.setSeq(0);
            userRequestTreePo.setGroup(userRequestGroupPo);
        }
        //挂载空用户请求
        if (dto.getKind() == 1) {
            UserRequestPo userRequestPo = new UserRequestPo();
            userRequestPo.setTree(userRequestTreePo);
            userRequestPo.setGroup(null);
            userRequestPo.setOriginalRequest(null);
            userRequestPo.setUser(authService.requireUser());
            userRequestPo.setName(dto.getName());
            userRequestPo.setMethod("POST");
            userRequestPo.setUrl("/");
            userRequestPo.setRequestHeaders("{}");
            userRequestPo.setRequestBodyType("application/json;charset=utf-8");
            userRequestPo.setRequestBody("{}");
            userRequestPo.setSeq(0);
            userRequestTreePo.setRequest(userRequestPo);
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
    public List<GetUserRequestTreeVo> moveUserRequestTree(MoveUserRequestTreeDto dto) throws BizException, AuthException {

        Long userId = AuthService.requireUserId();

        UserRequestTreePo nodePo = userRequestTreeRepository.getNodeByIdAndUserId(dto.getNodeId(), userId);

        if (nodePo == null) {
            throw new BizException("对象节点不存在或无权限访问");
        }

        //构造查询DTO
        GetUserRequestTreeDto queryDto = new GetUserRequestTreeDto();
        queryDto.setKeyword(dto.getKeyword());

        //移动到顶层
        if (dto.getTargetId() == null) {
            nodePo.setParent(null);
            nodePo.setSeq(userRequestTreeRepository.getMaxSeqInParent(null));
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
                userRequestTreeRepository.save(nodePo);
                return getUserRequestTree(queryDto);
            }

        }

        return getUserRequestTree(queryDto);
    }


    /**
     * 移除用户请求树对象
     *
     * @param dto 参数
     */
    public void removeUserRequestTree(RemoveUserRequestTreeDto dto) throws BizException, AuthException {

        Long userId = AuthService.requireUserId();

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
