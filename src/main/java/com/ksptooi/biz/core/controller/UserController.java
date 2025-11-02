package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.user.AddUserDto;
import com.ksptooi.biz.core.model.user.EditUserDto;
import com.ksptooi.biz.core.model.user.GetUserDetailsVo;
import com.ksptooi.biz.core.model.user.GetUserListDto;
import com.ksptooi.biz.core.model.user.GetUserListVo;
import com.ksptooi.biz.core.service.UserService;
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

@PrintLog
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "用户管理")
public class UserController {

    @Autowired
    private UserService service;

    @Operation(summary = "获取用户列表")
    @PostMapping("getUserList")
    @RequirePermissionRest("admin:user:view")
    public PageResult<GetUserListVo> getUserList(@RequestBody @Valid GetUserListDto dto) {
        return service.getUserList(dto);
    }

    @Operation(summary = "获取用户详情")
    @PostMapping("getUserDetails")
    @RequirePermissionRest("admin:user:save")
    public Result<GetUserDetailsVo> getUserDetails(@RequestBody @Valid CommonIdDto dto) {
        try {
            return Result.success(service.getUserDetails(dto.getId()));
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }

    @Operation(summary = "新增用户")
    @PrintLog(sensitiveFields = "password")
    @PostMapping("addUser")
    @RequirePermissionRest("admin:user:save")
    public Result<String> addUser(@RequestBody @Valid AddUserDto dto) {
        try {
            service.addUser(dto);
            return Result.success("新增成功");
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }

    @Operation(summary = "编辑用户")
    @PrintLog(sensitiveFields = "password")
    @PostMapping("editUser")
    @RequirePermissionRest("admin:user:save")
    public Result<String> editUser(@RequestBody @Valid EditUserDto dto) {
        try {
            service.editUser(dto);
            return Result.success("修改成功");
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }

    @Operation(summary = "删除用户")
    @PostMapping("removeUser")
    @RequirePermissionRest("admin:user:delete")
    public Result<String> removeUser(@RequestBody @Valid CommonIdDto dto) {
        try {
            service.removeUser(dto.getId());
            return Result.success("success");
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }

}
