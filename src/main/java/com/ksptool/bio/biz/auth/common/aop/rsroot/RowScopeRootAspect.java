package com.ksptool.bio.biz.auth.common.aop.rsroot;

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
 * 仅本租户数据权限切面
 * 拦截标注了 @RowScopeRoot 的方法或类，激活 rsRootFilter 过滤器
 */
@Aspect
@Component
public class RowScopeRootAspect {

    private static final String RS_ROOT_FILTER_NAME = "rsRootFilter";

    @PersistenceContext
    private EntityManager entityManager;

    @Pointcut("@annotation(com.ksptool.bio.biz.auth.common.aop.rsroot.RowScopeRoot) || @within(com.ksptool.bio.biz.auth.common.aop.rsroot.RowScopeRoot)")
    public void rsRootPointcut() {
    }

    @Before("rsRootPointcut()")
    public void enableFilter() throws BizException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new BizException("在处理仅本租户数据权限时，获取当前登录用户的 Authentication 失败!");
        }

        AuthUserDetails aud = (AuthUserDetails) auth.getPrincipal();

        if (aud == null) {
            throw new BizException("在处理仅本租户数据权限时，获取当前登录用户的 AUD 失败!");
        }

        if (aud.getRootId() == null) {
            throw new BizException("在处理仅本租户数据权限时，当前登录用户未绑定租户!");
        }

        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(RS_ROOT_FILTER_NAME);
        filter.setParameter("rootId", aud.getRootId());
    }

    @After("rsRootPointcut()")
    public void disableFilter() {
        Session session = entityManager.unwrap(Session.class);
        session.disableFilter(RS_ROOT_FILTER_NAME);
    }

}
