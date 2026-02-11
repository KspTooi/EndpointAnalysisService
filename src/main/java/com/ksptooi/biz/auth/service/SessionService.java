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
import com.ksptooi.commons.WebUtils;
import com.ksptooi.commons.utils.SHA256;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.ksptool.entities.Entities.*;

@Slf4j
@Service
public class SessionService {

    //线程上下文 用于放置UserSession
    public static final String SESSION_ATTRIBUTE = "CURRENT_USER_SESSION";

    /**
     * 会话ID字段 前端请求头或Cookie中会话ID的字段
     */
    private final Set<String> sessionIdFields = new HashSet<String>() {{
        add("eas-token");
        add("eas-session-id");
    }};

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
    public static UserSessionVo session() throws AuthException {

        var session = getSession();

        if (session == null || session.getUserId() == null) {
            throw new AuthException("Require User Login");
        }

        return session;
    }

    /**
     * 获取当前用户会话
     *
     * @return 当前用户会话 如果用户未登录，则返回null。
     */
    public static UserSessionVo getSession() {
        return (UserSessionVo) RequestContextHolder.currentRequestAttributes()
                .getAttribute(SESSION_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
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
     * 从HTTP请求中获取用户会话信息
     *
     * @param hsr HTTP请求
     * @return 会话信息，如果未登录或会话无效则返回null
     */
    public UserSessionVo getUserSessionByHSR(HttpServletRequest hsr) {

        //尝试获取Token字段
        String sessionId = null;

        //优先从Cookie中获取会话ID
        for (String field : sessionIdFields) {
            sessionId = WebUtils.getCookieValue(hsr, field);
            if (StringUtils.isNotBlank(sessionId)) {
                break;
            }
        }

        //如果Cookie中未获取到会话ID，则从请求头中获取
        if (StringUtils.isBlank(sessionId)) {
            for (String field : sessionIdFields) {
                sessionId = hsr.getHeader(field);
                if (StringUtils.isNotBlank(sessionId)) {
                    break;
                }
            }
        }

        //Cookie与请求头中均未获取到会话ID，则返回null
        if (StringUtils.isBlank(sessionId)) {
            return null;
        }

        //查询会话
        var existingSession = userSessionRepository.getSessionBySessionId(sessionId);

        if (existingSession == null || existingSession.isExpired()) {
            return null;
        }

        return existingSession.toVo();
    }

}
