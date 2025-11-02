package com.ksptooi.biz.core.controller;


import com.ksptooi.biz.core.model.group.AddGroupDto;
import com.ksptooi.biz.core.model.group.EditGroupDto;
import com.ksptooi.biz.core.model.group.GetGroupDefinitionsVo;
import com.ksptooi.biz.core.model.group.GetGroupDetailsVo;
import com.ksptooi.biz.core.model.group.GetGroupListDto;
import com.ksptooi.biz.core.model.group.GetGroupListVo;
import com.ksptooi.biz.core.service.GroupService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.annotation.RequirePermissionRest;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import com.ksptooi.commons.utils.web.Result;
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
    @RequirePermissionRest("admin:group:view")
    public PageResult<GetGroupListVo> getGroupList(@RequestBody @Valid GetGroupListDto dto) {
        return service.getGroupList(dto);
    }

    @Operation(summary = "获取组详情")
    @PostMapping("getGroupDetails")
    @RequirePermissionRest("admin:group:save")
    public Result<GetGroupDetailsVo> getGroupDetails(@RequestBody @Valid CommonIdDto dto) {
        try {
            return Result.success(service.getGroupDetails(dto.getId()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "新增组")
    @PostMapping("addGroup")
    @RequirePermissionRest("admin:group:save")
    public Result<String> addGroup(@RequestBody @Valid AddGroupDto dto) {
        try {
            service.addGroup(dto);
            return Result.success("新增成功");
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }

    @Operation(summary = "编辑组")
    @PostMapping("editGroup")
    @RequirePermissionRest("admin:group:save")
    public Result<String> editGroup(@RequestBody @Valid EditGroupDto dto) {
        try {
            service.editGroup(dto);
            return Result.success("修改成功");
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }

    @Operation(summary = "删除组")
    @PostMapping("removeGroup")
    @RequirePermissionRest("admin:group:delete")
    public Result<String> removeGroup(@RequestBody @Valid CommonIdDto dto) {
        try {
            service.removeGroup(dto.getId());
            return Result.success("success");
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }
}
