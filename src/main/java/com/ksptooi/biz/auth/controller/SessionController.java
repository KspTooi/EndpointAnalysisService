package com.ksptooi.biz.auth.controller;

import com.ksptooi.biz.auth.model.session.vo.GetSessionDetailsVo;
import com.ksptooi.biz.auth.model.session.dto.GetSessionListDto;
import com.ksptooi.biz.auth.model.session.vo.GetSessionListVo;
import com.ksptooi.biz.auth.service.SessionService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("@auth.hasCode('auth:session:view')")
    @Operation(summary = "获取在线用户列表")
    @PostMapping("getSessionList")
    public PageResult<GetSessionListVo> getSessionList(@RequestBody @Valid GetSessionListDto dto) {
        return service.getSessionList(dto);
    }

    @PreAuthorize("@auth.hasCode('auth:session:view')")
    @Operation(summary = "获取在线用户详情")
    @PostMapping("getSessionDetails")
    public Result<GetSessionDetailsVo> getSessionDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return Result.success(service.getSessionDetails(dto.getId()));
    }

    @PreAuthorize("@auth.hasCode('auth:session:remove')")
    @Operation(summary = "关闭在线用户会话", description = "强退用户时使用")
    @PostMapping("closeSession")
    public Result<String> closeSession(@RequestBody @Valid CommonIdDto dto) throws Exception {
        service.closeSessionByPrimaryKey(dto.getId());
        return Result.success("关闭成功");
    }
}
