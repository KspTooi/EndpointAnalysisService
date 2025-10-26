package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.companymember.dto.AddCompanyMemberDto;
import com.ksptooi.biz.core.model.companymember.dto.EditCompanyMemberDto;
import com.ksptooi.biz.core.model.companymember.dto.GetCompanyMemberListDto;
import com.ksptooi.biz.core.model.companymember.vo.GetCompanyMemberDetailsVo;
import com.ksptooi.biz.core.model.companymember.vo.GetCompanyMemberListVo;
import com.ksptooi.biz.core.service.CompanyMemberService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
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
@RequestMapping("/companyMember")
@Tag(name = "公司成员管理", description = "公司成员管理")
@Slf4j
public class CompanyMemberController {

    @Autowired
    private CompanyMemberService companyMemberService;

    @PostMapping("/getCompanyMemberList")
    @Operation(summary = "获取公司成员列表")
    public PageResult<GetCompanyMemberListVo> getCompanyMemberList(@RequestBody @Valid GetCompanyMemberListDto dto) throws Exception {
        return companyMemberService.getCompanyMemberList(dto);
    }

    @Operation(summary = "新增公司成员")
    @PostMapping("/addCompanyMember")
    public Result<String> addCompanyMember(@RequestBody @Valid AddCompanyMemberDto dto) throws Exception {
        companyMemberService.addCompanyMember(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑公司成员")
    @PostMapping("/editCompanyMember")
    public Result<String> editCompanyMember(@RequestBody @Valid EditCompanyMemberDto dto) throws Exception {
        companyMemberService.editCompanyMember(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "获取公司成员详情")
    @PostMapping("/getCompanyMemberDetails")
    public Result<GetCompanyMemberDetailsVo> getCompanyMemberDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetCompanyMemberDetailsVo details = companyMemberService.getCompanyMemberDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除公司成员")
    @PostMapping("/removeCompanyMember")
    public Result<String> removeCompanyMember(@RequestBody @Valid CommonIdDto dto) throws Exception {
        companyMemberService.removeCompanyMember(dto);
        return Result.success("操作成功");
    }

}
