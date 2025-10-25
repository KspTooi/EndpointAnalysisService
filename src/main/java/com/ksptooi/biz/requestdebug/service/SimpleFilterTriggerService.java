package com.ksptooi.biz.requestdebug.service;

import com.ksptooi.biz.requestdebug.model.filter.SimpleFilterTriggerPo;
import com.ksptooi.biz.requestdebug.model.filter.dto.AddSimpleFilterTriggerDto;
import com.ksptooi.biz.requestdebug.model.filter.dto.EditSimpleFilterTriggerDto;
import com.ksptooi.biz.requestdebug.model.filter.dto.GetSimpleFilterTriggerListDto;
import com.ksptooi.biz.requestdebug.model.filter.vo.GetSimpleFilterTriggerDetailsVo;
import com.ksptooi.biz.requestdebug.model.filter.vo.GetSimpleFilterTriggerListVo;
import com.ksptooi.biz.requestdebug.repoistory.SimpleFilterTriggerRepository;
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
public class SimpleFilterTriggerService {

    @Autowired
    private SimpleFilterTriggerRepository repository;

    public PageResult<GetSimpleFilterTriggerListVo> getSimpleFilterTriggerList(GetSimpleFilterTriggerListDto dto) {
        SimpleFilterTriggerPo query = new SimpleFilterTriggerPo();
        assign(dto, query);

        Page<SimpleFilterTriggerPo> page = repository.getSimpleFilterTriggerList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetSimpleFilterTriggerListVo> vos = as(page.getContent(), GetSimpleFilterTriggerListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSimpleFilterTrigger(AddSimpleFilterTriggerDto dto) {
        SimpleFilterTriggerPo insertPo = as(dto, SimpleFilterTriggerPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editSimpleFilterTrigger(EditSimpleFilterTriggerDto dto) throws BizException {
        SimpleFilterTriggerPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetSimpleFilterTriggerDetailsVo getSimpleFilterTriggerDetails(CommonIdDto dto) throws BizException {
        SimpleFilterTriggerPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetSimpleFilterTriggerDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeSimpleFilterTrigger(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}