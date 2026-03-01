package com.ksptool.bio.biz.qfmodelgroup.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptool.bio.biz.qfmodelgroup.service.QfModelGroupService;
import com.ksptool.bio.biz.qfmodelgroup.model.dto.AddQfModelGroupDto;
import com.ksptool.bio.biz.qfmodelgroup.model.dto.EditQfModelGroupDto;
import com.ksptool.bio.biz.qfmodelgroup.model.dto.GetQfModelGroupListDto;
import com.ksptool.bio.biz.qfmodelgroup.model.vo.GetQfModelGroupListVo;
import com.ksptool.bio.biz.qfmodelgroup.model.vo.GetQfModelGroupDetailsVo;


@RestController
@RequestMapping("/qfModelGroup")
@Tag(name = "QF流程模型分组管理", description = "QF流程模型分组管理")
@Slf4j
public class QfModelGroupController {

    @Autowired
    private QfModelGroupService qfModelGroupService;

    @PostMapping("/getQfModelGroupList")
    @Operation(summary ="查询流程模型分组列表")
    public PageResult<GetQfModelGroupListVo> getQfModelGroupList(@RequestBody @Valid GetQfModelGroupListDto dto) throws Exception{
        return qfModelGroupService.getQfModelGroupList(dto);
    }

    @Operation(summary ="新增流程模型分组")
    @PostMapping("/addQfModelGroup")
    public Result<String> addQfModelGroup(@RequestBody @Valid AddQfModelGroupDto dto) throws Exception{
		qfModelGroupService.addQfModelGroup(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑流程模型分组")
    @PostMapping("/editQfModelGroup")
    public Result<String> editQfModelGroup(@RequestBody @Valid EditQfModelGroupDto dto) throws Exception{
		qfModelGroupService.editQfModelGroup(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询流程模型分组详情")
    @PostMapping("/getQfModelGroupDetails")
    public Result<GetQfModelGroupDetailsVo> getQfModelGroupDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetQfModelGroupDetailsVo details = qfModelGroupService.getQfModelGroupDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除流程模型分组")
    @PostMapping("/removeQfModelGroup")
    public Result<String> removeQfModelGroup(@RequestBody @Valid CommonIdDto dto) throws Exception{
        qfModelGroupService.removeQfModelGroup(dto);
        return Result.success("操作成功");
    }

}
