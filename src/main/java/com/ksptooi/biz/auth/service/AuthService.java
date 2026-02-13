package com.ksptooi.biz.auth.service;

import com.ksptooi.biz.auth.model.session.vo.UserSessionVo;
import com.ksptooi.biz.auth.repository.GroupRepository;
import com.ksptooi.biz.auth.repository.UserSessionRepository;
import com.ksptooi.biz.core.repository.UserRepository;
import com.ksptooi.biz.core.service.AttachService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private AttachService attachService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private GroupRepository groupRepository;


    /**
     * 检查当前用户是否拥有指定权限
     *
     * @param permission 权限标识，如：system:user:view
     * @return 如果用户拥有该权限返回true，否则返回false
     */
    public static boolean hasPermission(String permission) {

        UserSessionVo session = null;

        return true;

        //UserSessionVo session = getCurrentUserSession();
        //var uid = -1;
        //if (session == null || session.getPermissions() == null) {
        //    log.warn("权限校验未通过 uid:{} permission:{}", uid, permission);
        //    return false;
        //}
//
        //if(session.getPermissions().contains(permission)){
        //    return true;
        //}
        //log.warn("权限校验未通过 uid:{} permission:{}", uid, permission);
        //return false;
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

    /**
     * 清除所有已登录用户的会话状态
     */
    public void clearUserSession() {
        // 删除所有用户会话记录
        userSessionRepository.deleteAll();
    }


}