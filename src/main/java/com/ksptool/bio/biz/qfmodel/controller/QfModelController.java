package com.ksptool.bio.biz.qfmodel.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptool.bio.biz.qfmodel.service.QfModelService;
import com.ksptool.bio.biz.qfmodel.model.dto.AddQfModelDto;
import com.ksptool.bio.biz.qfmodel.model.dto.EditQfModelDto;
import com.ksptool.bio.biz.qfmodel.model.dto.GetQfModelListDto;
import com.ksptool.bio.biz.qfmodel.model.vo.GetQfModelListVo;
import com.ksptool.bio.biz.qfmodel.model.vo.GetQfModelDetailsVo;


@RestController
@RequestMapping("/qfModel")
@Tag(name = "QF流程模型管理", description = "QF流程模型管理")
@Slf4j
public class QfModelController {

    @Autowired
    private QfModelService qfModelService;

    @PostMapping("/getQfModelList")
    @Operation(summary ="查询流程模型列表")
    public PageResult<GetQfModelListVo> getQfModelList(@RequestBody @Valid GetQfModelListDto dto) throws Exception{
        return qfModelService.getQfModelList(dto);
    }

    @Operation(summary ="新增流程模型")
    @PostMapping("/addQfModel")
    public Result<String> addQfModel(@RequestBody @Valid AddQfModelDto dto) throws Exception{
		qfModelService.addQfModel(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑流程模型")
    @PostMapping("/editQfModel")
    public Result<String> editQfModel(@RequestBody @Valid EditQfModelDto dto) throws Exception{
		qfModelService.editQfModel(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询流程模型详情")
    @PostMapping("/getQfModelDetails")
    public Result<GetQfModelDetailsVo> getQfModelDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetQfModelDetailsVo details = qfModelService.getQfModelDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除流程模型")
    @PostMapping("/removeQfModel")
    public Result<String> removeQfModel(@RequestBody @Valid CommonIdDto dto) throws Exception{
        qfModelService.removeQfModel(dto);
        return Result.success("操作成功");
    }

}
