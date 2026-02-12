package com.ksptooi.biz.auth.service;

import com.ksptooi.biz.auth.model.auth.AuthUserDetails;
import com.ksptooi.biz.auth.model.session.UserSessionPo;
import com.ksptooi.biz.auth.model.session.dto.GetSessionListDto;
import com.ksptooi.biz.auth.model.session.vo.GetSessionDetailsVo;
import com.ksptooi.biz.auth.model.session.vo.GetSessionListVo;
import com.ksptooi.biz.auth.model.session.vo.UserSessionVo;
import com.ksptooi.biz.auth.repository.UserSessionRepository;
import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.core.repository.UserRepository;
import com.ksptooi.commons.utils.IdWorker;
import com.ksptooi.commons.utils.SHA256;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

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
        return vo;
    }

    /**
     * 关闭用户会话
     *
     * @param uid 用户ID
     */
    public void closeSession(Long uid) {
        closeSession(List.of(uid));
    }

    /**
     * 批量关闭用户会话
     *
     * @param uids 用户ID列表
     */
    public void closeSession(List<Long> uids) {
        userSessionRepository.removeUserSessionByUserIds(uids);
    }

    /**
     * 获取当前登录的用户
     *
     * @return 当前用户
     */
    public UserPo requireUser() throws AuthException {
        return userRepository.findById(session().getUserId()).orElseThrow(() -> new AuthException("user not found"));
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
        userSessionRepository.save(newSession);
        return sessionId;
    }


    /**
     * 更新用户会话
     *
     * @param uid 用户ID
     * @return 用户会话
     */
    public UserSessionVo updateSession(Long uid) throws AuthException {

        //查询用户会话
        var existingSession = userSessionRepository.getSessionByUserId(uid);

        if (existingSession == null) {
            throw new AuthException("用户会话不存在");
        }

        if (existingSession.isExpired()) {
            userSessionRepository.delete(existingSession);
            throw new AuthException("用户会话已过期");
        }

        //查询用户
        var userPo = userRepository.findById(uid).orElseThrow(() -> new AuthException("用户不存在"));

        //获取用户拥有的全部权限代码
        var userPermissionCodes = userRepository.getUserPermissionCodes(userPo.getId());

        //更新会话
        existingSession.update(userPo, userPermissionCodes, expiresInSeconds);
        existingSession = userSessionRepository.save(existingSession);
        return existingSession.toVo();
    }


    /**
     * 根据SessionId获取会话 这是一个带有缓存的查询方法 缓存配置位于com.ksptooi.commons.config.CacheConfig
     *
     * @param sessionId 会话SessionId
     * @return 会话
     * @throws BizException 如果会话不存在，或会话已过期。
     */
    @Cacheable(cacheNames = "userSession", key = "#sessionId")
    public UserSessionPo getSessionBySessionId(String sessionId) throws BizException {

        var session = userSessionRepository.getSessionBySessionId(SHA256.hex(sessionId));

        if (session == null) {
            throw new BizException("会话不存在");
        }

        return session;
    }

}
