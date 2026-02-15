package com.ksptooi.biz.authgroupdept.controller;

import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptooi.biz.authgroupdept.service.AuthGroupDeptService;
import com.ksptooi.biz.authgroupdept.model.dto.AddAuthGroupDeptDto;
import com.ksptooi.biz.authgroupdept.model.dto.EditAuthGroupDeptDto;
import com.ksptooi.biz.authgroupdept.model.dto.GetAuthGroupDeptListDto;
import com.ksptooi.biz.authgroupdept.model.vo.GetAuthGroupDeptListVo;
import com.ksptooi.biz.authgroupdept.model.vo.GetAuthGroupDeptDetailsVo;


@RestController
@RequestMapping("/authGroupDept")
@Tag(name = "authGroupDept", description = "GD表")
@Slf4j
public class AuthGroupDeptController {

    @Autowired
    private AuthGroupDeptService authGroupDeptService;

    @PostMapping("/getAuthGroupDeptList")
    @Operation(summary ="列表查询")
    public PageResult<GetAuthGroupDeptListVo> getAuthGroupDeptList(@RequestBody @Valid GetAuthGroupDeptListDto dto) throws Exception{
        return authGroupDeptService.getAuthGroupDeptList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addAuthGroupDept")
    public Result<String> addAuthGroupDept(@RequestBody @Valid AddAuthGroupDeptDto dto) throws Exception{
		authGroupDeptService.addAuthGroupDept(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editAuthGroupDept")
    public Result<String> editAuthGroupDept(@RequestBody @Valid EditAuthGroupDeptDto dto) throws Exception{
		authGroupDeptService.editAuthGroupDept(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getAuthGroupDeptDetails")
    public Result<GetAuthGroupDeptDetailsVo> getAuthGroupDeptDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetAuthGroupDeptDetailsVo details = authGroupDeptService.getAuthGroupDeptDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeAuthGroupDept")
    public Result<String> removeAuthGroupDept(@RequestBody @Valid CommonIdDto dto) throws Exception{
        authGroupDeptService.removeAuthGroupDept(dto);
        return Result.success("操作成功");
    }

}
