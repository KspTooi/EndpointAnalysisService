package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.companymember.dto.*;
import com.ksptooi.biz.core.model.companymember.vo.GetCompanyMemberDetailsVo;
import com.ksptooi.biz.core.model.companymember.vo.GetCompanyMemberListVo;
import com.ksptooi.biz.core.model.companymember.vo.GetCurrentUserActiveCompanyMemberListVo;
import com.ksptooi.biz.core.service.CompanyMemberService;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.commons.annotation.PrintLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PrintLog
@RestController
@RequestMapping("/companyMember")
@Tag(name = "公司成员管理", description = "公司成员管理")
@Slf4j
public class CompanyMemberController {

    @Autowired
    private CompanyMemberService companyMemberService;

    @PreAuthorize("@auth.hasCode('core:company:member:view')")
    @PostMapping("/getCompanyMemberList")
    @Operation(summary = "获取公司成员列表")
    public PageResult<GetCompanyMemberListVo> getCompanyMemberList(@RequestBody @Valid GetCompanyMemberListDto dto) throws Exception {
        return companyMemberService.getCompanyMemberList(dto);
    }

    @PreAuthorize("@auth.hasCode('core:company:member:view')")
    @Operation(summary = "获取当前用户激活的公司成员列表")
    @PostMapping("/getCurrentUserActiveCompanyMemberList")
    public Result<GetCurrentUserActiveCompanyMemberListVo> getCurrentUserActiveCompanyMemberList(@RequestBody @Valid GetCurrentUserActiveCompanyMemberListDto dto) throws Exception {
        return companyMemberService.getCurrentUserActiveCompanyMemberList(dto);
    }

    @PreAuthorize("@auth.hasCode('core:company:member:add')")
    @Operation(summary = "新增公司成员")
    @PostMapping("/addCompanyMember")
    public Result<String> addCompanyMember(@RequestBody @Valid AddCompanyMemberDto dto) throws Exception {
        companyMemberService.addCompanyMember(dto);
        return Result.success("新增成功");
    }

    @PreAuthorize("@auth.hasCode('core:company:member:edit')")
    @Operation(summary = "编辑公司成员")
    @PostMapping("/editCompanyMember")
    public Result<String> editCompanyMember(@RequestBody @Valid EditCompanyMemberDto dto) throws Exception {
        companyMemberService.editCompanyMember(dto);
        return Result.success("修改成功");
    }

    @PreAuthorize("@auth.hasCode('core:company:member:view')")
    @Operation(summary = "获取公司成员详情")
    @PostMapping("/getCompanyMemberDetails")
    public Result<GetCompanyMemberDetailsVo> getCompanyMemberDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetCompanyMemberDetailsVo details = companyMemberService.getCompanyMemberDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @PreAuthorize("@auth.hasCode('core:company:member:remove')")
    @Operation(summary = "删除公司成员")
    @PostMapping("/removeCompanyMember")
    public Result<String> removeCompanyMember(@RequestBody @Valid CommonIdDto dto) throws Exception {
        companyMemberService.removeCompanyMember(dto);
        return Result.success("操作成功");
    }

    @PreAuthorize("@auth.hasCode('core:company:member:fire')")
    @Operation(summary = "开除成员")
    @PostMapping("/fireCompanyMember")
    public Result<String> fireCompanyMember(@RequestBody @Valid FireCompanyMemberDto dto) throws Exception {
        companyMemberService.fireCompanyMember(dto);
        return Result.success("开除成功");
    }

}
