package com.ksptooi.biz.rdbg.service;

import com.ksptooi.biz.rdbg.model.collectionhistory.CollectionHistoryPo;
import com.ksptooi.biz.rdbg.model.collectionhistory.dto.AddCollectionHistoryDto;
import com.ksptooi.biz.rdbg.model.collectionhistory.dto.EditCollectionHistoryDto;
import com.ksptooi.biz.rdbg.model.collectionhistory.dto.GetCollectionHistoryListDto;
import com.ksptooi.biz.rdbg.model.collectionhistory.vo.GetCollectionHistoryDetailsVo;
import com.ksptooi.biz.rdbg.model.collectionhistory.vo.GetCollectionHistoryListVo;
import com.ksptooi.biz.rdbg.repository.CollectionHistoryRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class CollectionHistoryService {

    @Autowired
    private CollectionHistoryRepository repository;

    /**
     * 获取请求集合历史记录列表
     *
     * @param dto 查询条件
     * @return 请求集合历史记录列表
     */
    public PageResult<GetCollectionHistoryListVo> getCollectionHistoryList(GetCollectionHistoryListDto dto) {
        CollectionHistoryPo query = new CollectionHistoryPo();
        assign(dto, query);

        Page<CollectionHistoryPo> page = repository.getCollectionHistoryList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetCollectionHistoryListVo> vos = as(page.getContent(), GetCollectionHistoryListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 添加请求集合历史记录
     *
     * @param dto 请求集合历史记录信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCollectionHistory(AddCollectionHistoryDto dto) {
        CollectionHistoryPo insertPo = as(dto, CollectionHistoryPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑请求集合历史记录
     *
     * @param dto 请求集合历史记录信息
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editCollectionHistory(EditCollectionHistoryDto dto) throws BizException {
        CollectionHistoryPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 获取请求集合历史记录详情
     *
     * @param dto 查询条件
     * @return 请求集合历史记录详情
     * @throws BizException 业务异常
     */
    public GetCollectionHistoryDetailsVo getCollectionHistoryDetails(CommonIdDto dto) throws BizException {
        CollectionHistoryPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetCollectionHistoryDetailsVo.class);
    }

    /**
     * 删除请求集合历史记录
     *
     * @param dto 查询条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeCollectionHistory(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}