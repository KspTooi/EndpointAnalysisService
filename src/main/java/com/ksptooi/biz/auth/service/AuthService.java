package com.ksptooi.biz.auth.service;

import com.ksptooi.biz.audit.service.AuditLoginService;
import com.ksptooi.biz.auth.model.session.UserSessionPo;
import com.ksptooi.biz.auth.model.session.UserSessionVo;
import com.ksptooi.biz.core.model.auth.vo.GetCurrentUserProfile;
import com.ksptooi.biz.core.model.auth.vo.GetCurrentUserProfilePermissionVo;
import com.ksptooi.biz.core.model.group.GroupPo;
import com.ksptooi.biz.core.model.permission.PermissionPo;
import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.core.repository.UserRepository;
import com.ksptooi.biz.core.repository.UserSessionRepository;
import com.ksptooi.biz.core.service.AttachService;
import com.ksptooi.biz.core.service.EndpointService;
import com.ksptooi.biz.core.service.GlobalConfigService;
import com.ksptooi.biz.core.service.SessionService;
import com.ksptooi.commons.WebUtils;
import com.ksptooi.commons.utils.SHA256;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private EndpointService endpointService;

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private AttachService attachService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AuditLoginService auditLoginService;


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
     * 用户使用用户名与密码登录系统
     *
     * @param username 用户名
     * @param password 密码
     */
    public String loginByPassword(String username, String password, HttpServletRequest hsr) throws BizException {


        // 根据用户名查询用户
        var user = userRepository.findByUsername(username);
        var ipAddr = getIpAddr(hsr);
        var uaString = hsr.getHeader("User-Agent");

        if (user == null) {
            auditLoginService.recordLoginAudit(null, username, 1, "用户不存在", ipAddr, uaString);
            throw new BizException("用户名或密码错误");
        }

        // 使用用户名作为盐，对密码进行加密：password + username
        String salted = password + username;
        String hashedPassword = SHA256.hex(salted);
        if (!hashedPassword.equals(user.getPassword())) {
            auditLoginService.recordLoginAudit(null, username, 1, "密码错误", ipAddr, uaString);
            throw new BizException("用户名或密码错误");
        }

        // 更新登录次数和最后登录时间
        user.setLoginCount(user.getLoginCount() + 1);
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        // 登录成功，创建或返回 token
        UserSessionVo session = sessionService.createSession(user.getId());
        auditLoginService.recordLoginAudit(user.getId(), username, 0, "登录成功", ipAddr, uaString);
        return session.getSessionId();
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
        UserSessionPo userSession = userSessionRepository.getSessionBySessionId(token);
        if (userSession == null || userSession.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null; // Token无效
        } else {
            return userSession.getUserId();
        }
    }


    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    public GetCurrentUserProfile getCurrentUserProfile() throws AuthException {

        var user = sessionService.requireUser();

        //组装vo
        var vo = new GetCurrentUserProfile();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setGender(user.getGender());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setIsSystem(user.getIsSystem());
        vo.setAvatarAttachId(null);

        //处理用户组、权限
        var groups = user.getGroups();

        List<String> groupNames = new ArrayList<>();
        Set<PermissionPo> permissionSet = new HashSet<>();

        for (GroupPo group : groups) {
            groupNames.add(group.getName());
            group.getPermissions().forEach(permission -> {
                permissionSet.add(permission);
            });
        }

        vo.setGroups(groupNames);

        //处理权限
        var permissionList = new ArrayList<GetCurrentUserProfilePermissionVo>();
        for (PermissionPo permission : permissionSet) {
            var permissionVo = new GetCurrentUserProfilePermissionVo();
            permissionVo.setCode(permission.getCode());
            permissionVo.setName(permission.getName());
            permissionList.add(permissionVo);
        }
        vo.setPermissions(permissionList);

        //处理头像
        var avatarAttach = user.getAvatarAttach();
        if (avatarAttach != null) {
            vo.setAvatarAttachId(avatarAttach.getId());
        }

        return vo;
    }


    /**
     * 获取当前用户头像
     *
     * @return 用户头像
     */
    public ResponseEntity<Resource> getUserAvatar() throws AuthException {

        var userPo = sessionService.requireUser();
        var avatarAttach = userPo.getAvatarAttach();

        //返回默认头像
        if (avatarAttach == null) {
            return getDefaultAvatar();
        }

        //返回用户头像
        var absolutePath = attachService.getAttachLocalPath(Paths.get(avatarAttach.getPath()));
        if (!Files.exists(absolutePath)) {
            return getDefaultAvatar();
        }

        var resource = new FileSystemResource(absolutePath);
        var filename = avatarAttach.getName();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * 更新当前用户头像
     *
     * @param file 头像文件
     * @return 更新后的头像
     */
    public ResponseEntity<Resource> updateUserAvatar(MultipartFile file) throws AuthException {

        var userPo = sessionService.requireUser();

        if (file == null || file.isEmpty()) {
            throw new AuthException("头像文件不能为空");
        }

        if (StringUtils.isBlank(file.getOriginalFilename())) {
            throw new AuthException("头像文件名不能为空");
        }

        try {
            var attachId = attachService.uploadAttach(file, "user_avatar");
            var attachPo = attachService.requireAttach(attachId);
            userPo.setAvatarAttach(attachPo);
            userRepository.save(userPo);
            return getUserAvatar();
        } catch (BizException e) {
            throw new AuthException(e.getMessage());
        }
    }


    /**
     * 获取默认头像
     *
     * @return 默认头像资源
     */
    private ResponseEntity<Resource> getDefaultAvatar() {
        var resource = new ClassPathResource("views/webconsole/src/assets/EAS_CROWN_SHORT_LOGO.jpg");
        var filename = "EAS_CROWN_SHORT_LOGO.jpg";
        if (!resource.exists()) {
            throw new RuntimeException("默认头像文件不存在");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }


    /*
     * 获取IP地址
     * @param request 请求
     * @return IP地址
     */
    private String getIpAddr(HttpServletRequest request) {

        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-REAL-IP");

        if (StringUtils.isNotBlank(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }


}