package com.ksptool.bio.biz.core.common.jpa;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 雪花算法ID生成器注解,加在你们PO类的ID字段上
 * <p>
 * 使用方式:
 * `@Id
 * `@SnowflakeGenerated
 * `@Column(name = "id", comment = "主键ID")
 * `private Long id;
 * <p>
 * 注意: 这个注解仅用于SpringDataJPA的ID字段。
 * <p>
 * 如果你们在其他地方手工设置了PO中的ID字段，那么这个注解不会生效，最终以手工设置的ID为准。
 */
@IdGeneratorType(SnowflakeIdGenerator.class) // 指向生成器实现类
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface SnowflakeGenerated {
}