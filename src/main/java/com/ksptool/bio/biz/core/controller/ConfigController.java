package com.ksptool.bio.biz.core.controller;

import com.ksptooi.biz.core.model.config.*;
import com.ksptool.bio.biz.core.model.config.*;
import com.ksptool.bio.biz.core.service.ConfigService;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.commons.annotation.PrintLog;
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
@RequestMapping("/config")
@Tag(name = "配置项管理", description = "配置项管理")
public class ConfigController {

    @Autowired
    private ConfigService service;

    @PreAuthorize("@auth.hasCode('core:config:view')")
    @Operation(summary = "获取配置项列表")
    @PostMapping("getConfigList")
    public PageResult<GetConfigListVo> getConfigList(@RequestBody @Valid GetConfigListDto dto) throws Exception {
        return service.getConfigList(dto);
    }

    @PreAuthorize("@auth.hasCode('core:config:view')")
    @Operation(summary = "获取配置项详情")
    @PostMapping("getConfigDetails")
    public Result<GetConfigDetailsVo> getConfigDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return Result.success(service.getConfigDetails(dto.getId()));
    }

    @PreAuthorize("@auth.hasCode('core:config:add')")
    @Operation(summary = "新增配置项")
    @PostMapping("addConfig")
    public Result<String> addConfig(@RequestBody @Valid AddConfigDto dto) throws Exception {
        service.addConfig(dto);
        return Result.success("新增成功");
    }

    @PreAuthorize("@auth.hasCode('core:config:edit')")
    @Operation(summary = "编辑配置项")
    @PostMapping("editConfig")
    public Result<String> editConfig(@RequestBody @Valid EditConfigDto dto) throws Exception {
        service.editConfig(dto);
        return Result.success("修改成功");
    }

    @PreAuthorize("@auth.hasCode('core:config:remove')")
    @Operation(summary = "删除配置项")
    @PostMapping("removeConfig")
    public Result<String> removeConfig(@RequestBody @Valid CommonIdDto dto) throws Exception {
        service.removeConfig(dto.getId());
        return Result.success("删除成功");
    }

}
