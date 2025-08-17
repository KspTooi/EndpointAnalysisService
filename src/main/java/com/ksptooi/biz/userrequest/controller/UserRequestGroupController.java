package com.ksptooi.biz.userrequest.controller;

import com.ksptooi.biz.userrequest.model.userrequestgroup.*;
import com.ksptooi.biz.userrequest.service.UserRequestGroupService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import com.ksptooi.commons.utils.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PrintLog
@RestController
@RequestMapping("/userRequestGroup")
@Tag(name = "UserRequestGroup", description = "请求组中的请求")
@Slf4j
public class UserRequestGroupController {

    @Autowired
    private UserRequestGroupService userRequestGroupService;


    @Operation(summary = "查询请求组详情")
    @PostMapping("/getUserRequestGroupDetails")
    public Result<GetUserRequestGroupDetailsVo> getUserRequestGroupDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetUserRequestGroupDetailsVo details = userRequestGroupService.getUserRequestGroupDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }


    @PostMapping("/getUserRequestGroupList")
    @Operation(summary = "查询请求组列表")
    public PageResult<GetUserRequestGroupListVo> getUserRequestGroupList(@RequestBody @Valid GetUserRequestGroupListDto dto) throws Exception {
        return userRequestGroupService.getUserRequestGroupList(dto);
    }

    @Operation(summary = "新增请求组")
    @PostMapping("/addUserRequestGroup")
    public Result<String> addUserRequestGroup(@RequestBody @Valid AddUserRequestGroupDto dto) throws Exception {
        userRequestGroupService.addUserRequestGroup(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑请求组")
    @PostMapping("/editUserRequestGroup")
    public Result<String> editUserRequestGroup(@RequestBody @Valid EditUserRequestGroupDto dto) throws Exception {
        userRequestGroupService.editUserRequestGroup(dto);
        return Result.success("修改成功");
    }


    @Operation(summary = "删除请求组")
    @PostMapping("/removeUserRequestGroup")
    public Result<String> removeUserRequestGroup(@RequestBody @Valid CommonIdDto dto) throws Exception {
        userRequestGroupService.removeUserRequestGroup(dto);
        return Result.success("操作成功");
    }

}
