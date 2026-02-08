package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.registryentry.RegistryEntryPo;
import com.ksptooi.biz.core.model.registryentry.dto.AddRegistryEntryDto;
import com.ksptooi.biz.core.model.registryentry.dto.EditRegistryEntryDto;
import com.ksptooi.biz.core.model.registryentry.dto.GetRegistryEntryListDto;
import com.ksptooi.biz.core.model.registryentry.vo.GetRegistryEntryDetailsVo;
import com.ksptooi.biz.core.model.registryentry.vo.GetRegistryEntryListVo;
import com.ksptooi.biz.core.repository.RegistryEntryRepository;
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
public class RegistryEntryService {

    @Autowired
    private RegistryEntryRepository repository;

    public PageResult<GetRegistryEntryListVo> getRegistryEntryList(GetRegistryEntryListDto dto) {
        RegistryEntryPo query = new RegistryEntryPo();
        assign(dto, query);

        Page<RegistryEntryPo> page = repository.getRegistryEntryList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetRegistryEntryListVo> vos = as(page.getContent(), GetRegistryEntryListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addRegistryEntry(AddRegistryEntryDto dto) {
        RegistryEntryPo insertPo = as(dto, RegistryEntryPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editRegistryEntry(EditRegistryEntryDto dto) throws BizException {
        RegistryEntryPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetRegistryEntryDetailsVo getRegistryEntryDetails(CommonIdDto dto) throws BizException {
        RegistryEntryPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetRegistryEntryDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeRegistryEntry(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}