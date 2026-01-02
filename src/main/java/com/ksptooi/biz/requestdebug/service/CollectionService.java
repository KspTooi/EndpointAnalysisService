package com.ksptooi.biz.requestdebug.service;

import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.biz.requestdebug.model.collection.CollectionPo;
import com.ksptooi.biz.requestdebug.model.collection.dto.AddCollectionDto;
import com.ksptooi.biz.requestdebug.model.collection.dto.EditCollectionDto;
import com.ksptooi.biz.requestdebug.model.collection.dto.MoveCollectionDto;
import com.ksptooi.biz.requestdebug.model.collection.vo.GetCollectionDetailsVo;
import com.ksptooi.biz.requestdebug.model.collection.vo.GetCollectionTreeVo;
import com.ksptooi.biz.requestdebug.repoistory.CollectionRepository;
import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ksptool.entities.Entities.as;

@Service
public class CollectionService {

    @Autowired
    private CollectionRepository repository;

    /**
     * 获取请求集合树
     *
     * @return 请求集合树
     */
    public List<GetCollectionTreeVo> getCollectionTree() {

        //获取当前公司ID
        Long companyId = AuthService.getCurrentCompanyId();

        //获取公司所拥有的全部请求集合树节点
        List<CollectionPo> nodePos = repository.getCollectionTreeListByCompanyId(companyId);

        List<GetCollectionTreeVo> flatNodeVos = new ArrayList<>();
        List<GetCollectionTreeVo> treeNodeVos = new ArrayList<>();

        //处理PO为平面节点
        for (CollectionPo item : nodePos) {
            GetCollectionTreeVo vo = as(item, GetCollectionTreeVo.class);
            if (item.getParent() != null) {
                vo.setParentId(item.getParent().getId());
            }
            vo.setChildren(new ArrayList<>());
            flatNodeVos.add(vo);
        }

        //将平面节点转换为树结构
        Map<Long, GetCollectionTreeVo> idToNode = new HashMap<>();

        //先将所有节点添加到映射中
        for (GetCollectionTreeVo node : flatNodeVos) {
            idToNode.put(node.getId(), node);
        }

        //构建树结构
        for (GetCollectionTreeVo node : flatNodeVos) {
            Long parentId = node.getParentId();
            if (parentId == null) {
                treeNodeVos.add(node);
                continue;
            }
            GetCollectionTreeVo parent = idToNode.get(parentId);
            if (parent == null) {
                treeNodeVos.add(node);
                continue;
            }
            parent.getChildren().add(node);
        }

        return treeNodeVos;
    }

    /**
     * 新增请求集合
     *
     * @param dto 新增请求集合参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCollection(AddCollectionDto dto) throws BizException {

        Long companyId = AuthService.getCurrentCompanyId();
        CollectionPo parentPo = null;
        
        //如果父级ID不为空，则校验父级节点
        if (dto.getParentId() != null) {

            parentPo = repository.getByIdAndCompanyId(dto.getParentId(), companyId);
            if (parentPo == null) {
                throw new BizException("父级节点不存在或无权限访问");
            }

            if (parentPo.getKind() != 1) {
                throw new BizException("父级节点必须是请求组");
            }

        }

        //组装请求PO
        CollectionPo insertPo = new CollectionPo();
        as(dto, insertPo);
        insertPo.setParent(parentPo);
        insertPo.setCompanyId(companyId);
        insertPo.setSeq(repository.getMaxSeqInParent(dto.getParentId()));
        repository.save(insertPo);
    }

    /**
     * 移动请求集合
     *
     * @param dto 移动请求集合参数
     * @return 移动请求集合结果
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void moveCollection(MoveCollectionDto dto) throws BizException {

        Long companyId = AuthService.getCurrentCompanyId();

        //查找对象节点
        CollectionPo nodePo = repository.getByIdAndCompanyId(dto.getNodeId(), companyId);

        if (nodePo == null) {
            throw new BizException("对象节点不存在或无权限访问");
        }

        //移动到顶层
        if (dto.getTargetId() == null) {
            nodePo.setParent(null);
            nodePo.setSeq(repository.getMaxSeqInParent(null));
            repository.save(nodePo);
            return;
        }

        //移动到目标节点
        CollectionPo targetNodePo = repository.getByIdAndCompanyId(dto.getTargetId(), companyId);

        if (targetNodePo == null) {
            throw new BizException("目标节点不存在或无权限访问");
        }

        Integer targetSeq = targetNodePo.getSeq();

        //处理移动方式 0:顶部 1:底部 2:内部

        //顶部
        if (dto.getKind() == 0) {

            nodePo.setSeq(targetSeq);

            /*
             * 处理目标节点让位
             * 让位逻辑: 目标节点同级节点下所有节点SEQ+1
             */

