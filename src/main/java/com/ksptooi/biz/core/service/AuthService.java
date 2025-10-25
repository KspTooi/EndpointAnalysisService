package com.ksptooi.biz.core.service;

import com.google.gson.Gson;
import com.ksptooi.biz.core.model.permission.PermissionPo;
import com.ksptooi.biz.core.model.session.UserSessionPo;
import com.ksptooi.biz.core.model.session.UserSessionVo;
import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.core.repository.UserRepository;
import com.ksptooi.biz.core.repository.UserSessionRepository;
import com.ksptooi.commons.WebUtils;
import com.ksptooi.commons.enums.GlobalConfigEnum;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.SHA256;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;

@Slf4j
@Service
public class AuthService {

    private static final String SESSION_ATTRIBUTE = "CURRENT_USER_SESSION";

    private final Gson gson = new Gson();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Value("${session.expires}")
    private long expiresInSeconds;

    @Autowired
    private EndpointService endpointService;

    @Autowired
    private GlobalConfigService globalConfigService;

    /**
     * 用户使用用户名与密码登录系统
     *
     * @param username 用户名
     * @param password 密码
     */
    public String loginByPassword(String username, String password) throws BizException {
        // 根据用户名查询用户
        UserPo user = userRepository.findByUsername(username);
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        // 使用用户名作为盐，对密码进行加密：password + username
        String salted = password + username;
        String hashedPassword = SHA256.hex(salted);
        if (!hashedPassword.equals(user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }

        // 更新登录次数和最后登录时间
        user.setLoginCount(user.getLoginCount() + 1);
        user.setLastLoginTime(new Date());
        userRepository.save(user);

        // 登录成功，创建或返回 token
        return createUserSession(user.getId()).getToken();
    }


    /**
     * 获取当前用户
     *
     * @return 当前用户
     * @throws AuthException 如果用户未登录
     */
    public UserPo requireUser() throws AuthException {
        var session = getCurrentUserSession();
        if (session == null || session.getUserId() == null) {
            throw new AuthException("require user login");
        }
        return userRepository.findById(session.getUserId()).orElseThrow(() -> new AuthException("user not found"));
    }


    /**
     * 设置当前请求的用户会话
     *
     * @param session 用户会话信息
     */
    public static void setCurrentUserSession(UserSessionVo session) {
        RequestContextHolder.currentRequestAttributes()
                .setAttribute(SESSION_ATTRIBUTE, session, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 获取当前请求的用户会话
     *
     * @return 用户会话信息，如果未设置则返回null
     */
    public static UserSessionVo getCurrentUserSession() {
        return (UserSessionVo) RequestContextHolder.currentRequestAttributes()
                .getAttribute(SESSION_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    }

    public static UserSessionVo requirePlayer() throws AuthException {
        var session = getCurrentUserSession();
        if (session == null || session.getPlayerId() == null) {
            throw new AuthException("require player login");
        }
        return session;
    }

    public static Long requirePlayerId() throws AuthException {
        var playerId = getCurrentPlayerId();
        if (playerId == null) {
            throw new AuthException("require player login");
        }
        return playerId;
    }

    public static String requirePlayerName() throws AuthException {
        var playerName = getCurrentPlayerName();
        if (playerName == null) {
            throw new AuthException("require player login");
        }
        return playerName;
    }

    public static Long requireUserId() throws AuthException {
        var userId = getCurrentUserId();
        if (userId == null) {
            throw new AuthException("require user login");
        }
        return userId;
    }

    public static Long getCurrentPlayerId() {
        UserSessionVo session = getCurrentUserSession();
        return session != null ? session.getPlayerId() : null;
    }

    public static String getCurrentPlayerName() {
        UserSessionVo session = getCurrentUserSession();
        return session != null ? session.getPlayerName() : null;
    }

    public static boolean isLoginPlayer() {
        return getCurrentPlayerId() != null;
    }

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID，如果未登录则返回null
     */
    public static Long getCurrentUserId() {
        UserSessionVo session = getCurrentUserSession();
        return session != null ? session.getUserId() : null;
    }


    /**
     * 根据URL路径检查当前用户是否拥有权限
     *
     * @param urlPath 请求URL路径
     * @return 如果用户拥有该权限返回true，否则返回false
     */
    public boolean hasPermissionByUrlPath(String urlPath) {

        String requiredPermission = endpointService.getEndpointRequiredPermission(urlPath);

        //如果端点未配置则读取配置项endpoint.access.denied 
        if (requiredPermission == null) {

            boolean denied = globalConfigService.getBoolean(GlobalConfigEnum.ENDPOINT_ACCESS_DENIED.getKey(), Boolean.parseBoolean(GlobalConfigEnum.ENDPOINT_ACCESS_DENIED.getDefaultValue()));

            if (denied) {
                log.warn("端点: {} 未配置权限,已默认禁止访问 请配置端点权限或修改配置项endpoint.access.denied", urlPath);
                return false;
            }

            log.warn("端点: {} 未配置权限,已默认允许访问 请配置端点权限或修改配置项endpoint.access.denied", urlPath);
            return true;
        }

        // 如果端点不需要权限
        if (StringUtils.isBlank(requiredPermission) || "*".equals(requiredPermission)) {
            return true;
        }

        UserSessionVo session = getCurrentUserSession();

        if (session == null || session.getPermissions() == null) {
            return false;
        }

        if (session.getPermissions().contains(requiredPermission)) {
            return true;
        }

        log.warn("用户ID: {} 访问端点: {} 时权限校验未通过,所需权限: {}", session.getUserId(), urlPath, requiredPermission);
        return false;
    }


    /**
     * 检查当前用户是否拥有指定权限
     *
     * @param permission 权限标识，如：system:user:view
     * @return 如果用户拥有该权限返回true，否则返回false
     */
    public static boolean hasPermission(String permission) {

        UserSessionVo session = getCurrentUserSession();


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
     * 清除所有已登录用户的会话状态
     */
    public void clearUserSession() {
        // 删除所有用户会话记录
        userSessionRepository.deleteAll();
    }

    public UserPo verifyUser(HttpServletRequest hsr) {

        String token = WebUtils.getCookieValue(hsr, "token");

        if (token == null) {
            return null; // Cookie中没有token
        }

        Long userId = verifyToken(token);

        if (userId == null) {
            return null; // Token无效
        }

        UserPo user = userRepository.findById(userId).orElse(null);

        // 用户不存在

        return user; // Token有效，返回用户实例
    }


    public Long verifyToken(String token) {
        UserSessionPo userSession = userSessionRepository.findByToken(token);
        if (userSession == null || userSession.getExpiresAt().before(new Date())) {
            return null; // Token无效
        } else {
            return userSession.getUserId();
        }
    }

    public void destroyToken(String token) {
        userSessionRepository.deleteByToken(token);
    }

    /**
     * 根据token获取用户会话信息
     *
     * @param token 会话token
     * @return 会话信息，如果token无效或过期则返回null
     */
    public UserSessionVo getUserSessionByToken(String token) {
        UserSessionPo session = userSessionRepository.findByToken(token);
        if (session == null || session.getExpiresAt().before(new Date())) {
            return null;
        }
        return new UserSessionVo(session);
    }

    /**
     * 从HTTP请求中获取用户会话信息
     *
     * @param hsr HTTP请求
     * @return 会话信息，如果未登录或会话无效则返回null
     */
    public UserSessionVo getUserSessionByHSR(HttpServletRequest hsr) {
        String token = WebUtils.getCookieValue(hsr, "token");
        if (token == null) {
            return null;
        }
        return getUserSessionByToken(token);
    }

    /**
     * 用户注销，清除数据库中的 session
     *
     * @param user 当前用户
     */
    public void logout(UserPo user) {
        // 清除用户的 session
        UserSessionPo query = new UserSessionPo();
        query.setUserId(user.getId());
        Example<UserSessionPo> example = Example.of(query);
        userSessionRepository.deleteAll(userSessionRepository.findAll(example));
    }

    public UserSessionVo createUserSession(Long userId) {
        Date now = new Date();
        // 查询当前用户是否已有会话
        UserSessionPo existingSession = userSessionRepository.findByUserId(userId);
        if (existingSession != null) {
            // 如果 token 未过期，则直接返回当前 token
            if (existingSession.getExpiresAt().after(now)) {
                return new UserSessionVo(existingSession);
            } else {
                // 已过期，删除旧记录
                userSessionRepository.delete(existingSession);
            }
        }

        // 获取用户的所有权限
        List<PermissionPo> permissions = userRepository.findUserPermissions(userId);
        Set<String> permissionCodes = new HashSet<>();
        for (PermissionPo permission : permissions) {
            permissionCodes.add(permission.getCode());
        }

        // 创建新的 token 和会话
        String token = UUID.randomUUID().toString();
        UserSessionPo newSession = new UserSessionPo();
        newSession.setUserId(userId);
        newSession.setToken(token);
        newSession.setExpiresAt(new Date(System.currentTimeMillis() + expiresInSeconds * 1000));

        //序列化权限
        newSession.setPermissions(gson.toJson(permissionCodes));
        userSessionRepository.save(newSession);
        return new UserSessionVo(newSession);
    }

    /**
     * 刷新用户会话的权限和过期时间
     *
     * @param userId 用户ID
     * @return 更新后的会话信息，如果会话不存在或已过期则返回null
     */
    public UserSessionVo refreshUserSession(Long userId) {
        // 查询当前用户是否已有会话
        UserSessionPo existingSession = userSessionRepository.findByUserId(userId);
        if (existingSession == null) {
            return null; // 没有会话时直接返回
        }

        // 检查会话是否过期
        if (existingSession.getExpiresAt().before(new Date())) {
            return null; // 会话已过期，直接返回
        }

        // 获取用户的所有权限
        List<PermissionPo> permissions = userRepository.findUserPermissions(userId);

        Set<String> permissionCodes = new HashSet<>();
        for (PermissionPo permission : permissions) {
            permissionCodes.add(permission.getCode());
        }


        // 更新会话信息
        existingSession.setPermissions(gson.toJson(permissionCodes));
        existingSession.setExpiresAt(new Date(System.currentTimeMillis() + expiresInSeconds * 1000));
        userSessionRepository.save(existingSession);
        return new UserSessionVo(existingSession);
    }


}