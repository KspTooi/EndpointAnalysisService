package com.ksptool.bio.biz.gen.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.gen.model.gentymschema.dto.AddGenTymSchemaDto;
import com.ksptool.bio.biz.gen.model.gentymschema.dto.EditGenTymSchemaDto;
import com.ksptool.bio.biz.gen.model.gentymschema.dto.GetGenTymSchemaListDto;
import com.ksptool.bio.biz.gen.model.gentymschema.vo.GetGenTymSchemaDetailsVo;
import com.ksptool.bio.biz.gen.model.gentymschema.vo.GetGenTymSchemaListVo;
import com.ksptool.bio.biz.gen.service.GenTymSchemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/genTymSchema")
@Tag(name = "genTymSchema", description = "类型映射方案表")
@Slf4j
public class GenTymSchemaController {

    @Autowired
    private GenTymSchemaService genTymSchemaService;

    @PostMapping("/getGenTymSchemaList")
    @Operation(summary = "列表查询")
    public PageResult<GetGenTymSchemaListVo> getGenTymSchemaList(@RequestBody @Valid GetGenTymSchemaListDto dto) throws Exception {
        return genTymSchemaService.getGenTymSchemaList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addGenTymSchema")
    public Result<String> addGenTymSchema(@RequestBody @Valid AddGenTymSchemaDto dto) throws Exception {
        genTymSchemaService.addGenTymSchema(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editGenTymSchema")
    public Result<String> editGenTymSchema(@RequestBody @Valid EditGenTymSchemaDto dto) throws Exception {
        genTymSchemaService.editGenTymSchema(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getGenTymSchemaDetails")
    public Result<GetGenTymSchemaDetailsVo> getGenTymSchemaDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetGenTymSchemaDetailsVo details = genTymSchemaService.getGenTymSchemaDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeGenTymSchema")
    public Result<String> removeGenTymSchema(@RequestBody @Valid CommonIdDto dto) throws Exception {
        genTymSchemaService.removeGenTymSchema(dto);
        return Result.success("操作成功");
    }

}
