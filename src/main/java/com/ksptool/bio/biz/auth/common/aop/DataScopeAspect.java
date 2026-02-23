package com.ksptool.bio.biz.auth.common.aop;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.bio.biz.auth.model.auth.AuthUserDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Aspect
@Component
public class DataScopeAspect {

    @PersistenceContext
    private EntityManager entityManager;

    // 拦截所有 Service 层方法 (您可以根据项目实际情况调整切点，比如加上自定义注解 @DataPermission)
    @Before("execution(* com.ksptool.bio.biz..service.*.*(..))")
    public void enableDataScopeFilter() throws BizException {

        //获取当前登录用户的 Session 上下文
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new BizException("在处理数据权限时，获取当前登录用户的 Authentication 失败!");
        }

        //获取登录成功的AUD
        AuthUserDetails aud = (AuthUserDetails) auth.getPrincipal();

        if (aud == null) {
            throw new BizException("在处理数据权限时，获取当前登录用户的 AUD 失败!");
        }

        Integer rsMax = aud.getRsMax();

        //如果是 0 (全部权限)，直接放行，不启用过滤器
        if (rsMax != null && rsMax == 0) {
            return;
        }

        //取出 Hibernate 的 Session 并激活过滤器
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("dataScopeFilter");

        //注入参数
        filter.setParameter("rsMax", rsMax);
        filter.setParameter("userId", aud.getId());
        filter.setParameter("rootId", aud.getRootId());

        // 💣 防坑指南：Hibernate 中使用 IN 查询时，如果集合为空会报错
        List<Long> deptIds = aud.getRsAllowDepts();

        if (deptIds == null || deptIds.isEmpty()) {
            filter.setParameterList("deptIds", Collections.singletonList(-1L)); // 塞入一个不存在的无效 ID
        }

        if (deptIds != null && !deptIds.isEmpty()) {
            filter.setParameterList("deptIds", deptIds);
        }
    }
}