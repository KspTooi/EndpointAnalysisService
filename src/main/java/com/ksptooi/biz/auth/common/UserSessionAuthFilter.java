package com.ksptooi.biz.auth.common;

import com.ksptooi.biz.auth.model.session.vo.UserSessionVo;
import com.ksptooi.biz.auth.repository.UserSessionRepository;
import com.ksptooi.commons.WebUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;

/**
 * 基于 core_user_session 的无状态认证过滤器模板。
 *
 * <p>
 * 设计目标：
 * - core_user_session 是唯一权威
 * - 每次请求从 Cookie/Header 取 sessionId，然后查表重建 Authentication
 * - 不依赖 HttpSession / Spring Session
 * </p>
 *
 * <p>
 * 注意：
 * - 如果你们需要 ROLE_ 角色码，请确保 core_user_session.permissions 内也包含 ROLE_xxx，或在此处额外补齐
 * - 如果你们要做“秒更”，可以对 sessionId 做 Caffeine 短 TTL 缓存，并在权限变更时主动失效
 * </p>
 */
@Component
public class UserSessionAuthFilter extends OncePerRequestFilter {

    private static final String COOKIE_SESSION_ID = "eas-session-id";
    private static final String HEADER_SESSION_ID = "eas-token";

    @Autowired
    private UserSessionRepository userSessionRepository;


    @NullMarked
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String sessionId = WebUtils.getCookieValue(request, COOKIE_SESSION_ID);
        if (StringUtils.isBlank(sessionId)) {
            sessionId = request.getHeader(HEADER_SESSION_ID);
        }

        if (StringUtils.isBlank(sessionId)) {
            filterChain.doFilter(request, response);
            return;
        }

        var sessionPo = userSessionRepository.getSessionBySessionId(sessionId);
        if (sessionPo == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (sessionPo.isExpired()) {
            filterChain.doFilter(request, response);
            return;
        }

        UserSessionVo sessionVo = sessionPo.toVo();
        if (sessionVo == null || sessionVo.getUserId() == null) {
            filterChain.doFilter(request, response);
            return;
        }

        var authorities = new HashSet<GrantedAuthority>();
        if (sessionVo.getPermissionCodes() != null) {
            for (String permissionCode : sessionVo.getPermissionCodes()) {
                if (StringUtils.isBlank(permissionCode)) {
                    continue;
                }
                authorities.add(new SimpleGrantedAuthority(permissionCode));
            }
        }

        var authentication = new UsernamePasswordAuthenticationToken(sessionVo, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}

