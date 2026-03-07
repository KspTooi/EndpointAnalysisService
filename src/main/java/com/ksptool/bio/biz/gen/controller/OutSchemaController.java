package com.ksptool.bio.biz.gen.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptool.bio.biz.gen.service.OutSchemaService;
import com.ksptool.bio.biz.gen.model.outschema.dto.AddOutSchemaDto;
import com.ksptool.bio.biz.gen.model.outschema.dto.EditOutSchemaDto;
import com.ksptool.bio.biz.gen.model.outschema.dto.GetOutSchemaListDto;
import com.ksptool.bio.biz.gen.model.outschema.vo.GetOutSchemaListVo;
import com.ksptool.bio.biz.gen.model.outschema.vo.GetOutSchemaDetailsVo;


@RestController
@RequestMapping("/outSchema")
@Tag(name = "输出方案管理", description = "输出方案管理")
@Slf4j
public class OutSchemaController {

    @Autowired
    private OutSchemaService outSchemaService;

    @PostMapping("/getOutSchemaList")
    @Operation(summary = "查询输出方案列表")
    public PageResult<GetOutSchemaListVo> getOutSchemaList(@RequestBody @Valid GetOutSchemaListDto dto) throws Exception {
        return outSchemaService.getOutSchemaList(dto);
    }

    @Operation(summary = "新增输出方案")
    @PostMapping("/addOutSchema")
    public Result<String> addOutSchema(@RequestBody @Valid AddOutSchemaDto dto) throws Exception {
        var message = outSchemaService.addOutSchema(dto);
        return Result.success(message,null);
    }

    @Operation(summary = "编辑输出方案")
    @PostMapping("/editOutSchema")
    public Result<String> editOutSchema(@RequestBody @Valid EditOutSchemaDto dto) throws Exception {
        var message = outSchemaService.editOutSchema(dto);
        return Result.success(message,null);
    }

    @Operation(summary = "查询输出方案详情")
    @PostMapping("/getOutSchemaDetails")
    public Result<GetOutSchemaDetailsVo> getOutSchemaDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetOutSchemaDetailsVo details = outSchemaService.getOutSchemaDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除输出方案元素")
    @PostMapping("/removeOutSchema")
    public Result<String> removeOutSchema(@RequestBody @Valid CommonIdDto dto) throws Exception {
        outSchemaService.removeOutSchema(dto);
        return Result.success("操作成功");
    }

}
