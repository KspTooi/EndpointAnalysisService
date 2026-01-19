package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.config.*;
import com.ksptooi.biz.core.service.ConfigService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.exception.BizException;
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
@RequestMapping("/config")
@Tag(name = "配置项管理", description = "配置项管理")
public class ConfigController {

    @Autowired
    private ConfigService service;

    @Operation(summary = "获取配置项列表")
    @PostMapping("getConfigList")
    public PageResult<GetConfigListVo> getConfigList(@RequestBody @Valid GetConfigListDto dto) throws Exception {
        return service.getConfigList(dto);
    }

    @Operation(summary = "获取配置项详情")
    @PostMapping("getConfigDetails")
    public Result<GetConfigDetailsVo> getConfigDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        try {
            return Result.success(service.getConfigDetails(dto.getId()));
        } catch (BizException e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "新增配置项")
    @PostMapping("addConfig")
    public Result<String> addConfig(@RequestBody @Valid AddConfigDto dto) throws Exception {
        try {
            service.addConfig(dto);
            return Result.success("新增成功");
        } catch (BizException ex) {
            return Result.error(ex.getMessage());
        }
    }

    @Operation(summary = "编辑配置项")
    @PostMapping("editConfig")
    public Result<String> editConfig(@RequestBody @Valid EditConfigDto dto) throws Exception {
        try {
            service.editConfig(dto);
            return Result.success("修改成功");
        } catch (BizException ex) {
            return Result.error(ex.getMessage());
        }
    }

    @Operation(summary = "删除配置项")
    @PostMapping("removeConfig")
    public Result<String> removeConfig(@RequestBody @Valid CommonIdDto dto) throws Exception {
        try {
            service.removeConfig(dto.getId());
            return Result.success("success");
        } catch (BizException ex) {
            return Result.error(ex.getMessage());
        }
    }

}
