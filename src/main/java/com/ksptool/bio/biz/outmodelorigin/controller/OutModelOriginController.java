package com.ksptool.bio.biz.outmodelorigin.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptool.bio.biz.outmodelorigin.service.OutModelOriginService;
import com.ksptool.bio.biz.outmodelorigin.model.dto.AddOutModelOriginDto;
import com.ksptool.bio.biz.outmodelorigin.model.dto.EditOutModelOriginDto;
import com.ksptool.bio.biz.outmodelorigin.model.dto.GetOutModelOriginListDto;
import com.ksptool.bio.biz.outmodelorigin.model.vo.GetOutModelOriginListVo;
import com.ksptool.bio.biz.outmodelorigin.model.vo.GetOutModelOriginDetailsVo;


@RestController
@RequestMapping("/outModelOrigin")
@Tag(name = "输出方案原始模型管理", description = "输出方案原始模型管理")
@Slf4j
public class OutModelOriginController {

    @Autowired
    private OutModelOriginService outModelOriginService;

    @PostMapping("/getOutModelOriginList")
    @Operation(summary ="查询原始模型列表")
    public PageResult<GetOutModelOriginListVo> getOutModelOriginList(@RequestBody @Valid GetOutModelOriginListDto dto) throws Exception{
        return outModelOriginService.getOutModelOriginList(dto);
    }

    @Operation(summary ="新增原始模型")
    @PostMapping("/addOutModelOrigin")
    public Result<String> addOutModelOrigin(@RequestBody @Valid AddOutModelOriginDto dto) throws Exception{
		outModelOriginService.addOutModelOrigin(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑原始模型")
    @PostMapping("/editOutModelOrigin")
    public Result<String> editOutModelOrigin(@RequestBody @Valid EditOutModelOriginDto dto) throws Exception{
		outModelOriginService.editOutModelOrigin(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询原始模型详情")
    @PostMapping("/getOutModelOriginDetails")
    public Result<GetOutModelOriginDetailsVo> getOutModelOriginDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetOutModelOriginDetailsVo details = outModelOriginService.getOutModelOriginDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除原始模型元素")
    @PostMapping("/removeOutModelOrigin")
    public Result<String> removeOutModelOrigin(@RequestBody @Valid CommonIdDto dto) throws Exception{
        outModelOriginService.removeOutModelOrigin(dto);
        return Result.success("操作成功");
    }

}
