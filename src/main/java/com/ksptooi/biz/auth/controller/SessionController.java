package com.ksptooi.biz.auth.controller;

import com.ksptooi.biz.auth.model.session.GetSessionDetailsVo;
import com.ksptooi.biz.auth.model.session.GetSessionListDto;
import com.ksptooi.biz.auth.model.session.GetSessionListVo;
import com.ksptooi.biz.auth.service.SessionService;
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
@RequestMapping("/session")
@Tag(name = "Session", description = "会话管理")
public class SessionController {

    @Autowired
    private SessionService service;

    @Operation(summary = "获取会话列表")
    @PostMapping("getSessionList")
    public PageResult<GetSessionListVo> getSessionList(@RequestBody @Valid GetSessionListDto dto) {
        return service.getSessionList(dto);
    }

    @Operation(summary = "获取会话详情")
    @PostMapping("getSessionDetails")
    public Result<GetSessionDetailsVo> getSessionDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return Result.success(service.getSessionDetails(dto.getId()));
    }

    @Operation(summary = "关闭会话")
    @PostMapping("closeSession")
    public Result<String> closeSession(@RequestBody @Valid CommonIdDto dto) throws Exception {
        service.closeSession(dto.getId());
        return Result.success("关闭成功");
    }
}
