package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.maintain.vo.MaintainUpdateVo;
import com.ksptooi.biz.core.repository.ResourceRepository;
import com.ksptooi.biz.core.service.MaintainService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private ResourceRepository resourceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MaintainService maintainService;

    //@PreAuthorize(value = "@auth.hasCode('maintain:validate:permissions')")
    @Operation(summary = "校验系统内置权限节点")
    @PostMapping("/validatePermissions")
    public Result<MaintainUpdateVo> validatePermissions() {
        return Result.success(maintainService.validatePermissions());
    }

    @PreAuthorize(value = "@auth.hasCode('maintain:validate:groups')")
    @Operation(summary = "校验系统内置用户组")
    @PostMapping("/validateGroups")
    public Result<MaintainUpdateVo> validateGroups() throws BizException {
        return Result.success(maintainService.validateGroups());
    }

    @PreAuthorize(value = "@auth.hasCode('maintain:validate:users')")
    @Operation(summary = "校验系统内置用户")
    @PostMapping("/validateUsers")
    public Result<MaintainUpdateVo> validateUsers() throws BizException {
        return Result.success(maintainService.validateUsers());
    }

    @PreAuthorize(value = "@auth.hasCode('maintain:reset:menus')")
    @Operation(summary = "维护中心:重置菜单")
    @PostMapping("/resetMenus")
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "menuTree", allEntries = true)
    public Result<MaintainUpdateVo> resetMenus() throws BizException {

        ClassPathResource sqlScript = new ClassPathResource("sql/default_menus_1_5W109.sql");

        if (!sqlScript.exists()) {
            throw new BizException("SQL脚本文件 'sql/default_menus_1_5W109.sql' 不存在。请检查文件是否已正确放置。");
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


    @PreAuthorize(value = "@auth.hasCode('maintain:reset:endpoints')")
    @Operation(summary = "维护中心:重置端点权限配置")
    @PostMapping("/resetEndpoints")
    @Transactional(rollbackFor = Exception.class)
    public Result<MaintainUpdateVo> resetEndpoints() throws BizException {

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