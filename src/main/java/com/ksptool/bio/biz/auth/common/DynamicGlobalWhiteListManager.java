package com.ksptool.bio.biz.auth.common;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.function.Supplier;

/**
 *
 */
@Component
public class DynamicGlobalWhiteListManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    @Override
    public void verify(@NonNull Supplier<? extends @Nullable Authentication> at, RequestAuthorizationContext rac) {
        AuthorizationManager.super.verify(at, rac);
    }

    @Override
    public @Nullable AuthorizationResult authorize(@NonNull Supplier<? extends @Nullable Authentication> at, RequestAuthorizationContext rac) {
        return null;
    }

}
