package com.ksptool.bio.biz.drive.common.aop;

import java.lang.annotation.*;


/**
 * 标记需要启用云盘空间过滤的接口/方法。
 * 被标注的方法在执行前会自动激活 Hibernate driveSpaceFilter 过滤器。
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DriveSpace {


}
