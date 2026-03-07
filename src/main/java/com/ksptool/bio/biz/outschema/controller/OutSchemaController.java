package com.ksptool.bio.biz.outschema.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptool.bio.biz.outschema.service.OutSchemaService;
import com.ksptool.bio.biz.outschema.model.dto.AddOutSchemaDto;
import com.ksptool.bio.biz.outschema.model.dto.EditOutSchemaDto;
import com.ksptool.bio.biz.outschema.model.dto.GetOutSchemaListDto;
import com.ksptool.bio.biz.outschema.model.vo.GetOutSchemaListVo;
import com.ksptool.bio.biz.outschema.model.vo.GetOutSchemaDetailsVo;


@RestController
@RequestMapping("/outSchema")
@Tag(name = "outSchema", description = "输出方案表")
@Slf4j
public class OutSchemaController {

    @Autowired
    private OutSchemaService outSchemaService;

    @PostMapping("/getOutSchemaList")
    @Operation(summary ="列表查询")
    public PageResult<GetOutSchemaListVo> getOutSchemaList(@RequestBody @Valid GetOutSchemaListDto dto) throws Exception{
        return outSchemaService.getOutSchemaList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addOutSchema")
    public Result<String> addOutSchema(@RequestBody @Valid AddOutSchemaDto dto) throws Exception{
		outSchemaService.addOutSchema(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editOutSchema")
    public Result<String> editOutSchema(@RequestBody @Valid EditOutSchemaDto dto) throws Exception{
		outSchemaService.editOutSchema(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getOutSchemaDetails")
    public Result<GetOutSchemaDetailsVo> getOutSchemaDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetOutSchemaDetailsVo details = outSchemaService.getOutSchemaDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeOutSchema")
    public Result<String> removeOutSchema(@RequestBody @Valid CommonIdDto dto) throws Exception{
        outSchemaService.removeOutSchema(dto);
        return Result.success("操作成功");
    }

}
