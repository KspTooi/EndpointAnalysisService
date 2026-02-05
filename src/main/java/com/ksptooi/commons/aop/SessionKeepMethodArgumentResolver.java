package com.ksptooi.commons.aop;

import com.ksptooi.biz.core.model.session.UserSessionVo;
import com.ksptooi.biz.core.service.SessionService;
import com.ksptool.assembly.entity.exception.AuthException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * SessionKeepMethodArgumentResolver 是一个 Spring MVC 参数解析器。
 * <p>
 * 它的作用是自动将当前请求的会话信息注入到 Controller 方法的参数中。
 * 当 Controller 方法声明了 UserSessionVo 类型的参数时，该解析器会自动调用 SessionService.session()
 * 获取当前登录用户的会话数据并填充到参数中，从而简化了在业务代码中手动获取会话的操作。
 */
public class SessionKeepMethodArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 判断是否支持解析当前参数
     *
     * @param parameter 方法参数
     * @return 是否支持解析当前参数
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSessionVo.class);
    }

    @Override
    public @Nullable Object resolveArgument(@NonNull MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

        var session = SessionService.getSession();

        if (session == null) {
            throw new AuthException("当前操作需要有效的用户会话,请先登录!");
        }

        //获取用户Session
        return session;
    }

}
