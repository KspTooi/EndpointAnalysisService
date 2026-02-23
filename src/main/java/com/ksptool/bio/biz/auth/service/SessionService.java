package com.ksptool.bio.biz.auth.service;

import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.auth.model.auth.AuthUserDetails;
import com.ksptool.bio.biz.auth.model.session.UserSessionPo;
import com.ksptool.bio.biz.auth.model.session.dto.GetSessionListDto;
import com.ksptool.bio.biz.auth.model.session.vo.GetSessionDetailsVo;
import com.ksptool.bio.biz.auth.model.session.vo.GetSessionListVo;
import com.ksptool.bio.biz.auth.repository.GroupRepository;
import com.ksptool.bio.biz.auth.repository.UserSessionRepository;
import com.ksptool.bio.biz.core.model.org.OrgPo;
import com.ksptool.bio.biz.core.model.user.UserPo;
import com.ksptool.bio.biz.core.repository.OrgRepository;
import com.ksptool.bio.biz.core.repository.UserRepository;
import com.ksptool.bio.commons.utils.IdWorker;
import com.ksptool.bio.commons.utils.SHA256;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.ksptool.entities.Entities.*;

@Slf4j
@Service
public class SessionService {

    @Value("${session.expires}")
    private long expiresInSeconds;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private OrgRepository orgRepository;


    /**
     * 获取当前用户会话
     *
     * @return 当前用户会话
     * @throws AuthException 如果用户会话不存在，或用户未登录。
     */
    public static AuthUserDetails session() throws AuthException {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AuthException("用户未登录");
        }

