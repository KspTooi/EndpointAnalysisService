package com.ksptool.bio.biz.core.controller;

import com.ksptool.bio.biz.core.model.company.dto.AddCompanyDto;
import com.ksptool.bio.biz.core.model.company.dto.EditCompanyDto;
import com.ksptool.bio.biz.core.model.company.dto.GetCurrentUserCompanyListDto;
import com.ksptool.bio.biz.core.model.company.dto.ResignCeoDto;
import com.ksptool.bio.biz.core.model.company.vo.GetCompanyDetailsVo;
import com.ksptool.bio.biz.core.model.company.vo.GetCurrentUserCompanyListVo;
import com.ksptool.bio.biz.core.service.CompanyService;
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
@RequestMapping("/company")
@Tag(name = "团队管理", description = "团队管理")
@Slf4j
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PreAuthorize("@auth.hasCode('core:company:view')")
    @PostMapping("/getCurrentUserCompanyList")
    @Operation(summary = "获取我的/我加入的团队列表")
    public PageResult<GetCurrentUserCompanyListVo> getCurrentUserCompanyList(@RequestBody @Valid GetCurrentUserCompanyListDto dto) throws Exception {
        return companyService.getCurrentUserCompanyList(dto);
    }

    @PreAuthorize("@auth.hasCode('core:company:add')")
    @Operation(summary = "新增团队")
    @PostMapping("/addCompany")
    public Result<String> addCompany(@RequestBody @Valid AddCompanyDto dto) throws Exception {
        companyService.addCompany(dto);
        return Result.success("新增成功");
    }

    @PreAuthorize("@auth.hasCode('core:company:edit')")
    @Operation(summary = "编辑团队")
    @PostMapping("/editCompany")
    public Result<String> editCompany(@RequestBody @Valid EditCompanyDto dto) throws Exception {
        companyService.editCompany(dto);
        return Result.success("修改成功");
    }

    @PreAuthorize("@auth.hasCode('core:company:leave')")
    @Operation(summary = "退出团队")
    @PostMapping("/leaveCompany")
    public Result<String> leaveCompany(@RequestBody @Valid CommonIdDto dto) throws Exception {
        companyService.leaveCompany(dto);
        return Result.success("已成功退出团队");
    }


    @PreAuthorize("@auth.hasCode('core:company:view')")
    @Operation(summary = "获取团队详情")
    @PostMapping("/getCompanyDetails")
    public Result<GetCompanyDetailsVo> getCompanyDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetCompanyDetailsVo details = companyService.getCompanyDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @PreAuthorize("@auth.hasCode('core:company:activate')")
    @Operation(summary = "激活团队")
    @PostMapping("/activateCompany")
    public Result<String> activateCompany(@RequestBody @Valid CommonIdDto dto) throws Exception {
        companyService.activateCompany(dto);
        return Result.success("激活成功");
    }

    @PreAuthorize("@auth.hasCode('core:company:resign')")
    @Operation(summary = "辞去CEO职位")
    @PostMapping("/resignCEO")
    public Result<String> resignCEO(@RequestBody @Valid ResignCeoDto dto) throws Exception {
        companyService.resignCEO(dto);
        return Result.success("您已经辞去CEO职位");
    }

}
