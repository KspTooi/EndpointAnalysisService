package com.ksptooi.biz.auth.controller;


import com.ksptooi.biz.auth.model.group.dto.*;
import com.ksptooi.biz.auth.model.group.vo.GetGroupDetailsVo;
import com.ksptooi.biz.auth.model.group.vo.GetGroupListVo;
import com.ksptooi.biz.auth.model.group.vo.GetGroupPermissionMenuViewVo;
import com.ksptooi.biz.auth.model.group.vo.GetGroupPermissionNodeVo;
import com.ksptooi.biz.auth.service.GroupService;
import com.ksptooi.biz.core.service.MenuService;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.commons.annotation.PrintLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private MenuService menuService;


    @PreAuthorize("@auth.hasCode('auth:group:view')")
    @Operation(summary = "获取组列表")
    @PostMapping("getGroupList")
    public PageResult<GetGroupListVo> getGroupList(@RequestBody @Valid GetGroupListDto dto) {
        return service.getGroupList(dto);
    }

    @PreAuthorize("@auth.hasCode('auth:group:view')")
    @Operation(summary = "获取组详情")
    @PostMapping("getGroupDetails")
    public Result<GetGroupDetailsVo> getGroupDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return Result.success(service.getGroupDetails(dto.getId()));
    }

    @PreAuthorize("@auth.hasCode('auth:group:add')")
    @Operation(summary = "新增组")
    @PostMapping("addGroup")
    public Result<String> addGroup(@RequestBody @Valid AddGroupDto dto) throws Exception {

        //验证入参
        if (dto.validate() != null) {
            return Result.error(dto.validate());
        }

        service.addGroup(dto);
        return Result.success("新增成功");
    }

    @PreAuthorize("@auth.hasCode('auth:group:edit')")
    @Operation(summary = "编辑组")
    @PostMapping("editGroup")
    @CacheEvict(cacheNames = {"userSession", "userProfile", "menuTree"}, allEntries = true)
    public Result<String> editGroup(@RequestBody @Valid EditGroupDto dto) throws Exception {

        //验证入参
        if (dto.validate() != null) {
            return Result.error(dto.validate());
        }

        service.editGroup(dto);
        menuService.clearUserMenuTreeCache();
        return Result.success("修改成功");
    }


    @PreAuthorize("@auth.hasCode('auth:group:edit')")
    @Operation(summary = "获取组权限菜单视图")
    @PostMapping("getGroupPermissionMenuView")
    public Result<List<GetGroupPermissionMenuViewVo>> getGroupPermissionMenuView(@RequestBody @Valid GetGroupPermissionMenuViewDto dto) throws Exception {
        return Result.success(service.getGroupPermissionMenuView(dto));
    }

    @PreAuthorize("@auth.hasCode('auth:group:edit')")
    @Operation(summary = "获取组权限节点视图")
    @PostMapping("getGroupPermissionNodeView")
    public PageResult<GetGroupPermissionNodeVo> getGroupPermissionNodeView(@RequestBody @Valid GetGroupPermissionNodeDto dto) throws Exception {
        return service.getGroupPermissionNodeView(dto);
    }

    @PreAuthorize("@auth.hasCode('auth:group:edit')")
    @Operation(summary = "批量授权或取消授权")
    @PostMapping("grantAndRevoke")
    @CacheEvict(cacheNames = {"userSession", "userProfile", "menuTree"}, allEntries = true)
    public Result<String> grantAndRevoke(@RequestBody @Valid GrantAndRevokeDto dto) throws Exception {
        service.grantAndRevoke(dto);
        menuService.clearUserMenuTreeCache();
        return Result.success("授权或取消授权成功");
    }

    @PreAuthorize("@auth.hasCode('auth:group:remove')")
    @Operation(summary = "删除组")
    @PostMapping("removeGroup")
    @CacheEvict(cacheNames = {"userSession", "userProfile", "menuTree"}, allEntries = true)
    public Result<String> removeGroup(@RequestBody @Valid CommonIdDto dto) throws Exception {
        service.removeGroup(dto);
        menuService.clearUserMenuTreeCache();
        return Result.success("删除成功");
    }
}
