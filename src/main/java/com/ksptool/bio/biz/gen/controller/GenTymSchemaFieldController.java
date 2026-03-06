package com.ksptool.bio.biz.gen.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.gen.model.gentymschemafield.dto.AddGenTymSchemaFieldDto;
import com.ksptool.bio.biz.gen.model.gentymschemafield.dto.EditGenTymSchemaFieldDto;
import com.ksptool.bio.biz.gen.model.gentymschemafield.dto.GetGenTymSchemaFieldListDto;
import com.ksptool.bio.biz.gen.model.gentymschemafield.vo.GetGenTymSchemaFieldDetailsVo;
import com.ksptool.bio.biz.gen.model.gentymschemafield.vo.GetGenTymSchemaFieldListVo;
import com.ksptool.bio.biz.gen.service.GenTymSchemaFieldService;
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
@RequestMapping("/genTymSchemaField")
@Tag(name = "genTymSchemaField", description = "类型映射方案字段表")
@Slf4j
public class GenTymSchemaFieldController {

    @Autowired
    private GenTymSchemaFieldService genTymSchemaFieldService;

    @PostMapping("/getGenTymSchemaFieldList")
    @Operation(summary = "列表查询")
    public PageResult<GetGenTymSchemaFieldListVo> getGenTymSchemaFieldList(@RequestBody @Valid GetGenTymSchemaFieldListDto dto) throws Exception {
        return genTymSchemaFieldService.getGenTymSchemaFieldList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addGenTymSchemaField")
    public Result<String> addGenTymSchemaField(@RequestBody @Valid AddGenTymSchemaFieldDto dto) throws Exception {
        genTymSchemaFieldService.addGenTymSchemaField(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editGenTymSchemaField")
    public Result<String> editGenTymSchemaField(@RequestBody @Valid EditGenTymSchemaFieldDto dto) throws Exception {
        genTymSchemaFieldService.editGenTymSchemaField(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getGenTymSchemaFieldDetails")
    public Result<GetGenTymSchemaFieldDetailsVo> getGenTymSchemaFieldDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetGenTymSchemaFieldDetailsVo details = genTymSchemaFieldService.getGenTymSchemaFieldDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeGenTymSchemaField")
    public Result<String> removeGenTymSchemaField(@RequestBody @Valid CommonIdDto dto) throws Exception {
        genTymSchemaFieldService.removeGenTymSchemaField(dto);
        return Result.success("操作成功");
    }

}
