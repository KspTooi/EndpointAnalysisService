package com.ksptooi.biz.user.controller;

import com.ksptooi.biz.user.model.session.GetSessionDetailsVo;
import com.ksptooi.biz.user.model.session.GetSessionListDto;
import com.ksptooi.biz.user.model.session.GetSessionListVo;
import com.ksptooi.biz.user.service.SessionService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.annotation.RequirePermissionRest;
import com.ksptooi.commons.exception.BizException;
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
@RequestMapping("/session")
@Tag(name = "Session", description = "会话管理")
public class SessionController {

    @Autowired
    private SessionService service;

    @Operation(summary = "获取会话列表")
    @PostMapping("getSessionList")
    @RequirePermissionRest("admin:session:view")
    public PageResult<GetSessionListVo> getSessionList(@RequestBody @Valid GetSessionListDto dto) {
        return service.getSessionList(dto);
    }

    @Operation(summary = "获取会话详情")
    @PostMapping("getSessionDetails")
    @RequirePermissionRest("admin:session:view")
    public Result<GetSessionDetailsVo> getSessionDetails(@RequestBody @Valid CommonIdDto dto) {
        try {
            return Result.success(service.getSessionDetails(dto.getId()));
        } catch (BizException ex) {
            return Result.error(ex.getMessage());
        }
    }

    @Operation(summary = "关闭会话")
    @PostMapping("closeSession")
    @RequirePermissionRest("admin:session:close")
    public Result<String> closeSession(@RequestBody @Valid CommonIdDto dto) {
        try {
            service.closeSession(dto.getId());
            return Result.success("关闭成功");
        } catch (BizException ex) {
            return Result.error(ex.getMessage());
        }
    }
}