        return (AuthUserDetails) authentication.getPrincipal();
    }

    /**
     * 获取当前用户权限
     *
     * @return 当前用户权限
     * @throws AuthException 如果用户未登录
     */
    public static List<GrantedAuthority> authorities() throws AuthException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthException("用户未登录");
        }
        return new ArrayList<>(authentication.getAuthorities());
    }

    /**
     * 获取当前用户会话
     *
     * @return 当前用户会话，如果用户未登录，则返回null
     */
    public static AuthUserDetails getSession() {
        try {
            return session();
        } catch (Exception e) {
            return null;
        }
    }


    public PageResult<GetSessionListVo> getSessionList(GetSessionListDto dto) {
        Page<GetSessionListVo> pPos = userSessionRepository.getSessionList(dto, dto.pageRequest());
        return PageResult.success(pPos.getContent(), pPos.getTotalElements());
    }

    public GetSessionDetailsVo getSessionDetails(Long id) throws BizException {

        UserSessionPo session = userSessionRepository.findById(id)
                .orElseThrow(() -> new BizException("会话不存在"));

        GetSessionDetailsVo vo = new GetSessionDetailsVo();
        vo.setId(session.getId());
        vo.setUsername("----");
        userRepository.findById(session.getUserId())
                .ifPresent(user -> vo.setUsername(user.getUsername()));
        vo.setCreateTime(session.getCreateTime());
        vo.setExpiresAt(session.getExpiresAt());
        vo.setPermissions(new HashSet<>(fromJsonArray(session.getPermissionCodes(), String.class)));

        //RS 0:全部 1:本公司/租户及以下 2:本部门及以下 3:本部门 4:仅本人 5:指定部门
        var maxRs = session.getRsMax();
        var rsAllowDepts = fromJsonArray(session.getRsAllowDepts(), Long.class);
        var rsAllowDeptNames = new ArrayList<String>();


        //处理RS权限列表
        if (maxRs == 2 || maxRs == 3 || maxRs == 5) {

            var depts = orgRepository.getDeptsByIds(rsAllowDepts);

            if (depts.isEmpty()) {
                return vo;
            }

            var rootIds = depts.stream().map(OrgPo::getRootId).distinct().collect(Collectors.toList());
            var roots = orgRepository.getRootsByIds(rootIds);

            for (var dept : depts) {

                var rootName = "未知企业";

                for (var root : roots) {

                    if (Objects.equals(root.getId(), dept.getRootId())) {
                        rootName = root.getName();
                        break;
                    }

                }

                rsAllowDeptNames.add(rootName + " - " + dept.getName());
            }

        }

        vo.setRsMax(maxRs);
        vo.setRsDeptNames(rsAllowDeptNames);
        return vo;
    }

    /**
     * 关闭用户会话
     *
     * @param uid 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeSession(Long uid) {
        closeSession(List.of(uid));
    }

    /**
     * 批量关闭用户会话
     *
     * @param uids 用户ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeSession(List<Long> uids) {

        //获取该用户所有的会话
        var sessions = userSessionRepository.getSessionByUserIds(uids);

        Cache cache = cacheManager.getCache("userSession");

        if (cache != null) {
            //失效所有缓存
            for (var session : sessions) {
                cache.evict(session.getSessionId());
            }
        }

        //删除该用户所有的会话
        userSessionRepository.removeUserSessionByUserIds(uids);
    }

    /**
     * 根据SessionId关闭用户会话
     *
     * @param sessionPrimaryKey 会话PK
     */
    public void closeSessionByPrimaryKey(Long sessionPrimaryKey) throws BizException {

        var session = userSessionRepository.findById(sessionPrimaryKey).orElseThrow(() -> new BizException("会话 [" + sessionPrimaryKey + "] 不存在"));

        //先失效缓存
        Cache cache = cacheManager.getCache("userSession");

        if (cache != null) {
            cache.evict(session.getSessionId());
        }

        //删除数据库中的会话
        userSessionRepository.delete(session);
    }

    /**
     * 获取当前登录的用户
     *
     * @return 当前用户
     */
    public UserPo requireUser() throws AuthException {
        var userId = session().getUserId();
        return userRepository.findById(userId).orElseThrow(() -> new AuthException("user not found"));
    }


    /**
     * 创建用户会话
     *
     * @param aud 认证用户详情
     * @return 用户会话ID
     */
    public String createSession(AuthUserDetails aud) throws BizException {

        var sessionId = UUID.randomUUID().toString();
        var hashedSessionId = SHA256.hex(sessionId);

        var newSession = new UserSessionPo();
        assign(aud, newSession);

        //搜集权限码
        var permCodes = new HashSet<String>();

        for (var authority : aud.getAuthorities()) {
            permCodes.add(authority.getAuthority());
        }

        //存入数据库
        newSession.setId(IdWorker.nextId());
        newSession.setUserId(aud.getId());
        newSession.setSessionId(hashedSessionId);
        newSession.setPermissionCodes(toJson(permCodes));
        newSession.setExpiresAt(LocalDateTime.now().plusSeconds(expiresInSeconds));
        newSession.setCreatorId(aud.getId());
        userSessionRepository.save(newSession);

        //处理用户登录次数与最后登录时间
        var userPo = userRepository.findById(aud.getId()).orElseThrow(() -> new BizException("用户不存在"));
        userPo.setLoginCount(userPo.getLoginCount() + 1);
        userPo.setLastLoginTime(LocalDateTime.now());

        //更新用户
        userRepository.save(userPo);
        return sessionId;
    }


    /**
     * 更新用户会话
     *
     * @param uid 用户ID
     * @return 用户会话
     */
    public void updateSession(Long uid) {

        //查询用户会话(用户现在可能有多个会话,因为用户可能同时登录了多个设备)
        var existingSessions = userSessionRepository.getSessionsByUserId(uid);

        if (existingSessions.isEmpty()) {
            return;
        }

        //查询用户
        var userPo = userRepository.findById(uid).orElse(null);

        if (userPo == null) {
            return;
        }

        //获取用户拥有的全部权限码
        var permissionCodes = userRepository.getUserPermissionCodes(userPo.getId());

        //获取用户拥有的全部用户组
        var groups = groupRepository.getGroupsByUserId(userPo.getId());

        //处理权限码
        var grantedAuthoritiesStr = new HashSet<String>(permissionCodes);

        //处理用户组
        for (var group : groups) {
            grantedAuthoritiesStr.add("ROLE_" + group.getCode());
        }


        //处理用户的每个会话
        for (var session : existingSessions) {
            if (session.isExpired()) {
                userSessionRepository.delete(session);
                continue;
            }
            session.update(userPo, grantedAuthoritiesStr, expiresInSeconds);
            userSessionRepository.save(session);
        }

    }

    /**
     * 清除所有已登录用户的会话状态
     */
    @CacheEvict(cacheNames = "userSession", allEntries = true)
    public void clearUserSession() {
        // 删除所有用户会话记录
        userSessionRepository.deleteAll();
    }

    /**
     * 根据SessionId获取会话 这是一个带有缓存的查询方法 缓存配置位于com.ksptool.bio.commons.config.CacheConfig
     *
     * @param sessionId 会话SessionId
     * @return 会话
     * @throws BizException 如果会话不存在，或会话已过期。
     */
    @Cacheable(cacheNames = "userSession", key = "T(com.ksptool.bio.commons.utils.SHA256).hex(#sessionId)")
    public UserSessionPo getSessionBySessionId(String sessionId) throws BizException {

        var session = userSessionRepository.getSessionBySessionId(SHA256.hex(sessionId));

        if (session == null) {
            throw new BizException("会话不存在");
        }

        return session;
    }

}
