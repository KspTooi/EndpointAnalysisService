package com.ksptool.bio.biz.outmodelpoly.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptool.bio.biz.outmodelpoly.service.OutModelPolyService;
import com.ksptool.bio.biz.outmodelpoly.model.dto.AddOutModelPolyDto;
import com.ksptool.bio.biz.outmodelpoly.model.dto.EditOutModelPolyDto;
import com.ksptool.bio.biz.outmodelpoly.model.dto.GetOutModelPolyListDto;
import com.ksptool.bio.biz.outmodelpoly.model.vo.GetOutModelPolyListVo;
import com.ksptool.bio.biz.outmodelpoly.model.vo.GetOutModelPolyDetailsVo;


@RestController
@RequestMapping("/outModelPoly")
@Tag(name = "outModelPoly", description = "输出方案聚合模型表")
@Slf4j
public class OutModelPolyController {

    @Autowired
    private OutModelPolyService outModelPolyService;

    @PostMapping("/getOutModelPolyList")
    @Operation(summary ="列表查询")
    public PageResult<GetOutModelPolyListVo> getOutModelPolyList(@RequestBody @Valid GetOutModelPolyListDto dto) throws Exception{
        return outModelPolyService.getOutModelPolyList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addOutModelPoly")
    public Result<String> addOutModelPoly(@RequestBody @Valid AddOutModelPolyDto dto) throws Exception{
		outModelPolyService.addOutModelPoly(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editOutModelPoly")
    public Result<String> editOutModelPoly(@RequestBody @Valid EditOutModelPolyDto dto) throws Exception{
		outModelPolyService.editOutModelPoly(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getOutModelPolyDetails")
    public Result<GetOutModelPolyDetailsVo> getOutModelPolyDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetOutModelPolyDetailsVo details = outModelPolyService.getOutModelPolyDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeOutModelPoly")
    public Result<String> removeOutModelPoly(@RequestBody @Valid CommonIdDto dto) throws Exception{
        outModelPolyService.removeOutModelPoly(dto);
        return Result.success("操作成功");
    }

}
