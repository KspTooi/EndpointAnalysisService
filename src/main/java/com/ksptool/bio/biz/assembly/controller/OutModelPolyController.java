package com.ksptool.bio.biz.assembly.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptool.bio.biz.assembly.service.OutModelPolyService;
import com.ksptool.bio.biz.assembly.model.outmodelpoly.dto.AddOutModelPolyDto;
import com.ksptool.bio.biz.assembly.model.outmodelpoly.dto.EditOutModelPolyDto;
import com.ksptool.bio.biz.assembly.model.outmodelpoly.dto.GetOutModelPolyListDto;
import com.ksptool.bio.biz.assembly.model.outmodelpoly.vo.GetOutModelPolyListVo;
import com.ksptool.bio.biz.assembly.model.outmodelpoly.vo.GetOutModelPolyDetailsVo;


@RestController
@RequestMapping("/outModelPoly")
@Tag(name = "输出方案聚合模型管理", description = "输出方案聚合模型管理")
@Slf4j
public class OutModelPolyController {

    @Autowired
    private OutModelPolyService outModelPolyService;

    @PostMapping("/getOutModelPolyList")
    @Operation(summary = "查询聚合模型列表")
    public PageResult<GetOutModelPolyListVo> getOutModelPolyList(@RequestBody @Valid GetOutModelPolyListDto dto) throws Exception {
        return outModelPolyService.getOutModelPolyList(dto);
    }

    @Operation(summary = "新增聚合模型")
    @PostMapping("/addOutModelPoly")
    public Result<String> addOutModelPoly(@RequestBody @Valid AddOutModelPolyDto dto) throws Exception {
        outModelPolyService.addOutModelPoly(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑聚合模型")
    @PostMapping("/editOutModelPoly")
    public Result<String> editOutModelPoly(@RequestBody @Valid EditOutModelPolyDto dto) throws Exception {
        outModelPolyService.editOutModelPoly(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询聚合模型详情")
    @PostMapping("/getOutModelPolyDetails")
    public Result<GetOutModelPolyDetailsVo> getOutModelPolyDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetOutModelPolyDetailsVo details = outModelPolyService.getOutModelPolyDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除聚合模型元素")
    @PostMapping("/removeOutModelPoly")
    public Result<String> removeOutModelPoly(@RequestBody @Valid CommonIdDto dto) throws Exception {
        outModelPolyService.removeOutModelPoly(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "从原始模型同步聚合模型(输出方案ID)")
    @PostMapping("/syncFromOriginBySchema")
    public Result<String> syncFromOrigin(@RequestBody @Valid CommonIdDto dto) throws Exception {
        outModelPolyService.syncFromOriginBySchema(dto);
        return Result.success("同步成功");
    }

}
