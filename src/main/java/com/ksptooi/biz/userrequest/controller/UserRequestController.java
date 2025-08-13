package com.ksptooi.biz.userrequest.controller;

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
import com.ksptooi.biz.userrequest.model.userrequest.GetUserRequestTreeVo;
import com.ksptooi.biz.userrequest.model.userrequest.GetUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequest.GetUserRequestDetailsVo;
import com.ksptooi.biz.userrequest.model.userrequest.EditUserRequestDto;
import com.ksptooi.biz.userrequest.model.userrequest.SaveAsUserRequestDto;
import com.ksptooi.commons.utils.web.PageResult;

import java.util.List;

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

    @PostMapping("/getUserRequestTree")
    @Operation(summary ="获取用户请求树")
    public Result<List<GetUserRequestTreeVo>> getUserRequestTree(@RequestBody @Valid GetUserRequestTreeDto dto) throws Exception{
        return Result.success(userRequestService.getUserRequestTree(dto));
    }


    @Operation(summary ="编辑")
    @PostMapping("/editUserRequest")
    public Result<String> editUserRequest(@RequestBody @Valid EditUserRequestDto dto) throws Exception{
        userRequestService.editUserRequest(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getUserRequestDetails")
    public Result<GetUserRequestDetailsVo> getUserRequestDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetUserRequestDetailsVo details = userRequestService.getUserRequestDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeUserRequest")
    public Result<String> removeUserRequest(@RequestBody @Valid CommonIdDto dto) throws Exception{
        userRequestService.removeUserRequest(dto);
        return Result.success("操作成功");
    }

}
