package com.ksptooi.biz.auth.common;

import com.ksptooi.biz.auth.model.auth.AuthUserDetails;
import com.ksptooi.biz.auth.model.session.UserSessionPo;
import com.ksptooi.biz.auth.service.SessionService;
import com.ksptooi.commons.WebUtils;
import com.ksptool.assembly.entity.exception.BizException;
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

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.fromJsonArray;

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

    @Autowired
    private SessionService sessionService;

    @NullMarked
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //如果已经认证，则直接放行
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        //优先从Authentication头中获取sessionId
        var sessionId = WebUtils.getAuthenticationBearerSessionId(request);

        if (StringUtils.isBlank(sessionId)) {
            sessionId = WebUtils.getCookieValue(request, "bio-session-id");
        }

        //如果sessionId为空，则直接放行(无请求上下文)
        if (StringUtils.isBlank(sessionId)) {
            filterChain.doFilter(request, response);
            return;
        }

        //从数据库查询会话信息
        UserSessionPo sessionPo = null;

        try {
            sessionPo = sessionService.getSessionBySessionId(sessionId);
        } catch (BizException e) {
            filterChain.doFilter(request, response);
            return;
        }

        //如果会话不存在，则直接放行(无请求上下文)
        if (sessionPo == null) {
            filterChain.doFilter(request, response);
            return;
        }

        //如果会话已过期，则直接放行(无请求上下文)
        if (sessionPo.isExpired()) {
            filterChain.doFilter(request, response);
            return;
        }

        //会话正常，配置安全上下文
        var authorities = new HashSet<GrantedAuthority>();

        //解析会话中的权限码(存储在数据库中的权限码是JSON数组且角色已有ROLE_前缀)
        var permissionCodes = fromJsonArray(sessionPo.getPermissionCodes(), String.class);

        for (var permissionCode : permissionCodes) {
            if (StringUtils.isBlank(permissionCode)) {
                continue;
            }
            authorities.add(new SimpleGrantedAuthority(permissionCode));
        }

        //UserSessionPo 转换为 AuthUserDetails
        var aud = as(sessionPo, AuthUserDetails.class);
        var authentication = new UsernamePasswordAuthenticationToken(aud, null, authorities);

        //设置认证详情
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}

