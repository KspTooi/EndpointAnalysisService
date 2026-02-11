package com.ksptooi.biz.auth.controller;


import com.ksptooi.biz.auth.model.auth.UserLoginDto;
import com.ksptooi.biz.auth.model.session.UserSessionVo;
import com.ksptooi.biz.auth.service.AuthService;
import com.ksptooi.biz.core.model.auth.vo.GetCurrentUserProfile;
import com.ksptooi.biz.core.model.user.dto.LoginDto;
import com.ksptooi.biz.core.model.user.dto.RegisterDto;
import com.ksptooi.biz.core.service.GlobalConfigService;
import com.ksptooi.biz.core.service.SessionService;
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
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@PrintLog
@Controller
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
    public String userLogin(@RequestBody UserLoginDto dto, HttpServletRequest hsr) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        HttpSession hs = hsr.getSession();
        hs.setAttribute("AAAA", "AABC");

        return null;
    }


    @Operation(summary = "登录")
    @PrintLog(sensitiveFields = "password")
    @PostMapping(value = "/loginOld")
    public String login(@Valid LoginDto dto, HttpServletResponse response, HttpServletRequest hsr, RedirectAttributes ra) {
        try {
            var token = authService.loginByPassword(dto.getUsername(), dto.getPassword(), hsr);

            // 设置 cookie
            var cookie = new Cookie("eas-session-id", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);  // 防止 XSS 攻击
            cookie.setMaxAge(7 * 24 * 60 * 60);  // 7天有效期
            response.addCookie(cookie);

            // 登录成功，重定向到客户端UI
            return "redirect:/";
        } catch (BizException e) {
            // 登录失败，添加错误消息并重定向回登录页
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
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

    @Operation(summary = "注册页面")
    @GetMapping("/register")
    public ModelAndView register(HttpServletRequest hsr, RedirectAttributes ra) {
        if (authService.verifyUser(hsr) != null) {
            return new ModelAndView("redirect:/");
        }

        String allowRegister = globalConfigService.getValue(GlobalConfigEnum.ALLOW_USER_REGISTER.getKey());
        if (StringUtils.isBlank(allowRegister) || allowRegister.equals("false")) {
            ra.addFlashAttribute("error", "管理员已禁用注册!");
            return new ModelAndView("redirect:/login");
        }

        String loginBrand = "EAS - 端点分析服务";
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("loginBrand", StringUtils.isBlank(loginBrand) ? "" : loginBrand);
        return mav;
    }

    @Operation(summary = "注销")
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取当前用户
            var user = authService.verifyUser(request);
            // 清除数据库中的 session
            sessionService.closeSession(user.getId());
            // 清除 HTTP session
            request.getSession().invalidate();
            // 重定向到登录页
            return "redirect:/login";
        } catch (Exception e) {
            // 如果发生异常（比如用户已经注销），也重定向到登录页
            return "redirect:/login";
        }
    }

    @Operation(summary = "用户注册")
    @PrintLog(sensitiveFields = "password")
    @PostMapping("/userRegister")
    public ModelAndView userRegister(@Valid RegisterDto dto, BindingResult bindingResult, RedirectAttributes ra) {

        String allowRegister = globalConfigService.getValue(GlobalConfigEnum.ALLOW_USER_REGISTER.getKey());

        if (StringUtils.isBlank(allowRegister) || allowRegister.equals("false")) {
            ra.addFlashAttribute("error", "管理员已禁用注册!");
            return new ModelAndView("redirect:/login");
        }

        ModelAndView mav = new ModelAndView();

        if (bindingResult.hasErrors()) {
            mav.setViewName("redirect:/register");
            ra.addFlashAttribute("error", bindingResult.getAllErrors().getFirst().getDefaultMessage());
            return mav;
        }

        try {
            userService.register(dto.getUsername(), dto.getPassword());
            mav.setViewName("redirect:/login");
            return mav;
        } catch (BizException e) {
            mav.setViewName("redirect:/register");
            ra.addFlashAttribute("error", e.getMessage());
            return mav;
        }
    }

    @Operation(summary = "获取权限")
    @PostMapping("/getPermissions")
    @ResponseBody
    public Result<Set<String>> getPermissions(UserSessionVo session) {
        return Result.success(session.getPermissionCodes());
    }

    @Operation(summary = "获取当前用户信息")
    @PostMapping("/getCurrentUserProfile")
    @ResponseBody
    public Result<GetCurrentUserProfile> getCurrentUserProfile() throws AuthException {
        return Result.success(authService.getCurrentUserProfile());
    }

    @Operation(summary = "获取当前用户头像")
    @GetMapping("/getUserAvatar")
    public ResponseEntity<Resource> getUserAvatar() throws AuthException {
        return authService.getUserAvatar();
    }

    @Operation(summary = "更新当前用户头像")
    @PostMapping("/updateUserAvatar")
    public ResponseEntity<Resource> updateUserAvatar(@RequestParam("file") MultipartFile file) throws AuthException {
        return authService.updateUserAvatar(file);
    }

}
