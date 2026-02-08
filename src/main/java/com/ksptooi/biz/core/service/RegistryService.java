package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.registry.RegistryPo;
import com.ksptooi.biz.core.model.registry.dto.AddRegistryDto;
import com.ksptooi.biz.core.model.registry.dto.EditRegistryDto;
import com.ksptooi.biz.core.model.registry.dto.GetRegistryListDto;
import com.ksptooi.biz.core.model.registry.vo.GetRegistryDetailsVo;
import com.ksptooi.biz.core.model.registry.vo.GetRegistryListVo;
import com.ksptooi.biz.core.repository.RegistryRepository;
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
public class RegistryService {

    @Autowired
    private RegistryRepository repository;

    public PageResult<GetRegistryListVo> getRegistryList(GetRegistryListDto dto) {
        RegistryPo query = new RegistryPo();
        assign(dto, query);

        Page<RegistryPo> page = repository.getRegistryList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetRegistryListVo> vos = as(page.getContent(), GetRegistryListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addRegistry(AddRegistryDto dto) {
        RegistryPo insertPo = as(dto, RegistryPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editRegistry(EditRegistryDto dto) throws BizException {
        RegistryPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetRegistryDetailsVo getRegistryDetails(CommonIdDto dto) throws BizException {
        RegistryPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetRegistryDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeRegistry(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}