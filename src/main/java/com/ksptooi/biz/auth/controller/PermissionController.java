package com.ksptooi.biz.auth.controller;

import com.ksptooi.biz.auth.model.permission.dto.AddPermissionDto;
import com.ksptooi.biz.auth.model.permission.dto.EditPermissionDto;
import com.ksptooi.biz.auth.model.permission.dto.GetPermissionListDto;
import com.ksptooi.biz.auth.model.permission.vo.GetPermissionDefinitionVo;
import com.ksptooi.biz.auth.model.permission.vo.GetPermissionDetailsVo;
import com.ksptooi.biz.auth.model.permission.vo.GetPermissionListVo;
import com.ksptooi.biz.auth.service.PermissionService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
    public PageResult<GetPermissionListVo> getPermissionList(@RequestBody @Valid GetPermissionListDto dto) {
        return service.getPermissionList(dto);
    }

    @Operation(summary = "获取权限详情")
    @PostMapping("getPermissionDetails")
    public Result<GetPermissionDetailsVo> getPermissionDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return Result.success(service.getPermissionDetails(dto.getId()));
    }

    @Operation(summary = "新增权限")
    @PostMapping("addPermission")
    public Result<String> addPermission(@RequestBody @Valid AddPermissionDto dto) throws Exception {
        service.addPermission(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑权限")
    @PostMapping("editPermission")
    public Result<String> editPermission(@RequestBody @Valid EditPermissionDto dto) throws Exception {
        service.editPermission(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "删除权限")
    @PostMapping("removePermission")
    @CacheEvict(cacheNames = {"userSession", "userProfile", "menuTree"}, allEntries = true)
    public Result<String> removePermission(@RequestBody @Valid CommonIdDto dto) throws Exception {
        service.removePermission(dto);
        return Result.success("success");
    }
}
