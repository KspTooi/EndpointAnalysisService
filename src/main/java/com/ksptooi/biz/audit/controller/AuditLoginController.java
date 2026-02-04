package com.ksptooi.biz.audit.controller;

import com.ksptooi.biz.audit.modal.auditlogin.dto.AddAuditLoginDto;
import com.ksptooi.biz.audit.modal.auditlogin.dto.EditAuditLoginDto;
import com.ksptooi.biz.audit.modal.auditlogin.dto.GetAuditLoginListDto;
import com.ksptooi.biz.audit.modal.auditlogin.vo.GetAuditLoginDetailsVo;
import com.ksptooi.biz.audit.modal.auditlogin.vo.GetAuditLoginListVo;
import com.ksptooi.biz.audit.service.AuditLoginService;
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


@RestController
@RequestMapping("/auditLogin")
@Tag(name = "auditLogin", description = "登录审计日志")
@Slf4j
public class AuditLoginController {

    @Autowired
    private AuditLoginService auditLoginService;

    @PostMapping("/getAuditLoginList")
    @Operation(summary = "列表查询")
    public PageResult<GetAuditLoginListVo> getAuditLoginList(@RequestBody @Valid GetAuditLoginListDto dto) throws Exception {
        return auditLoginService.getAuditLoginList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addAuditLogin")
    public Result<String> addAuditLogin(@RequestBody @Valid AddAuditLoginDto dto) throws Exception {
        auditLoginService.addAuditLogin(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editAuditLogin")
    public Result<String> editAuditLogin(@RequestBody @Valid EditAuditLoginDto dto) throws Exception {
        auditLoginService.editAuditLogin(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getAuditLoginDetails")
    public Result<GetAuditLoginDetailsVo> getAuditLoginDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetAuditLoginDetailsVo details = auditLoginService.getAuditLoginDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeAuditLogin")
    public Result<String> removeAuditLogin(@RequestBody @Valid CommonIdDto dto) throws Exception {
        auditLoginService.removeAuditLogin(dto);
        return Result.success("操作成功");
    }

}
