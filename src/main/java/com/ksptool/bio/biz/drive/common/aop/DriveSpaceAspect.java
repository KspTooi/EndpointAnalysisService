package com.ksptool.bio.biz.drive.common.aop;

import com.ksptool.assembly.entity.exception.BizException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

/**
 * 云盘空间过滤器切面
 * 用于在 Hibernate 查询时为 Session 启用云盘空间过滤器
 */
@Aspect
@Component
public class DriveSpaceAspect {

    private static final String DRIVE_SPACE_FILTER_NAME = "driveSpaceFilter";

    @PersistenceContext
    private EntityManager entityManager;


    @Pointcut("@annotation(DriveSpace)")
    public void driveSpacePointcut() {
    }

    @Before("driveSpacePointcut()")
    public void enableDriveSpaceFilter(JoinPoint joinPoint) throws BizException {

        Long driveSpaceId = resolveDriveSpaceId(joinPoint.getArgs());
        if (driveSpaceId == null) {
            throw new BizException("在处理云盘空间过滤时，driveSpaceId 为空");
        }
        if (driveSpaceId <= 0) {
            throw new BizException("在处理云盘空间过滤时，driveSpaceId 非法");
        }

        //取出 Hibernate 的 Session 并激活过滤器
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(DRIVE_SPACE_FILTER_NAME);

        //注入参数
        filter.setParameter("driveSpaceId", driveSpaceId);
    }

    // 后置：无论成功失败，必定关闭过滤器
    @After("driveSpacePointcut()")
    public void disableFilter() {
        Session session = entityManager.unwrap(Session.class);
        // 强制关闭，清空当前线程/Session的权限上下文
        session.disableFilter(DRIVE_SPACE_FILTER_NAME);
    }

    private Long resolveDriveSpaceId(Object[] args) throws BizException {

        if (args == null || args.length == 0) {
            throw new BizException("在处理云盘空间过滤时，未找到方法入参");
        }

        for (var arg : args) {

            if (arg == null) {
                continue;
            }

            BeanWrapperImpl bw = new BeanWrapperImpl(arg);
            if (!bw.isReadableProperty("driveSpaceId")) {
                continue;
            }

            Object value = bw.getPropertyValue("driveSpaceId");
            if (value == null) {
                continue;
            }

            if (value instanceof Long) {
                return (Long) value;
            }
            if (value instanceof Integer) {
                return ((Integer) value).longValue();
            }
            if (value instanceof String) {
                return parseLong((String) value);
            }
        }

        throw new BizException("在处理云盘空间过滤时，入参中缺少 driveSpaceId");
    }

    private Long parseLong(String value) throws BizException {

        if (value == null) {
            throw new BizException("在处理云盘空间过滤时，driveSpaceId 为空字符串");
        }

        String v = value.trim();
        if (v.isEmpty()) {
            throw new BizException("在处理云盘空间过滤时，driveSpaceId 为空字符串");
        }

        try {
            return Long.parseLong(v);
        } catch (NumberFormatException e) {
            throw new BizException("在处理云盘空间过滤时，driveSpaceId 不是有效数字");
        }
    }
}