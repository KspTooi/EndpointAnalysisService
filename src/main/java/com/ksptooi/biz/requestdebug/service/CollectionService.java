package com.ksptooi.biz.requestdebug.service;

import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.biz.requestdebug.model.collection.CollectionPo;
import com.ksptooi.biz.requestdebug.model.collection.dto.AddCollectionDto;
import com.ksptooi.biz.requestdebug.model.collection.dto.EditCollectionDto;
import com.ksptooi.biz.requestdebug.model.collection.dto.GetCollectionListDto;
import com.ksptooi.biz.requestdebug.model.collection.vo.GetCollectionDetailsVo;
import com.ksptooi.biz.requestdebug.model.collection.vo.GetCollectionListVo;
import com.ksptooi.biz.requestdebug.model.collection.vo.GetCollectionTreeVo;
import com.ksptooi.biz.requestdebug.repoistory.CollectionRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class CollectionService {

    @Autowired
    private CollectionRepository repository;

    /**
     * 获取请求集合树
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
            vo.setChildren(new ArrayList<>());
            flatNodeVos.add(vo);
        }

        //将平面节点转换为树结构
        Map<Long, GetCollectionTreeVo> idToNode = new HashMap<>();

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
     * @param dto 新增请求集合参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCollection(AddCollectionDto dto) throws BizException {

        Long companyId = AuthService.getCurrentCompanyId();

        //如果父级ID不为空，则校验父级节点
        if (dto.getParentId() != null) {

            CollectionPo parentPo = repository.getByIdAndCompanyId(dto.getParentId(), companyId);
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
        insertPo.setParentId(dto.getParentId());
        insertPo.setSeq(repository.getMaxSeqInParent(dto.getParentId()));
        repository.save(insertPo);
    }

    /**
     * 编辑请求集合
     * @param dto 编辑请求集合参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editCollection(EditCollectionDto dto) throws BizException {
        CollectionPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 获取请求集合详情
     * @param dto 获取请求集合详情参数
     * @return 请求集合详情
     * @throws BizException 业务异常
     */
    public GetCollectionDetailsVo getCollectionDetails(CommonIdDto dto) throws BizException {
        CollectionPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetCollectionDetailsVo.class);
    }

    /**
     * 删除请求集合
     * @param dto 删除请求集合参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeCollection(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}