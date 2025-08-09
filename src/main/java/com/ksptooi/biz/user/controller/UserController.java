package com.ksptooi.biz.user.controller;

import com.ksptooi.biz.user.model.user.GetUserDetailsVo;
import com.ksptooi.biz.user.model.user.GetUserListDto;
import com.ksptooi.biz.user.model.user.GetUserListVo;
import com.ksptooi.biz.user.model.user.SaveUserDto;
import com.ksptooi.biz.user.service.UserService;
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

@PrintLog
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;


    @PostMapping("getUserList")
    @RequirePermissionRest("admin:user:view")
    public PageResult<GetUserListVo> getUserList(@RequestBody @Valid GetUserListDto dto){
        return service.getUserList(dto);
    }

    @PostMapping("getUserDetails")
    @RequirePermissionRest("admin:user:save")
    public Result<GetUserDetailsVo> getUserDetails(@RequestBody @Valid CommonIdDto dto) {
        try{
            return Result.success(service.getUserDetails(dto.getId()));
        } catch (Exception ex) {
            return Result.error(ex.getMessage());
        }
    }

    @PrintLog(sensitiveFields = "password")
    @PostMapping("saveUser")
    @RequirePermissionRest("admin:user:save")
    public Result<String> saveUser(@RequestBody @Valid SaveUserDto dto){
        try{
            service.saveUser(dto);
            return Result.success("success");
        }catch (Exception ex){
            return Result.error(ex.getMessage());
        }
    }

    @PostMapping("removeUser")
    @RequirePermissionRest("admin:user:delete")
    public Result<String> removeUser(@RequestBody @Valid CommonIdDto dto){
        try{
            service.removeUser(dto.getId());
            return Result.success("success");
        }catch (Exception ex){
            return Result.error(ex.getMessage());
        }
    }

}
