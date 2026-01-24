package com.ksptooi.biz.core.controller;


import com.ksptooi.biz.core.model.group.AddGroupDto;
import com.ksptooi.biz.core.model.group.EditGroupDto;
import com.ksptooi.biz.core.model.group.GetGroupDefinitionsVo;
import com.ksptooi.biz.core.model.group.GetGroupDetailsVo;
import com.ksptooi.biz.core.model.group.GetGroupListDto;
import com.ksptooi.biz.core.model.group.GetGroupListVo;
import com.ksptooi.biz.core.model.group.GetGroupPermissionMenuViewDto;
import com.ksptooi.biz.core.model.group.GetGroupPermissionMenuViewVo;
import com.ksptooi.biz.core.model.group.GetGroupPermissionNodeDto;
import com.ksptooi.biz.core.model.group.GetGroupPermissionNodeVo;
import com.ksptooi.biz.core.model.group.GrantAndRevokeDto;
import com.ksptooi.biz.core.service.GroupService;
import com.ksptooi.commons.annotation.PrintLog;
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
@RequestMapping("/group")
@Tag(name = "Group", description = "组管理")
public class GroupController {

    @Autowired
    private GroupService service;

    @Operation(summary = "获取组定义列表")
    @PostMapping("getGroupDefinitions")
    public Result<List<GetGroupDefinitionsVo>> getGroupDefinitions() {
        return Result.success(service.getGroupDefinitions());
    }

    @Operation(summary = "获取组列表")
    @PostMapping("getGroupList")
    public PageResult<GetGroupListVo> getGroupList(@RequestBody @Valid GetGroupListDto dto) {
        return service.getGroupList(dto);
    }

    @Operation(summary = "获取组详情")
    @PostMapping("getGroupDetails")
    public Result<GetGroupDetailsVo> getGroupDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return Result.success(service.getGroupDetails(dto.getId()));
    }

    @Operation(summary = "新增组")
    @PostMapping("addGroup")
    public Result<String> addGroup(@RequestBody @Valid AddGroupDto dto) throws Exception {
        service.addGroup(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑组")
    @PostMapping("editGroup")
    public Result<String> editGroup(@RequestBody @Valid EditGroupDto dto) throws Exception {
        service.editGroup(dto);
        return Result.success("修改成功");
    }


    @Operation(summary = "获取组权限菜单视图")
    @PostMapping("getGroupPermissionMenuView")
    public Result<List<GetGroupPermissionMenuViewVo>> getGroupPermissionMenuView(@RequestBody @Valid GetGroupPermissionMenuViewDto dto) throws Exception {
        return Result.success(service.getGroupPermissionMenuView(dto));
    }
    
    @Operation(summary = "获取组权限节点视图")
    @PostMapping("getGroupPermissionNodeView")
    public PageResult<GetGroupPermissionNodeVo> getGroupPermissionNodeView(@RequestBody @Valid GetGroupPermissionNodeDto dto) throws Exception {
        return service.getGroupPermissionNodeView(dto);
    }

    @Operation(summary = "批量授权或取消授权")
    @PostMapping("grantAndRevoke")
    public Result<String> grantAndRevoke(@RequestBody @Valid GrantAndRevokeDto dto) throws Exception {
        service.grantAndRevoke(dto);
        return Result.success("授权或取消授权成功");
    }

    @Operation(summary = "删除组")
    @PostMapping("removeGroup")
    public Result<String> removeGroup(@RequestBody @Valid CommonIdDto dto) throws Exception {
        service.removeGroup(dto.getId());
        return Result.success("删除成功");
    }
}
