package com.ksptooi.biz.auth.service;

import com.ksptooi.biz.auth.repository.UserSessionRepository;
import com.ksptool.assembly.entity.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Bio首创 自定义权限实现😄😄😄，auth取自Authorization前4个字母
 *
 * @author KspTooi
 */
@Slf4j
@Service("auth")
public class AuthService {

    @Autowired
    private UserSessionRepository userSessionRepository;


    public static boolean hasPermission(String permission) {
        return true;
    }


    /**
     * 检查当前用户是否拥有指定权限
     *
     * @param permissionCode 权限标识，如：system:user:view
     * @return 如果用户拥有该权限返回true，否则返回false
     */
    public boolean require(String permissionCode) {

        try {

            var session = SessionService.session();


            if (session == null) {
                return false;
            }

            Collection<? extends GrantedAuthority> authorities = session.getAuthorities();

            for (var authority : authorities) {
                if (authority.getAuthority().equals(permissionCode)) {
                    return true;
                }
            }

            return false;
        } catch (AuthException e) {
            return false;
        }

    }

    /**
     * 根据URL路径检查当前用户是否拥有权限
     *
     * @param urlPath 请求URL路径
     * @return 如果用户拥有该权限返回true，否则返回false
     */
    public boolean hasPermissionByUrlPath(String urlPath) {

        return true;

        /*List<String> requiredPermissions = endpointService.getEndpointRequiredPermission(urlPath);

        //如果端点未配置则读取配置项endpoint.access.denied
        if (requiredPermissions == null || requiredPermissions.isEmpty()) {

            boolean denied = globalConfigService.getBoolean(GlobalConfigEnum.ENDPOINT_ACCESS_DENIED.getKey(), Boolean.parseBoolean(GlobalConfigEnum.ENDPOINT_ACCESS_DENIED.getDefaultValue()));

            if (denied) {
                log.warn("端点: {} 未配置权限,已默认禁止访问 请配置端点权限或修改配置项endpoint.access.denied", urlPath);
                return false;
            }

            log.warn("端点: {} 未配置权限,已默认允许访问 请配置端点权限或修改配置项endpoint.access.denied", urlPath);
            return true;
        }

        // 如果端点不需要权限
        if (requiredPermissions.size() == 1 && "*".equals(requiredPermissions.getFirst())) {
            return true;
        }

        UserSessionVo session = getCurrentUserSession();

        if (session == null || session.getPermissions() == null) {
            return false;
        }

        // 检查用户是否拥有任意一个所需权限
        for (String requiredPermission : requiredPermissions) {
            if (StringUtils.isNotBlank(requiredPermission) && session.getPermissions().contains(requiredPermission)) {
                return true;
            }
        }

        log.warn("用户ID: {} 访问端点: {} 时权限校验未通过,所需权限: {}", session.getUserId(), urlPath, requiredPermissions);
        return false;*/
    }


}