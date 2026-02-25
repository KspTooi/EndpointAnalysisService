package com.ksptool.bio.commons.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 通用响应结果代码
 */
@AllArgsConstructor
@Getter
public enum ResultCode {

    SUCCESS(0, HttpStatus.OK, "操作成功"),

    BIZ_ERROR(1, HttpStatus.OK, "业务异常"),

    INTERNAL_ERROR(2, HttpStatus.INTERNAL_SERVER_ERROR, "系统内部错误"),

    PARAM_ERROR(3, HttpStatus.BAD_REQUEST, "参数异常"),

    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "用户会话异常"),

    FORBIDDEN(403, HttpStatus.FORBIDDEN, "权限不足"),

    REQUIRE_ROOT(101, HttpStatus.OK, "用户未绑定租户"),

    ;

    //业务状态码
    private final int code;

    //HTTP状态码
    private final HttpStatus httpStatus;

    //描述信息
    private final String message;


}
