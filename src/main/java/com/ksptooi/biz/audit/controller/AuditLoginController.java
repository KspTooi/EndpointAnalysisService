package com.ksptooi.biz.audit.controller;

import com.ksptooi.biz.audit.modal.auditlogin.dto.GetAuditLoginListDto;
import com.ksptooi.biz.audit.modal.auditlogin.vo.GetAuditLoginDetailsVo;
import com.ksptooi.biz.audit.modal.auditlogin.vo.GetAuditLoginListVo;
import com.ksptooi.biz.audit.service.AuditLoginService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
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
@RequestMapping("/auditLogin")
@Tag(name = "auditLogin", description = "登录审计日志")
@Slf4j
public class AuditLoginController {

    @Autowired
    private AuditLoginService auditLoginService;

    @PostMapping("/getAuditLoginList")
    @Operation(summary = "获取登录日志列表")
    public PageResult<GetAuditLoginListVo> getAuditLoginList(@RequestBody @Valid GetAuditLoginListDto dto) throws Exception {
        return auditLoginService.getAuditLoginList(dto);
    }
    
    @Operation(summary = "获取登录日志详情")
    @PostMapping("/getAuditLoginDetails")
    public Result<GetAuditLoginDetailsVo> getAuditLoginDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetAuditLoginDetailsVo details = auditLoginService.getAuditLoginDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除登录日志")
    @PostMapping("/removeAuditLogin")
    public Result<String> removeAuditLogin(@RequestBody @Valid CommonIdDto dto) throws Exception {
        auditLoginService.removeAuditLogin(dto);
        return Result.success("操作成功");
    }

}
