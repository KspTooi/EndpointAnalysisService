package com.ksptooi.biz.auth.controller;


import com.ksptooi.biz.auth.model.auth.AuthUserDetails;
import com.ksptooi.biz.auth.model.auth.dto.UserLoginDto;
import com.ksptooi.biz.auth.model.auth.vo.UserLoginVo;
import com.ksptooi.biz.auth.model.session.vo.UserSessionVo;
import com.ksptooi.biz.auth.service.AuthService;
import com.ksptooi.biz.auth.service.SessionService;
import com.ksptooi.biz.core.model.auth.vo.GetCurrentUserProfile;
import com.ksptooi.biz.core.model.user.dto.RegisterDto;
import com.ksptooi.biz.core.service.GlobalConfigService;
import com.ksptooi.biz.core.service.UserService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.enums.GlobalConfigEnum;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static com.ksptool.entities.Entities.as;

@PrintLog
@RestController
@Tag(name = "Auth", description = "认证管理")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Operation(summary = "登录(新)")
    @PrintLog(sensitiveFields = "password")
    @PostMapping(value = "/userLogin")
    public Result<UserLoginVo> userLogin(@RequestBody UserLoginDto dto, HttpServletResponse hsrp) throws BizException {

        Authentication auth = null;

        try {
            //使用Spring Security进行用户名密码认证
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
            );
        } catch (AuthenticationException e) {
            return Result.error("用户名或密码错误");
        }

        //获取认证用户
        var aud = (AuthUserDetails) auth.getPrincipal();

        //创建用户会话
        var sessionId = sessionService.createSession(aud);

        // 设置 cookie
        var cookie = new Cookie("bio-session-id", sessionId);
        cookie.setPath("/");
        cookie.setHttpOnly(true);  // 防止 XSS 攻击
        cookie.setMaxAge(7 * 24 * 60 * 60);  // 7天有效期
        hsrp.addCookie(cookie);

        //组装Vo
        var vo = as(aud, UserLoginVo.class);
        vo.setSessionId(sessionId);
        return Result.success(vo);
    }


    @Operation(summary = "注册")
    @PrintLog(sensitiveFields = "password")
    @PostMapping(value = "/register")
    @ResponseBody
    public Result<String> register(@Valid @RequestBody RegisterDto dto) {

        String allowRegister = globalConfigService.getValue(GlobalConfigEnum.ALLOW_USER_REGISTER.getKey());

        if (StringUtils.isBlank(allowRegister) || allowRegister.equals("false")) {
            return Result.error("管理员已禁用注册");
        }

        try {
            var register = userService.register(dto.getUsername(), dto.getPassword());
            return Result.success("注册成功:" + register.getUsername());
        } catch (BizException e) {
            return Result.error(e);
        }

    }

    @Operation(summary = "注销")
    @GetMapping("/logout")
    public Result<String> logout(HttpServletRequest request, HttpServletResponse response) {

        // 清除数据库中的 session
        //sessionService.closeSession(user.getId());

        return Result.success("注销成功");
    }


    @Operation(summary = "获取权限")
    @PostMapping("/getPermissions")
    @ResponseBody
    public Result<Set<String>> getPermissions(UserSessionVo session) {
        return Result.success(session.getPermissionCodes());
    }



}
