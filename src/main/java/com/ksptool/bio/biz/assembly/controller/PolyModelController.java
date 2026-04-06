package com.ksptool.bio.biz.assembly.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.assembly.model.polymodel.dto.AddPolyModelDto;
import com.ksptool.bio.biz.assembly.model.polymodel.dto.EditPolyModelDto;
import com.ksptool.bio.biz.assembly.model.polymodel.dto.GetPolyModelListDto;
import com.ksptool.bio.biz.assembly.model.polymodel.vo.GetPolyModelDetailsVo;
import com.ksptool.bio.biz.assembly.model.polymodel.vo.GetPolyModelListVo;
import com.ksptool.bio.biz.assembly.service.PolyModelService;
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
@RequestMapping("/outModelPoly")
@Tag(name = "代码装配-输出方案聚合模型管理", description = "输出方案聚合模型管理")
@Slf4j
public class PolyModelController {

    @Autowired
    private PolyModelService polyModelService;

    @PostMapping("/getOutModelPolyList")
    @Operation(summary = "查询聚合模型列表")
    public PageResult<GetPolyModelListVo> getOutModelPolyList(@RequestBody @Valid GetPolyModelListDto dto) throws Exception {
        return polyModelService.getOutModelPolyList(dto);
    }

    @Operation(summary = "新增聚合模型")
    @PostMapping("/addOutModelPoly")
    public Result<String> addOutModelPoly(@RequestBody @Valid AddPolyModelDto dto) throws Exception {
        polyModelService.addOutModelPoly(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑聚合模型")
    @PostMapping("/editOutModelPoly")
    public Result<String> editOutModelPoly(@RequestBody @Valid EditPolyModelDto dto) throws Exception {
        polyModelService.editOutModelPoly(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询聚合模型详情")
    @PostMapping("/getOutModelPolyDetails")
    public Result<GetPolyModelDetailsVo> getOutModelPolyDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetPolyModelDetailsVo details = polyModelService.getOutModelPolyDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除聚合模型元素")
    @PostMapping("/removeOutModelPoly")
    public Result<String> removeOutModelPoly(@RequestBody @Valid CommonIdDto dto) throws Exception {
        polyModelService.removeOutModelPoly(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "从原始模型同步聚合模型(输出方案ID)")
    @PostMapping("/syncFromOriginBySchema")
    public Result<String> syncFromOrigin(@RequestBody @Valid CommonIdDto dto) throws Exception {
        polyModelService.syncFromOriginBySchema(dto);
        return Result.success("同步成功");
    }

}
