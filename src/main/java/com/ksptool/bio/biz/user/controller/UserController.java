package com.ksptool.bio.biz.user.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import com.ksptool.bio.commons.annotation.PrintLog;

import com.ksptool.bio.biz.user.service.UserService;
import com.ksptool.bio.biz.user.model.dto.AddUserDto;
import com.ksptool.bio.biz.user.model.dto.EditUserDto;
import com.ksptool.bio.biz.user.model.dto.GetUserListDto;
import com.ksptool.bio.biz.user.model.vo.GetUserListVo;
import com.ksptool.bio.biz.user.model.vo.GetUserDetailsVo;

@PrintLog
@RestController
@RequestMapping("/user")
@Tag(name = "", description = "")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("@auth.hasCode('core:user:view')")
    @PostMapping("/getUserList")
    @Operation(summary ="查询列表")
    public PageResult<GetUserListVo> getUserList(@RequestBody @Valid GetUserListDto dto) throws Exception{
        return userService.getUserList(dto);
    }

    @PreAuthorize("@auth.hasCode('core:user:add')")
    @Operation(summary ="新增")
    @PostMapping("/addUser")
    public Result<String> addUser(@RequestBody @Valid AddUserDto dto) throws Exception{
		userService.addUser(dto);
        return Result.success("新增成功");
    }

    @PreAuthorize("@auth.hasCode('core:user:edit')")
    @Operation(summary ="编辑")
    @PostMapping("/editUser")
    public Result<String> editUser(@RequestBody @Valid EditUserDto dto) throws Exception{
		userService.editUser(dto);
        return Result.success("修改成功");
    }

    @PreAuthorize("@auth.hasCode('core:user:view')")
    @Operation(summary ="查询详情")
    @PostMapping("/getUserDetails")
    public Result<GetUserDetailsVo> getUserDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetUserDetailsVo details = userService.getUserDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @PreAuthorize("@auth.hasCode('core:user:remove')")
    @Operation(summary ="删除")
    @PostMapping("/removeUser")
    public Result<String> removeUser(@RequestBody @Valid CommonIdDto dto) throws Exception{
        userService.removeUser(dto);
        return Result.success("操作成功");
    }

}
