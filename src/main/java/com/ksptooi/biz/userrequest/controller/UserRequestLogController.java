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
import com.ksptooi.biz.userrequest.service.UserRequestLogService;
import com.ksptooi.biz.userrequest.model.userrequestlog.GetUserRequestLogListVo;
import com.ksptooi.biz.userrequest.model.userrequestlog.GetUserRequestLogListDto;
import com.ksptooi.biz.userrequest.model.userrequestlog.GetUserRequestLogDetailsVo;
import com.ksptooi.biz.userrequest.model.userrequestlog.EditUserRequestLogDto;
import com.ksptooi.biz.userrequest.model.userrequestlog.AddUserRequestLogDto;
import com.ksptooi.commons.utils.web.PageResult;

@PrintLog
@RestController
@RequestMapping("/userRequestLog")
@Tag(name = "UserRequestLog", description = "用户请求记录")
@Slf4j
public class UserRequestLogController {

    @Autowired
    private UserRequestLogService userRequestLogService;

    @PostMapping("/getUserRequestLogList")
    @Operation(summary ="列表查询")
    public PageResult<GetUserRequestLogListVo> getUserRequestLogList(@RequestBody @Valid GetUserRequestLogListDto dto) throws Exception {
        return userRequestLogService.getUserRequestLogList(dto);
    }
    
    @Operation(summary ="查询详情")
    @PostMapping("/getUserRequestLogDetails")
    public Result<GetUserRequestLogDetailsVo> getUserRequestLogDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetUserRequestLogDetailsVo details = userRequestLogService.getUserRequestLogDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="查询最新的响应")
    @PostMapping("/getLastUserRequestLogDetails")
    public Result<GetUserRequestLogDetailsVo> getLastUserRequestLogDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetUserRequestLogDetailsVo details = userRequestLogService.getLastUserRequestLogDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeUserRequestLog")
    public Result<String> removeUserRequestLog(@RequestBody @Valid CommonIdDto dto) throws Exception{
        userRequestLogService.removeUserRequestLog(dto);
        return Result.success("操作成功");
    }

}
