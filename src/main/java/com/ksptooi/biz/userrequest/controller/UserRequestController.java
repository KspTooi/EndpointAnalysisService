package com.ksptooi.biz.userrequest.controller;

import com.ksptooi.biz.userrequest.model.userrequest.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.Result;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.biz.userrequest.service.UserRequestService;

@PrintLog
@RestController
@RequestMapping("/userRequest")
@Tag(name = "用户请求", description = "用户请求记录")
@Slf4j
public class UserRequestController {

    @Autowired
    private UserRequestService userRequestService;

    @Operation(summary ="保存原始请求为用户请求")
    @PostMapping("/saveAsUserRequest")
    public Result<String> saveAsUserRequest(@RequestBody @Valid SaveAsUserRequestDto dto) throws Exception{
        userRequestService.saveAsUserRequest(dto);
        return Result.success("保存成功");
    }

    @Operation(summary ="编辑用户请求")
    @PostMapping("/editUserRequest")
    public Result<String> editUserRequest(@RequestBody @Valid EditUserRequestDto dto) throws Exception{
        userRequestService.editUserRequest(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询用户请求详情")
    @PostMapping("/getUserRequestDetails")
    public Result<GetUserRequestDetailsVo> getUserRequestDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetUserRequestDetailsVo details = userRequestService.getUserRequestDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除用户请求")
    @PostMapping("/removeUserRequest")
    public Result<String> removeUserRequest(@RequestBody @Valid CommonIdDto dto) throws Exception{
        userRequestService.removeUserRequest(dto);
        return Result.success("操作成功");
    }

}
