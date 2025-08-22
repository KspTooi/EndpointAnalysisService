package com.ksptooi.biz.userrequest.controller;

import com.ksptooi.biz.userrequest.model.userrequest.EditUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequest.GetUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequest.GetUserRequestTreeVo;
import com.ksptooi.biz.userrequest.model.userrequest.RemoveUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequesttree.dto.AddUserRequestTreeDto;
import com.ksptooi.biz.userrequest.repository.UserRequestTreeRepository;
import com.ksptooi.biz.userrequest.service.UserRequestService;
import com.ksptooi.biz.userrequest.service.UserRequestTreeService;
import com.ksptooi.commons.annotation.PrintLog;
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

import java.util.List;

@PrintLog
@RestController
@RequestMapping("/userRequest")
@Tag(name = "用户请求树", description = "用户请求树")
@Slf4j
public class UserRequestTreeController {

    @Autowired
    private UserRequestService userRequestService;

    @Autowired
    private UserRequestTreeService userRequestTreeService;


    @PostMapping("/getUserRequestTree")
    @Operation(summary ="获取用户请求树")
    public Result<List<GetUserRequestTreeVo>> getUserRequestTree(@RequestBody @Valid GetUserRequestTreeDto dto) throws Exception{
        return Result.success(userRequestService.getUserRequestTree(dto));
    }

    @PostMapping("/addUserRequestTree")
    @Operation(summary = "新增用户请求树")
    public Result<String> addUserRequestTree(@RequestBody @Valid AddUserRequestTreeDto dto) throws Exception{
        userRequestService.addUserRequestTree(dto);
        return Result.success("新增成功");
    }


    @PostMapping("/editUserRequestTree")
    @Operation(summary ="编辑用户请求树")
    public Result<String> editUserRequestTree(@RequestBody @Valid EditUserRequestTreeDto dto) throws Exception{
        userRequestService.editUserRequestTree(dto);
        return Result.success("修改成功");
    }

    @PostMapping("/removeUserRequestTree")
    @Operation(summary ="移除用户请求树对象")
    public Result<String> removeUserRequestTree(@RequestBody @Valid RemoveUserRequestTreeDto dto) throws Exception{
        userRequestService.removeUserRequestTree(dto);
        return Result.success("修改成功");
    }



}
