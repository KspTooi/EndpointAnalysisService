package com.ksptooi.biz.requestdebug.controller;

import com.ksptooi.biz.requestdebug.model.userrequestgroup.EditUserRequestGroupDto;
import com.ksptooi.biz.requestdebug.model.userrequestgroup.GetUserRequestGroupDetailsVo;
import com.ksptooi.biz.requestdebug.service.UserRequestGroupService;
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


    @Operation(summary = "编辑请求组")
    @PostMapping("/editUserRequestGroup")
    public Result<String> editUserRequestGroup(@RequestBody @Valid EditUserRequestGroupDto dto) throws Exception {
        userRequestGroupService.editUserRequestGroup(dto);
        return Result.success("修改成功");
    }


}
