package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.filter.SimpleFilterOperationPo;
import com.ksptooi.biz.core.model.filter.dto.AddSimpleFilterOperationDto;
import com.ksptooi.biz.core.model.filter.dto.EditSimpleFilterOperationDto;
import com.ksptooi.biz.core.model.filter.dto.GetSimpleFilterOperationListDto;
import com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterOperationDetailsVo;
import com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterOperationListVo;
import com.ksptooi.biz.core.repository.SimpleFilterOperationRepository;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class SimpleFilterOperationService {

    @Autowired
    private SimpleFilterOperationRepository repository;

    public PageResult<GetSimpleFilterOperationListVo> getSimpleFilterOperationList(GetSimpleFilterOperationListDto dto) {
        SimpleFilterOperationPo query = new SimpleFilterOperationPo();
        assign(dto, query);

        Page<SimpleFilterOperationPo> page = repository.getSimpleFilterOperationList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetSimpleFilterOperationListVo> vos = as(page.getContent(), GetSimpleFilterOperationListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSimpleFilterOperation(AddSimpleFilterOperationDto dto) {
        SimpleFilterOperationPo insertPo = as(dto, SimpleFilterOperationPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editSimpleFilterOperation(EditSimpleFilterOperationDto dto) throws BizException {
        SimpleFilterOperationPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetSimpleFilterOperationDetailsVo getSimpleFilterOperationDetails(CommonIdDto dto) throws BizException {
        SimpleFilterOperationPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetSimpleFilterOperationDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeSimpleFilterOperation(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}