package com.ksptooi.biz.userrequest.controller;

import com.ksptooi.biz.userrequest.model.userrequest.RemoveUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequesttree.dto.AddUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequesttree.dto.EditUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequesttree.dto.GetUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequesttree.dto.MoveUserRequestTreeDto;
import com.ksptooi.biz.userrequest.model.userrequesttree.vo.GetUserRequestTreeVo;
import com.ksptooi.biz.userrequest.service.UserRequestTreeService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.utils.web.CommonIdDto;
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
@RequestMapping("/userRequestTree")
@Tag(name = "用户请求树", description = "用户请求树")
@Slf4j
public class UserRequestTreeController {

    @Autowired
    private UserRequestTreeService userRequestTreeService;


    @PostMapping("/getUserRequestTree")
    @Operation(summary = "获取用户请求树")
    public Result<List<GetUserRequestTreeVo>> getUserRequestTree(@RequestBody @Valid GetUserRequestTreeDto dto) throws Exception {
        return Result.success(userRequestTreeService.getUserRequestTree(dto));
    }

    @PostMapping("/addUserRequestTree")
    @Operation(summary = "新增用户请求树")
    public Result<String> addUserRequestTree(@RequestBody @Valid AddUserRequestTreeDto dto) throws Exception {
        userRequestTreeService.addUserRequestTree(dto);
        return Result.success("新增成功");
    }

    @PostMapping("/moveUserRequestTree")
    @Operation(summary = "移动用户请求树")
    public Result<List<GetUserRequestTreeVo>> moveUserRequestTree(@RequestBody @Valid MoveUserRequestTreeDto dto) throws Exception {
        return Result.success(userRequestTreeService.moveUserRequestTree(dto));
    }

    @PostMapping("/copyUserRequestTree")
    @Operation(summary = "复制用户请求树")
    public Result<String> copyUserRequestTree(@RequestBody @Valid CommonIdDto dto) throws Exception {
        userRequestTreeService.copyUserRequestTree(dto);
        return Result.success("复制成功");
    }


    @PostMapping("/editUserRequestTree")
    @Operation(summary = "编辑用户请求树")
    public Result<String> editUserRequestTree(@RequestBody @Valid EditUserRequestTreeDto dto) throws Exception {
        userRequestTreeService.editUserRequestTree(dto);
        return Result.success("修改成功");
    }

    @PostMapping("/removeUserRequestTree")
    @Operation(summary = "移除用户请求树对象")
    public Result<String> removeUserRequestTree(@RequestBody @Valid RemoveUserRequestTreeDto dto) throws Exception {
        userRequestTreeService.removeUserRequestTree(dto);
        return Result.success("修改成功");
    }


}
