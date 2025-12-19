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
import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.biz.drive.model.dto.AddEntryDto;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class EntryService {

    @Autowired
    private EntryRepository repository;

    /**
     * 查询条目列表
     * @param dto 查询条件
     * @return 条目列表
     */
    public PageResult<GetEntryListVo> getEntryList(GetEntryListDto dto){

        Long companyId = AuthService.requireCompanyId();

        EntryPo query = new EntryPo();
        assign(dto,query);

        Page<EntryPo> page = repository.getEntryList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetEntryListVo> vos = as(page.getContent(), GetEntryListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增条目
     * @param dto 新增条目
     */
    @Transactional(rollbackFor = Exception.class)
    public void addEntry(AddEntryDto dto){
        EntryPo insertPo = as(dto,EntryPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑条目
     * @param dto 编辑条目
     */
    @Transactional(rollbackFor = Exception.class)
    public void editEntry(EditEntryDto dto) throws BizException {
        EntryPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询条目详情
     * @param dto 查询条件
     * @return 条目详情
     */
    public GetEntryDetailsVo getEntryDetails(CommonIdDto dto) throws BizException {
        EntryPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在."));
        return as(po,GetEntryDetailsVo.class);
    }

    /**
     * 删除条目
     * @param dto 删除条件
     */
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