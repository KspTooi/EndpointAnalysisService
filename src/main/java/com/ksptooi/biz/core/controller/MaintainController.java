package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.service.GlobalConfigService;
import com.ksptooi.biz.user.model.permission.ValidateSystemPermissionsVo;
import com.ksptooi.biz.user.service.GroupService;
import com.ksptooi.biz.user.service.PermissionService;
import com.ksptooi.biz.user.service.UserService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.annotation.RequirePermissionRest;
import com.ksptooi.commons.utils.web.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 维护工具控制器
 */
@PrintLog
@Controller
@RestController
@RequestMapping("/maintain")
public class MaintainController {

    @Autowired
    private GroupService adminGroupService;

    @Autowired
    private PermissionService adminPermissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private GlobalConfigService globalConfigService;

    /**
     * 校验系统内置权限节点
     * 检查数据库中是否存在所有系统内置权限，如果不存在则自动创建
     */
    @PostMapping("/validSystemPermission")
    @ResponseBody
    @RequirePermissionRest("admin:maintain:permission")
    public Result<ValidateSystemPermissionsVo> validateSystemPermissions() {
        try {
            ValidateSystemPermissionsVo result = adminPermissionService.validateSystemPermissions();
            
            String message;
            if (result.getAddedCount() > 0) {
                message = String.format("校验完成，已添加 %d 个缺失的权限节点，已存在 %d 个权限节点", 
                        result.getAddedCount(), result.getExistCount());
            } else {
                message = String.format("校验完成，所有 %d 个系统权限节点均已存在", result.getExistCount());
            }
            
            return Result.success(message, result);
        } catch (Exception e) {
            return Result.error("校验权限节点失败：" + e.getMessage());
        }
    }

    /**
     * 校验系统内置用户组
     * 检查数据库中是否存在所有系统内置用户组，如果不存在则自动创建
     */
    @PostMapping("/validSystemGroup")
    @ResponseBody
    @RequirePermissionRest("admin:maintain:group")
    public Result<String> validateSystemGroups() {
        try {
            String result = adminGroupService.validateSystemGroups();
            return Result.success(result,null);
        } catch (Exception e) {
            return Result.error("校验用户组失败：" + e.getMessage());
        }
    }

    /**
     * 校验系统内置用户
     * 检查数据库中是否存在所有系统内置用户，如果不存在则自动创建
     */
    @PostMapping("/validSystemUsers")
    @ResponseBody
    @RequirePermissionRest("admin:maintain:user")
    public Result<String> validateSystemUsers() {
        try {
            String result = userService.validateSystemUsers();
            return Result.success(result,null);
        } catch (Exception e) {
            return Result.error("校验用户失败：" + e.getMessage());
        }
    }

    /**
     * 校验系统全局配置项
     * 检查数据库中是否存在所有系统内置的全局配置项，如果不存在则自动创建
     */
    @PostMapping("/validSystemConfigs")
    @ResponseBody
    @RequirePermissionRest("admin:maintain:config")
    public Result<String> validateSystemConfigs() {
        try {
            String result = globalConfigService.validateSystemConfigs();
            return Result.success(result, null);
        } catch (Exception e) {
            return Result.error("校验系统配置项失败：" + e.getMessage());
        }
    }


} 