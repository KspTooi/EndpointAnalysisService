package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.exceltemplate.dto.AddExcelTemplateDto;
import com.ksptooi.biz.core.model.exceltemplate.dto.EditExcelTemplateDto;
import com.ksptooi.biz.core.model.exceltemplate.dto.GetExcelTemplateListDto;
import com.ksptooi.biz.core.model.exceltemplate.vo.GetExcelTemplateDetailsVo;
import com.ksptooi.biz.core.model.exceltemplate.vo.GetExcelTemplateListVo;
import com.ksptooi.biz.core.service.ExcelTemplateService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@PrintLog
@RestController
@RequestMapping("/excelTemplate")
@Tag(name = "ExcelTemplate", description = "导入模板表")
@Slf4j
public class ExcelTemplateController {

    @Autowired
    private ExcelTemplateService excelTemplateService;

    @PostMapping("/getExcelTemplateList")
    @Operation(summary = "查询Excel模板列表")
    public PageResult<GetExcelTemplateListVo> getExcelTemplateList(@RequestBody @Valid GetExcelTemplateListDto dto) throws Exception {
        return excelTemplateService.getExcelTemplateList(dto);
    }

    @Operation(summary = "新增Excel模板")
    @PostMapping("/addExcelTemplate")
    public Result<String> addExcelTemplate(@RequestBody @Valid AddExcelTemplateDto dto) throws Exception {
        excelTemplateService.addExcelTemplate(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑Excel模板")
    @PostMapping("/editExcelTemplate")
    public Result<String> editExcelTemplate(@RequestBody @Valid EditExcelTemplateDto dto) throws Exception {
        excelTemplateService.editExcelTemplate(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询Excel模板详情")
    @PostMapping("/getExcelTemplateDetails")
    public Result<GetExcelTemplateDetailsVo> getExcelTemplateDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetExcelTemplateDetailsVo details = excelTemplateService.getExcelTemplateDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除Excel模板")
    @PostMapping("/removeExcelTemplate")
    public Result<String> removeExcelTemplate(@RequestBody @Valid CommonIdDto dto) throws Exception {
        excelTemplateService.removeExcelTemplate(dto);
        return Result.success("操作成功");
    }

}
