package com.ksptooi.biz.user.controller;

import com.ksptooi.biz.user.model.permission.*;
import com.ksptooi.biz.user.service.PermissionService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.annotation.RequirePermissionRest;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import com.ksptooi.commons.utils.web.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PrintLog
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService service;


    @PostMapping("getPermissionDefinition")
    public Result<List<GetPermissionDefinitionVo>> getPermissionDefinition(){
        return Result.success(service.getPermissionDefinition());
    }

    @PostMapping("getPermissionList")
    @RequirePermissionRest("admin:permission:view")
    public PageResult<GetPermissionListVo> getPermissionList(@RequestBody @Valid GetPermissionListDto dto){
        return service.getPermissionList(dto);
    }

    @PostMapping("getPermissionDetails")
    @RequirePermissionRest("admin:permission:save")
    public Result<GetPermissionDetailsVo> getPermissionDetails(@RequestBody @Valid CommonIdDto dto){
        try{
            return Result.success(service.getPermissionDetails(dto.getId()));
        }catch (Exception ex){
            return Result.error(ex.getMessage());
        }
    }

    @PostMapping("savePermission")
    @RequirePermissionRest("admin:permission:save")
    public Result<String> savePermission(@RequestBody @Valid SavePermissionDto dto){
        try{
            service.savePermission(dto);
            return Result.success("success");
        }catch (Exception ex){
            return Result.error(ex.getMessage());
        }
    }

    @PostMapping("removePermission")
    @RequirePermissionRest("admin:permission:remove")
    public Result<String> removePermission(@RequestBody @Valid CommonIdDto dto){
        try{
            service.removePermission(dto.getId());
            return Result.success("success");
        }catch (Exception ex){
            return Result.error(ex.getMessage());
        }
    }
}
