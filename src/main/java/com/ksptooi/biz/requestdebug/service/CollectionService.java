package com.ksptooi.biz.requestdebug.service;

import com.ksptooi.biz.requestdebug.model.collection.CollectionPo;
import com.ksptooi.biz.requestdebug.model.collection.dto.AddCollectionDto;
import com.ksptooi.biz.requestdebug.model.collection.dto.EditCollectionDto;
import com.ksptooi.biz.requestdebug.model.collection.dto.GetCollectionListDto;
import com.ksptooi.biz.requestdebug.model.collection.vo.GetCollectionDetailsVo;
import com.ksptooi.biz.requestdebug.model.collection.vo.GetCollectionListVo;
import com.ksptooi.biz.requestdebug.repoistory.CollectionRepository;
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
public class CollectionService {

    @Autowired
    private CollectionRepository repository;

    public PageResult<GetCollectionListVo> getCollectionList(GetCollectionListDto dto) {
        CollectionPo query = new CollectionPo();
        assign(dto, query);

        Page<CollectionPo> page = repository.getCollectionList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetCollectionListVo> vos = as(page.getContent(), GetCollectionListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addCollection(AddCollectionDto dto) {
        CollectionPo insertPo = as(dto, CollectionPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editCollection(EditCollectionDto dto) throws BizException {
        CollectionPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetCollectionDetailsVo getCollectionDetails(CommonIdDto dto) throws BizException {
        CollectionPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetCollectionDetailsVo.class);
    }

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