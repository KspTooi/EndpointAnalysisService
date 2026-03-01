package com.ksptool.bio.biz.qfmodeldeployrcd.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptool.bio.biz.qfmodeldeployrcd.service.QfModelDeployRcdService;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.dto.AddQfModelDeployRcdDto;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.dto.EditQfModelDeployRcdDto;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.dto.GetQfModelDeployRcdListDto;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.vo.GetQfModelDeployRcdListVo;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.vo.GetQfModelDeployRcdDetailsVo;


@RestController
@RequestMapping("/qfModelDeployRcd")
@Tag(name = "qfModelDeployRcd", description = "流程模型部署历史")
@Slf4j
public class QfModelDeployRcdController {

    @Autowired
    private QfModelDeployRcdService qfModelDeployRcdService;

    @PostMapping("/getQfModelDeployRcdList")
    @Operation(summary ="列表查询")
    public PageResult<GetQfModelDeployRcdListVo> getQfModelDeployRcdList(@RequestBody @Valid GetQfModelDeployRcdListDto dto) throws Exception{
        return qfModelDeployRcdService.getQfModelDeployRcdList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addQfModelDeployRcd")
    public Result<String> addQfModelDeployRcd(@RequestBody @Valid AddQfModelDeployRcdDto dto) throws Exception{
		qfModelDeployRcdService.addQfModelDeployRcd(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editQfModelDeployRcd")
    public Result<String> editQfModelDeployRcd(@RequestBody @Valid EditQfModelDeployRcdDto dto) throws Exception{
		qfModelDeployRcdService.editQfModelDeployRcd(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getQfModelDeployRcdDetails")
    public Result<GetQfModelDeployRcdDetailsVo> getQfModelDeployRcdDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetQfModelDeployRcdDetailsVo details = qfModelDeployRcdService.getQfModelDeployRcdDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeQfModelDeployRcd")
    public Result<String> removeQfModelDeployRcd(@RequestBody @Valid CommonIdDto dto) throws Exception{
        qfModelDeployRcdService.removeQfModelDeployRcd(dto);
        return Result.success("操作成功");
    }

}
