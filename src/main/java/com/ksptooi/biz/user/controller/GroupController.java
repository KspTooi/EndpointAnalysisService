package com.ksptooi.biz.user.controller;


import com.ksptooi.biz.user.model.group.*;
import com.ksptooi.biz.user.service.GroupService;
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
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService service;

    @PostMapping("getGroupDefinitions")
    public Result<List<GetGroupDefinitionsVo>> getGroupDefinitions(){
        return Result.success(service.getGroupDefinitions());
    }

    @PostMapping("getGroupList")
    @RequirePermissionRest("admin:group:view")
    public PageResult<GetGroupListVo> getGroupList(@RequestBody @Valid GetGroupListDto dto){
        return service.getGroupList(dto);
    }

    @PostMapping("getGroupDetails")
    @RequirePermissionRest("admin:group:save")
    public Result<GetGroupDetailsVo> getGroupDetails(@RequestBody @Valid CommonIdDto dto){
        try{
            return Result.success(service.getGroupDetails(dto.getId()));
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("saveGroup")
    @RequirePermissionRest("admin:group:save")
    public Result<String> saveGroup(@RequestBody @Valid SaveGroupDto dto){
        try{
            service.saveGroup(dto);
            return Result.success("success");
        }catch (Exception ex){
            return Result.error(ex.getMessage());
        }
    }

    @PostMapping("removeGroup")
    @RequirePermissionRest("admin:group:delete")
    public Result<String> removeGroup(@RequestBody @Valid CommonIdDto dto){
        try{
            service.removeGroup(dto.getId());
            return Result.success("success");
        }catch (Exception ex){
            return Result.error(ex.getMessage());
        }
    }
}
