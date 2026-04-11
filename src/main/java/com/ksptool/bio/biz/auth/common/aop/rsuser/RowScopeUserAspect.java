package com.ksptool.bio.biz.auth.common.aop.rsuser;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.bio.biz.auth.model.auth.AuthUserDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 仅本人数据权限切面
 * 拦截标注了 @RowScopeUser 的方法或类，激活 rsUserFilter 过滤器
 */
@Aspect
@Component
public class RowScopeUserAspect {

    private static final String RS_USER_FILTER_NAME = "rsUserFilter";

    @PersistenceContext
    private EntityManager entityManager;

    @Pointcut("@annotation(com.ksptool.bio.biz.auth.common.aop.rsuser.RowScopeUser) || @within(com.ksptool.bio.biz.auth.common.aop.rsuser.RowScopeUser)")
    public void rsUserPointcut() {
    }

    @Before("rsUserPointcut()")
    public void enableFilter() throws BizException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new BizException("在处理仅本人数据权限时，获取当前登录用户的 Authentication 失败!");
        }

        AuthUserDetails aud = (AuthUserDetails) auth.getPrincipal();

        if (aud == null) {
            throw new BizException("在处理仅本人数据权限时，获取当前登录用户的 AUD 失败!");
        }

        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(RS_USER_FILTER_NAME);
        filter.setParameter("userId", aud.getId());
    }

    @After("rsUserPointcut()")
    public void disableFilter() {
        Session session = entityManager.unwrap(Session.class);
        session.disableFilter(RS_USER_FILTER_NAME);
    }

}
