package com.ksptooi.biz.drive.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import com.ksptooi.biz.drive.repository.EntryRepository;
import com.ksptooi.biz.drive.model.po.EntryPo;
import com.ksptooi.biz.drive.model.vo.GetEntryListVo;
import com.ksptooi.biz.drive.model.dto.GetEntryListDto;
import com.ksptooi.biz.drive.model.vo.GetEntryDetailsVo;
import com.ksptooi.biz.drive.model.dto.EditEntryDto;
import com.ksptooi.biz.drive.model.dto.AddEntryDto;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class EntryService {

    @Autowired
    private EntryRepository repository;

    public PageResult<GetEntryListVo> getEntryList(GetEntryListDto dto){
        EntryPo query = new EntryPo();
        assign(dto,query);

        Page<EntryPo> page = repository.getEntryList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetEntryListVo> vos = as(page.getContent(), GetEntryListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addEntry(AddEntryDto dto){
        EntryPo insertPo = as(dto,EntryPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editEntry(EditEntryDto dto) throws BizException {
        EntryPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetEntryDetailsVo getEntryDetails(CommonIdDto dto) throws BizException {
        EntryPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在."));
        return as(po,GetEntryDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeEntry(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if(!dto.isBatch()){
            repository.deleteById(dto.getId());
        }
    }

}