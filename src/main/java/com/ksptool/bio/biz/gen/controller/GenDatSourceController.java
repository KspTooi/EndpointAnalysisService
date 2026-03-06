package com.ksptool.bio.biz.gen.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.gen.model.gendatsource.dto.AddGenDatSourceDto;
import com.ksptool.bio.biz.gen.model.gendatsource.dto.EditGenDatSourceDto;
import com.ksptool.bio.biz.gen.model.gendatsource.dto.GetGenDatSourceListDto;
import com.ksptool.bio.biz.gen.model.gendatsource.vo.GetGenDatSourceDetailsVo;
import com.ksptool.bio.biz.gen.model.gendatsource.vo.GetGenDatSourceListVo;
import com.ksptool.bio.biz.gen.service.GenDatSourceService;
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
@RequestMapping("/genDatSource")
@Tag(name = "genDatSource", description = "数据源表")
@Slf4j
public class GenDatSourceController {

    @Autowired
    private GenDatSourceService genDatSourceService;

    @PostMapping("/getGenDatSourceList")
    @Operation(summary = "列表查询")
    public PageResult<GetGenDatSourceListVo> getGenDatSourceList(@RequestBody @Valid GetGenDatSourceListDto dto) throws Exception {
        return genDatSourceService.getGenDatSourceList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addGenDatSource")
    public Result<String> addGenDatSource(@RequestBody @Valid AddGenDatSourceDto dto) throws Exception {
        genDatSourceService.addGenDatSource(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editGenDatSource")
    public Result<String> editGenDatSource(@RequestBody @Valid EditGenDatSourceDto dto) throws Exception {
        genDatSourceService.editGenDatSource(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getGenDatSourceDetails")
    public Result<GetGenDatSourceDetailsVo> getGenDatSourceDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetGenDatSourceDetailsVo details = genDatSourceService.getGenDatSourceDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeGenDatSource")
    public Result<String> removeGenDatSource(@RequestBody @Valid CommonIdDto dto) throws Exception {
        genDatSourceService.removeGenDatSource(dto);
        return Result.success("操作成功");
    }

}
