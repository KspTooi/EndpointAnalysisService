package com.ksptooi.biz.auth.common;

import com.google.gson.Gson;
import com.ksptool.assembly.entity.web.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonAuthEntryPoint implements AuthenticationEntryPoint {

    private final Gson gson = new Gson();

    @NullMarked
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        var body = Result.error(401, "登录已过期或未登录");
        response.getWriter().write(gson.toJson(body));
    }
}