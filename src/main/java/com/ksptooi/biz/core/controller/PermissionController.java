package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.permission.AddPermissionDto;
import com.ksptooi.biz.core.model.permission.EditPermissionDto;
import com.ksptooi.biz.core.model.permission.GetPermissionDefinitionVo;
import com.ksptooi.biz.core.model.permission.GetPermissionDetailsVo;
import com.ksptooi.biz.core.model.permission.GetPermissionListDto;
import com.ksptooi.biz.core.model.permission.GetPermissionListVo;
import com.ksptooi.biz.core.service.PermissionService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.annotation.RequirePermissionRest;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PrintLog
@RestController
@RequestMapping("/permission")
@Tag(name = "Permission", description = "权限管理")
public class PermissionController {

    @Autowired
    private PermissionService service;


    @Operation(summary = "获取权限定义列表")
    @PostMapping("getPermissionDefinition")
    public Result<List<GetPermissionDefinitionVo>> getPermissionDefinition() {
        return Result.success(service.getPermissionDefinition());
    }

    @Operation(summary = "获取权限列表")
    @PostMapping("getPermissionList")
    @RequirePermissionRest("admin:permission:view")
    public PageResult<GetPermissionListVo> getPermissionList(@RequestBody @Valid GetPermissionListDto dto) {
        return service.getPermissionList(dto);
    }

    @Operation(summary = "获取权限详情")
    @PostMapping("getPermissionDetails")
    @RequirePermissionRest("admin:permission:save")
    public Result<GetPermissionDetailsVo> getPermissionDetails(@RequestBody @Valid CommonIdDto dto) {
        try {
            return Result.success(service.getPermissionDetails(dto.getId()));
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }

    @Operation(summary = "新增权限")
    @PostMapping("addPermission")
    @RequirePermissionRest("admin:permission:save")
    public Result<String> addPermission(@RequestBody @Valid AddPermissionDto dto) {
        try {
            service.addPermission(dto);
            return Result.success("新增成功");
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }

    @Operation(summary = "编辑权限")
    @PostMapping("editPermission")
    @RequirePermissionRest("admin:permission:save")
    public Result<String> editPermission(@RequestBody @Valid EditPermissionDto dto) {
        try {
            service.editPermission(dto);
            return Result.success("修改成功");
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }

    @Operation(summary = "删除权限")
    @PostMapping("removePermission")
    @RequirePermissionRest("admin:permission:remove")
    public Result<String> removePermission(@RequestBody @Valid CommonIdDto dto) {
        try {
            service.removePermission(dto.getId());
            return Result.success("success");
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }
}
