package com.ksptool.bio.biz.gentymschema.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptool.bio.biz.gentymschema.service.GenTymSchemaService;
import com.ksptool.bio.biz.gentymschema.model.dto.AddGenTymSchemaDto;
import com.ksptool.bio.biz.gentymschema.model.dto.EditGenTymSchemaDto;
import com.ksptool.bio.biz.gentymschema.model.dto.GetGenTymSchemaListDto;
import com.ksptool.bio.biz.gentymschema.model.vo.GetGenTymSchemaListVo;
import com.ksptool.bio.biz.gentymschema.model.vo.GetGenTymSchemaDetailsVo;


@RestController
@RequestMapping("/genTymSchema")
@Tag(name = "genTymSchema", description = "类型映射方案表")
@Slf4j
public class GenTymSchemaController {

    @Autowired
    private GenTymSchemaService genTymSchemaService;

    @PostMapping("/getGenTymSchemaList")
    @Operation(summary ="列表查询")
    public PageResult<GetGenTymSchemaListVo> getGenTymSchemaList(@RequestBody @Valid GetGenTymSchemaListDto dto) throws Exception{
        return genTymSchemaService.getGenTymSchemaList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addGenTymSchema")
    public Result<String> addGenTymSchema(@RequestBody @Valid AddGenTymSchemaDto dto) throws Exception{
		genTymSchemaService.addGenTymSchema(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editGenTymSchema")
    public Result<String> editGenTymSchema(@RequestBody @Valid EditGenTymSchemaDto dto) throws Exception{
		genTymSchemaService.editGenTymSchema(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getGenTymSchemaDetails")
    public Result<GetGenTymSchemaDetailsVo> getGenTymSchemaDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetGenTymSchemaDetailsVo details = genTymSchemaService.getGenTymSchemaDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeGenTymSchema")
    public Result<String> removeGenTymSchema(@RequestBody @Valid CommonIdDto dto) throws Exception{
        genTymSchemaService.removeGenTymSchema(dto);
        return Result.success("操作成功");
    }

}
