package com.ksptooi.biz.core.controller;


import com.ksptooi.biz.core.model.config.GetConfigDetailsVo;
import com.ksptooi.biz.core.model.config.GetConfigListDto;
import com.ksptooi.biz.core.model.config.GetConfigListVo;
import com.ksptooi.biz.core.model.config.SaveConfigDto;
import com.ksptooi.biz.core.service.ConfigService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.annotation.RequirePermissionRest;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import com.ksptooi.commons.utils.web.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PrintLog
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService service;

    @PostMapping("getConfigList")
    @RequirePermissionRest("admin:config:view")
    public PageResult<GetConfigListVo> getConfigList(@RequestBody @Valid GetConfigListDto dto) throws Exception{
        return service.getConfigList(dto);
    }

    @PostMapping("getConfigDetails")
    @RequirePermissionRest("admin:config:save")
    public Result<GetConfigDetailsVo> getConfigDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        try {
            return Result.success(service.getConfigDetails(dto.getId()));
        } catch (BizException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("saveConfig")
    @RequirePermissionRest("admin:config:save")
    public Result<String> saveConfig(@RequestBody @Valid SaveConfigDto dto) throws Exception{
        try {
            service.saveConfig(dto);
            return Result.success("success");
        } catch (BizException ex) {
            return Result.error(ex.getMessage());
        }
    }

    @PostMapping("removeConfig")
    @RequirePermissionRest("admin:config:remove")
    public Result<String> removeConfig(@RequestBody @Valid CommonIdDto dto) throws Exception{
        try {
            service.removeConfig(dto.getId());
            return Result.success("success");
        } catch (BizException ex) {
            return Result.error(ex.getMessage());
        }
    }

}
