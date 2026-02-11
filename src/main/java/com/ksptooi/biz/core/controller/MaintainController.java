package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.auth.service.GroupService;
import com.ksptooi.biz.core.model.permission.ValidateSystemPermissionsVo;
import com.ksptooi.biz.core.repository.ResourceRepository;
import com.ksptooi.biz.core.service.GlobalConfigService;
import com.ksptooi.biz.core.service.PermissionService;
import com.ksptooi.biz.core.service.UserService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.sql.Connection;

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

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 校验系统内置权限节点
     * 检查数据库中是否存在所有系统内置权限，如果不存在则自动创建
     */
    @PostMapping("/validSystemPermission")
    @ResponseBody
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
    public Result<String> validateSystemGroups() {
        try {
            String result = adminGroupService.validateSystemGroups();
            return Result.success(result, null);
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
    public Result<String> validateSystemUsers() {
        try {
            String result = userService.validateSystemUsers();
            return Result.success(result, null);
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
    public Result<String> validateSystemConfigs() {
        try {
            String result = globalConfigService.validateSystemConfigs();
            return Result.success(result, null);
        } catch (Exception e) {
            return Result.error("校验系统配置项失败：" + e.getMessage());
        }
    }


    /**
     * 重置菜单
     * 删除所有菜单并执行SQL脚本恢复默认菜单
     */
    @PostMapping("/resetMenus")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Result<String> resetMenus() throws FileNotFoundException, BizException {

        ClassPathResource sqlScript = new ClassPathResource("sql/default_menus_1_4Z.sql");

        if (!sqlScript.exists()) {
            throw new BizException("SQL脚本文件 'sql/default_menus_1_4Z.sql' 不存在。请检查文件是否已正确放置。");
        }

        try {
            resourceRepository.clearMenu();
            // 使用JdbcTemplate执行回调，以确保使用的是当前事务的连接
            jdbcTemplate.execute((Connection connection) -> {
                ScriptUtils.executeSqlScript(connection, sqlScript);
                return null;
            });
            return Result.success("重置菜单成功", null);
        } catch (Exception e) {
            throw new RuntimeException("重置菜单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 重置端点权限配置
     * 删除所有菜单并执行SQL脚本恢复默认菜单
     */
    @PostMapping("/resetEndpointPermissionConfig")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Result<String> resetEndpointPermissionConfig() throws FileNotFoundException, BizException {

        ClassPathResource sqlScript = new ClassPathResource("sql/default_endpoints_1_2G.sql");

        if (!sqlScript.exists()) {
            throw new BizException("SQL脚本文件 'sql/default_endpoints_1_2G.sql' 不存在。请检查文件是否已正确放置。");
        }

        try {
            resourceRepository.clearEndpoint();
            // 使用JdbcTemplate执行回调，以确保使用的是当前事务的连接
            jdbcTemplate.execute((Connection connection) -> {
                ScriptUtils.executeSqlScript(connection, sqlScript);
                return null;
            });
            return Result.success("重置接口权限配置成功", null);
        } catch (Exception e) {
            throw new RuntimeException("重置接口权限配置失败: " + e.getMessage(), e);
        }
    }

} 