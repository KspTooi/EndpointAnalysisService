package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.exceltemplate.ExcelTemplatePo;
import com.ksptooi.biz.core.model.exceltemplate.dto.AddExcelTemplateDto;
import com.ksptooi.biz.core.model.exceltemplate.dto.EditExcelTemplateDto;
import com.ksptooi.biz.core.model.exceltemplate.dto.GetExcelTemplateListDto;
import com.ksptooi.biz.core.model.exceltemplate.vo.GetExcelTemplateDetailsVo;
import com.ksptooi.biz.core.model.exceltemplate.vo.GetExcelTemplateListVo;
import com.ksptooi.biz.core.repository.ExcelTemplateRepository;
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
public class ExcelTemplateService {

    @Autowired
    private ExcelTemplateRepository repository;

    public PageResult<GetExcelTemplateListVo> getExcelTemplateList(GetExcelTemplateListDto dto) {
        ExcelTemplatePo query = new ExcelTemplatePo();
        assign(dto, query);

        Page<ExcelTemplatePo> page = repository.getExcelTemplateList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetExcelTemplateListVo> vos = as(page.getContent(), GetExcelTemplateListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addExcelTemplate(AddExcelTemplateDto dto) {
        ExcelTemplatePo insertPo = as(dto, ExcelTemplatePo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editExcelTemplate(EditExcelTemplateDto dto) throws BizException {
        ExcelTemplatePo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetExcelTemplateDetailsVo getExcelTemplateDetails(CommonIdDto dto) throws BizException {
        ExcelTemplatePo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetExcelTemplateDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeExcelTemplate(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}