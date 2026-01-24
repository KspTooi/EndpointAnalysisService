package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.user.AddUserDto;
import com.ksptooi.biz.core.model.user.EditUserDto;
import com.ksptooi.biz.core.model.user.GetUserDetailsVo;
import com.ksptooi.biz.core.model.user.GetUserListDto;
import com.ksptooi.biz.core.model.user.GetUserListVo;
import com.ksptooi.biz.core.service.UserService;
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

@PrintLog
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "用户管理")
public class UserController {

    @Autowired
    private UserService service;

    @Operation(summary = "获取用户列表")
    @PostMapping("getUserList")
    public PageResult<GetUserListVo> getUserList(@RequestBody @Valid GetUserListDto dto) {
        return service.getUserList(dto);
    }

    @Operation(summary = "获取用户详情")
    @PostMapping("getUserDetails")
    public Result<GetUserDetailsVo> getUserDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return Result.success(service.getUserDetails(dto.getId()));
    }

    @Operation(summary = "新增用户")
    @PrintLog(sensitiveFields = "password")
    @PostMapping("addUser")
    public Result<String> addUser(@RequestBody @Valid AddUserDto dto) throws Exception {
        service.addUser(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑用户")
    @PrintLog(sensitiveFields = "password")
    @PostMapping("editUser")
    public Result<String> editUser(@RequestBody @Valid EditUserDto dto) throws Exception {
        service.editUser(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "删除用户")
    @PostMapping("removeUser")
    public Result<String> removeUser(@RequestBody @Valid CommonIdDto dto) throws Exception {
        service.removeUser(dto.getId());
        return Result.success("success");
    }

}
