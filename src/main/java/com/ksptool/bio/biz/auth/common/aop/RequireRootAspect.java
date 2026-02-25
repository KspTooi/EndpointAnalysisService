package com.ksptool.bio.biz.auth.common.aop;

import com.ksptool.bio.biz.auth.common.exception.RootBindingException;
import com.ksptool.bio.biz.auth.model.auth.AuthUserDetails;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 租户绑定检查切面
 * 当 @RowScope(requireRoot=true) 时，检查当前用户是否已绑定租户
 */
@Aspect
@Component
public class RequireRootAspect {

    @Before("@annotation(com.ksptool.bio.biz.auth.common.aop.RowScope) || @within(com.ksptool.bio.biz.auth.common.aop.RowScope)")
    public void checkRequireRoot(JoinPoint joinPoint) throws RootBindingException {

        // 优先取方法级注解，没有则回退到类级注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RowScope rowScope = AnnotationUtils.findAnnotation(method, RowScope.class);
        
        // 方法上没有注解时，尝试从目标类上获取
        if (rowScope == null) {
            rowScope = AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(), RowScope.class);
        }

        // 未标注或未要求绑定租户，直接放行
        if (rowScope == null || !rowScope.requireRoot()) {
            return;
        }

        // 获取当前登录用户的认证信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new RootBindingException();
        }

        // 检查用户是否已绑定租户（rootId 不为空）
        AuthUserDetails aud = (AuthUserDetails) auth.getPrincipal();
        if (aud == null || aud.getRootId() == null) {
            throw new RootBindingException();
        }
    }

}
