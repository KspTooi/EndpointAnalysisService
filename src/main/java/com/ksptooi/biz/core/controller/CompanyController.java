package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.company.dto.AddCompanyDto;
import com.ksptooi.biz.core.model.company.dto.EditCompanyDto;
import com.ksptooi.biz.core.model.company.dto.GetCurrentUserCompanyListDto;
import com.ksptooi.biz.core.model.company.vo.GetCompanyDetailsVo;
import com.ksptooi.biz.core.model.company.vo.GetCurrentUserCompanyListVo;
import com.ksptooi.biz.core.service.CompanyService;
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
@RequestMapping("/company")
@Tag(name = "团队管理", description = "团队管理")
@Slf4j
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/getCurrentUserCompanyList")
    @Operation(summary = "获取我的/我加入的团队列表")
    public PageResult<GetCurrentUserCompanyListVo> getCurrentUserCompanyList(@RequestBody @Valid GetCurrentUserCompanyListDto dto) throws Exception {
        return companyService.getCurrentUserCompanyList(dto);
    }

    @Operation(summary = "新增团队")
    @PostMapping("/addCompany")
    public Result<String> addCompany(@RequestBody @Valid AddCompanyDto dto) throws Exception {
        companyService.addCompany(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑团队")
    @PostMapping("/editCompany")
    public Result<String> editCompany(@RequestBody @Valid EditCompanyDto dto) throws Exception {
        companyService.editCompany(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "退出团队")
    @PostMapping("/leaveCompany")
    public Result<String> leaveCompany(@RequestBody @Valid CommonIdDto dto) throws Exception {
        companyService.leaveCompany(dto);
        return Result.success("已成功退出团队");
    }


    @Operation(summary = "获取团队详情")
    @PostMapping("/getCompanyDetails")
    public Result<GetCompanyDetailsVo> getCompanyDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetCompanyDetailsVo details = companyService.getCompanyDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除团队")
    @PostMapping("/removeCompany")
    public Result<String> removeCompany(@RequestBody @Valid CommonIdDto dto) throws Exception {
        companyService.removeCompany(dto);
        return Result.success("操作成功");
    }

}
