package com.ksptooi.biz.user.controller;


import com.ksptooi.biz.core.service.GlobalConfigService;
import com.ksptooi.biz.user.model.session.UserSessionVo;
import com.ksptooi.biz.user.model.user.LoginDto;
import com.ksptooi.biz.user.model.user.RegisterDto;
import com.ksptooi.biz.user.service.AuthService;
import com.ksptooi.biz.user.service.UserService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.enums.GlobalConfigEnum;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.Result;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@PrintLog
@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;
    @Autowired
    private GlobalConfigService globalConfigService;

    @PrintLog(sensitiveFields = "password")
    @PostMapping(value = "/login")
    public String login(@Valid LoginDto dto, HttpServletResponse response, RedirectAttributes ra) {
        try {
            var token = authService.loginByPassword(dto.getUsername(), dto.getPassword());
            
            // 设置 cookie
            var cookie = new Cookie("token", token);
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

    @PrintLog(sensitiveFields = "password")
    @PostMapping(value = "/register")
    @ResponseBody
    public Result<String> register(@Valid @RequestBody RegisterDto dto) {

        String allowRegister = globalConfigService.getValue(GlobalConfigEnum.ALLOW_USER_REGISTER.getKey());

        if(StringUtils.isBlank(allowRegister) || allowRegister.equals("false")){
            return Result.error("管理员已禁用注册");
        }

        try{
            var register = userService.register(dto.getUsername(), dto.getPassword());
            return Result.success("注册成功:"+register.getUsername());
        }catch (BizException e){
            return Result.error(e);
        }

    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取当前用户
            var user = authService.verifyUser(request);
            // 清除数据库中的 session
            authService.logout(user);
            // 清除 HTTP session
            request.getSession().invalidate();
            // 重定向到登录页
            return "redirect:/login";
        } catch (Exception e) {
            // 如果发生异常（比如用户已经注销），也重定向到登录页
            return "redirect:/login";
        }
    }

    @PrintLog(sensitiveFields = "password")
    @PostMapping("/userRegister")
    public ModelAndView userRegister(@Valid RegisterDto dto, BindingResult bindingResult, RedirectAttributes ra) {

        String allowRegister = globalConfigService.getValue(GlobalConfigEnum.ALLOW_USER_REGISTER.getKey());

        if(StringUtils.isBlank(allowRegister) || allowRegister.equals("false")){
            ra.addFlashAttribute("vo", Result.error("管理员已禁用注册!"));
            return new ModelAndView("redirect:/login");
        }

        ModelAndView mav = new ModelAndView();
        
        if (bindingResult.hasErrors()) {
            mav.setViewName("redirect:/register");
            ra.addFlashAttribute("vo", Result.error(bindingResult.getAllErrors().getFirst().getDefaultMessage()));
            return mav;
        }

        try {
            var user = userService.register(dto.getUsername(), dto.getPassword());
            mav.setViewName("redirect:/login");
            ra.addFlashAttribute("vo", Result.success(String.format("注册成功，请登录: %s", user.getUsername())));
            return mav;
        } catch (BizException e) {
            mav.setViewName("redirect:/register");
            ra.addFlashAttribute("vo", Result.error(e.getMessage()));
            return mav;
        }
    }

    @PostMapping("/getPermissions")
    @ResponseBody
    public Result<Set<String>> getPermissions() {

        UserSessionVo session = AuthService.getCurrentUserSession();

        if(session == null){
            return Result.error("获取权限节点失败.");
        }

        return Result.success(session.getPermissions());
    }

}
