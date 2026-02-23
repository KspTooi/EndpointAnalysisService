package com.ksptool.bio.biz.auth.common;

import com.ksptool.bio.biz.core.model.endpoint.DynamicEndpointAuthorizationRule;
import com.ksptool.bio.biz.core.service.EndpointService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 动态权限授权管理器（模板）。
 *
 * <p>
 * 设计目标：
 * - 从某个“规则提供者”加载：path -> requiredPermissions
 * - 对当前请求 URI 做匹配，解析出所需权限集合
 * - 使用 Authentication 的 authorities 做对比，返回 allow/deny
 * </p>
 *
 * <p>
 * 说明：
 * - 此类不包含任何具体业务（例如 core_resource 表的读取），仅提供骨架与推荐默认策略
 * - 默认策略建议为：未命中任何规则时拒绝（fail-close），避免“新增接口忘配权限导致放行”
 * - 你们如果希望“未配置默认放行”，只需改动 {@link #decideWhenNoRuleMatched()} 即可
 * </p>
 */
@Component
public class DynamicAuthorizationManager {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private EndpointService endpointService;


    /**
     * Spring Security 调用入口。
     */
    public AuthorizationManager<RequestAuthorizationContext> asAuthorizationManager() {

        return this::check;
    }

    /**
     * 实际授权检查逻辑（模板）。
     */
    private AuthorizationDecision check(Supplier<? extends Authentication> authentication,
                                        RequestAuthorizationContext context) {

        if (context == null) {
            return new AuthorizationDecision(false);
        }

        HttpServletRequest request = context.getRequest();
        if (request == null) {
            return new AuthorizationDecision(false);
        }

        String requestPath = normalizePath(request.getRequestURI());
        if (StringUtils.isBlank(requestPath)) {
            return new AuthorizationDecision(false);
        }

        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }


        List<DynamicEndpointAuthorizationRule> rules = safeRules(endpointService.getEndpointPermissionConfig());
        if (rules.isEmpty()) {
            return decideWhenNoRulesConfigured();
        }

        DynamicEndpointAuthorizationRule matched = matchBestRule(rules, requestPath);

        if (matched == null) {
            return decideWhenNoRuleMatched();
        }

        Set<String> permissionCodes = parsePermissionCodes(matched.getPermissionCodes());
        if (permissionCodes.isEmpty()) {
            return decideWhenRuleHasNoPermissionConfigured();
        }

        if (permissionCodes.contains("*")) {
            return new AuthorizationDecision(true);
        }

        Set<String> userAuthorities = extractAuthorities(auth.getAuthorities());
        if (userAuthorities.isEmpty()) {
            return new AuthorizationDecision(false);
        }

        for (String required : permissionCodes) {
            if (StringUtils.isBlank(required)) {
                continue;
            }
            if (userAuthorities.contains(required)) {
                return new AuthorizationDecision(true);
            }
        }

        return new AuthorizationDecision(false);
    }

    /**
     * 规则未配置时的默认策略。
     */
    private AuthorizationDecision decideWhenNoRulesConfigured() {
        return new AuthorizationDecision(false);
    }

    /**
     * 未命中任何规则时的默认策略。
     */
    private AuthorizationDecision decideWhenNoRuleMatched() {
        return new AuthorizationDecision(false);
    }

    /**
     * 命中规则但权限为空时的默认策略。
     */
    private AuthorizationDecision decideWhenRuleHasNoPermissionConfigured() {
        return new AuthorizationDecision(false);
    }

    private String normalizePath(String uri) {
        if (StringUtils.isBlank(uri)) {
            return "";
        }
        int qIdx = uri.indexOf('?');
        if (qIdx < 0) {
            return uri;
        }
        return uri.substring(0, qIdx);
    }

    private List<DynamicEndpointAuthorizationRule> safeRules(List<DynamicEndpointAuthorizationRule> rules) {
        if (rules == null) {
            return Collections.emptyList();
        }
        return rules;
    }

    /**
     * 匹配最合适的规则（模板）：优先选择 pattern 更“长”的（更具体）。
     */
    private DynamicEndpointAuthorizationRule matchBestRule(List<DynamicEndpointAuthorizationRule> rules, String requestPath) {

        DynamicEndpointAuthorizationRule best = null;
        int bestScore = -1;

        for (DynamicEndpointAuthorizationRule rule : rules) {
            if (rule == null) {
                continue;
            }
            String pattern = normalizePath(rule.getPathPattern());
            if (StringUtils.isBlank(pattern)) {
                continue;
            }
            if (!pathMatcher.match(pattern, requestPath)) {
                continue;
            }
            int score = pattern.length();
            if (score <= bestScore) {
                continue;
            }
            bestScore = score;
            best = rule;
        }

        return best;
    }

    /**
     * 解析规则配置的权限集合（模板）。
     *
     * <p>
     * 推荐格式：
     * - "*"：放行
     * - "a;b;c"：多个权限，满足任意一个即可（OR）
     * </p>
     */
    private Set<String> parsePermissionCodes(String permissionCodes) {
        if (StringUtils.isBlank(permissionCodes)) {
            return Collections.emptySet();
        }
        return Set.of(permissionCodes.split(";"));
    }

    private Set<String> extractAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return Collections.emptySet();
        }
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
    }


}