            //获取目标节点的父级 如果是NULL则顶级节点让位
            CollectionPo targetParentNodePo = targetNodePo.getParent();

            //将当前正在移动的节点父级设置为目标节点的父级
            nodePo.setParent(targetParentNodePo);

            //处理顶级节点让位
            if (targetParentNodePo == null) {
                List<CollectionPo> rootNodeList = repository.getRootNodeListByCompanyId(companyId);
                for (CollectionPo item : rootNodeList) {
                    if (item.getSeq() >= targetSeq) {
                        item.setSeq(item.getSeq() + 1);
                    }
                }
                repository.saveAll(rootNodeList);
                repository.save(nodePo);
                return;
            }

            //处理内部节点让位
            List<CollectionPo> targetParentNodeList = targetParentNodePo.getChildren();
            for (CollectionPo item : targetParentNodeList) {
                if (item.getSeq() >= targetSeq) {

                    //跳过当前正在移动的节点
                    if (Objects.equals(item.getId(), nodePo.getId())) {
                        continue;
                    }

                    item.setSeq(item.getSeq() + 1);
                }
            }
            repository.saveAll(targetParentNodeList);
            repository.save(nodePo);
            return;
        }

        //底部
        if (dto.getKind() == 1) {
            nodePo.setSeq(targetSeq);

            /*
             * 处理目标节点让位
             * 让位逻辑: 目标节点同级节点下所有节点SEQ-1
             */
            //获取目标节点的父级 如果是NULL则顶级节点让位
            CollectionPo targetParentNodePo = targetNodePo.getParent();

            //将当前正在移动的节点父级设置为目标节点的父级
            nodePo.setParent(targetParentNodePo);

            //处理顶级节点让位
            if (targetParentNodePo == null) {
                List<CollectionPo> rootNodeList = repository.getRootNodeListByCompanyId(companyId);
                for (CollectionPo item : rootNodeList) {
                    if (item.getSeq() <= targetSeq) {

                        //跳过当前正在移动的节点
                        if (Objects.equals(item.getId(), nodePo.getId())) {
                            continue;
                        }


                        item.setSeq(item.getSeq() - 1);
                    }
                }
                repository.saveAll(rootNodeList);
                repository.save(nodePo);
                return;
            }

            //处理内部节点让位
            List<CollectionPo> targetParentNodeList = targetParentNodePo.getChildren();
            for (CollectionPo item : targetParentNodeList) {
                if (item.getSeq() <= targetSeq) {

                    //跳过当前正在移动的节点
                    if (Objects.equals(item.getId(), nodePo.getId())) {
                        continue;
                    }

                    item.setSeq(item.getSeq() - 1);
                }
            }
            repository.saveAll(targetParentNodeList);
            repository.save(nodePo);
            return;
        }

        //内部
        if (dto.getKind() == 2) {

            //目标节点不是组则直接移动到下一个位置 并处理让位
            if (targetNodePo.getKind() == 0) {
                nodePo.setSeq(targetSeq);

                //获取目标节点的父级 如果是NULL则顶级节点让位
                CollectionPo targetParentNodePo = targetNodePo.getParent();

                //将当前正在移动的节点父级设置为目标节点的父级
                nodePo.setParent(targetParentNodePo);

                //处理顶级节点让位
                if (targetParentNodePo == null) {
                    List<CollectionPo> rootNodeList = repository.getRootNodeListByCompanyId(companyId);
                    for (CollectionPo item : rootNodeList) {

                        //跳过当前正在移动的节点
                        if (Objects.equals(item.getId(), nodePo.getId())) {
                            continue;
                        }

                        if (item.getSeq() >= targetSeq) {
                            item.setSeq(item.getSeq() + 1);
                        }
                    }
                    repository.saveAll(rootNodeList);
                    repository.save(nodePo);
                    return;
                }

                //处理内部节点让位
                List<CollectionPo> targetParentNodeList = targetParentNodePo.getChildren();
                for (CollectionPo item : targetParentNodeList) {
                    if (item.getSeq() >= targetSeq) {

                        //跳过当前正在移动的节点
                        if (Objects.equals(item.getId(), nodePo.getId())) {
                            continue;
                        }

                        item.setSeq(item.getSeq() + 1);
                    }
                }
                repository.saveAll(targetParentNodeList);
                repository.save(nodePo);
                return;
            }

            //目标节点是组则移动到组中最后一个位置
            nodePo.setParent(targetNodePo);
            nodePo.setSeq(repository.getMaxSeqInParent(targetNodePo.getId()));
            repository.save(nodePo);
            return;
        }

    }


    /**
     * 复制请求集合
     *
     * @param dto 复制请求集合参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void copyCollection(CommonIdDto dto) throws BizException {

        Long companyId = AuthService.getCurrentCompanyId();

        CollectionPo sourceNodePo = repository.getByIdAndCompanyId(dto.getId(), companyId);

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

        //组装新节点PO
        CollectionPo newNodePo = new CollectionPo();
        as(sourceNodePo, newNodePo);
        newNodePo.setId(IdWorker.nextId());
        newNodePo.setName(nodeName);
        newNodePo.setSeq(sourceNodePo.getSeq() + 1);

        //处理让位
        var sourceParentNodePo = sourceNodePo.getParent();

        //处理顶级节点让位
        if (sourceParentNodePo == null) {
            List<CollectionPo> rootNodeList = repository.getRootNodeListByCompanyId(companyId);
            for (CollectionPo item : rootNodeList) {

                //所有SEQ大于等于新复制节点SEQ的节点都需要进行让位处理
                if (item.getSeq() >= newNodePo.getSeq()) {
                    newNodePo.setSeq(item.getSeq() + 1);
                }

            }
            repository.saveAll(rootNodeList);
        }

        //处理内部节点让位
        if (sourceParentNodePo != null) {

            List<CollectionPo> sourceParentNodeList = sourceParentNodePo.getChildren();
            for (CollectionPo item : sourceParentNodeList) {
                if (item.getSeq() >= newNodePo.getSeq()) {
                    item.setSeq(item.getSeq() + 1);
                }
            }
            repository.saveAll(sourceParentNodeList);

        }


        //如果被复制的节点是组则复制组下的所有请求
        if (sourceNodePo.getKind() == 0) {
            List<CollectionPo> sourceChildren = sourceNodePo.getChildren();
            for (CollectionPo item : sourceChildren) {
                CollectionPo newChild = new CollectionPo();
                as(item, newChild);
                newChild.setId(IdWorker.nextId());
                newChild.setParent(newNodePo);
            }
        }

        repository.save(newNodePo);
    }


    /**
     * 编辑请求集合
     *
     * @param dto 编辑请求集合参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editCollection(EditCollectionDto dto) throws BizException {

        Long companyId = AuthService.getCurrentCompanyId();
        CollectionPo updatePo = repository.getByIdAndCompanyId(dto.getId(), companyId);

        if (updatePo == null) {
            throw new BizException("对象节点不存在或无权限访问");
        }

        //如果是组类型不允许编辑请求相关字段
        if (updatePo.getKind() == 1) {
            if (StringUtils.isNotBlank(dto.getReqUrl())) {
                throw new BizException("组类型不允许编辑请求URL");
            }
            if (StringUtils.isNotBlank(dto.getReqUrlParamsJson())) {
                throw new BizException("组类型不允许编辑请求URL参数");
            }
            if (dto.getReqMethod() != null) {
                throw new BizException("组类型不允许编辑请求方法");
            }
            if (StringUtils.isNotBlank(dto.getReqHeaderJson())) {
                throw new BizException("组类型不允许编辑请求头");
            }
            if (dto.getReqBodyKind() != null) {
                throw new BizException("组类型不允许编辑请求体类型");
            }
            if (StringUtils.isNotBlank(dto.getReqBodyJson())) {
                throw new BizException("组类型不允许编辑请求体");
            }
        }

        updatePo.setName(dto.getName());
        updatePo.setReqUrl(dto.getReqUrl());
        updatePo.setReqUrlParamsJson(dto.getReqUrlParamsJson());
        updatePo.setReqMethod(dto.getReqMethod());
        updatePo.setReqHeaderJson(dto.getReqHeaderJson());
        updatePo.setReqBodyKind(dto.getReqBodyKind());
        updatePo.setReqBodyJson(dto.getReqBodyJson());
        repository.save(updatePo);
    }

    /**
     * 获取请求集合详情
     *
     * @param dto 获取请求集合详情参数
     * @return 请求集合详情
     * @throws BizException 业务异常
     */
    public GetCollectionDetailsVo getCollectionDetails(CommonIdDto dto) throws BizException {

        Long companyId = AuthService.getCurrentCompanyId();
        CollectionPo po = repository.getByIdAndCompanyId(dto.getId(), companyId);
        if (po == null) {
            throw new BizException("对象节点不存在或无权限访问");
        }

        GetCollectionDetailsVo vo = as(po, GetCollectionDetailsVo.class);

        if (po.getParent() != null) {
            vo.setParentId(po.getParent().getId());
        }

        return vo;
    }

    /**
     * 删除请求集合
     *
     * @param dto 删除请求集合参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeCollection(CommonIdDto dto) throws BizException {

        Long companyId = AuthService.getCurrentCompanyId();

        var ids = dto.toIds();

        for (var id : ids) {
            CollectionPo po = repository.getByIdAndCompanyId(id, companyId);
            if (po == null) {
                throw new BizException("对象节点不存在或无权限访问");
            }
            repository.delete(po);
        }

    }

}