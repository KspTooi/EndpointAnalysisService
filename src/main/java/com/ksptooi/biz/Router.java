package com.ksptooi.biz;

import com.ksptooi.biz.core.service.GlobalConfigService;
import com.ksptooi.biz.user.service.AuthService;
import com.ksptooi.commons.enums.GlobalConfigEnum;
import com.ksptooi.commons.utils.web.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class Router {

    @Autowired
    private AuthService authService;

    @Autowired
    private GlobalConfigService globalConfigService;

    @GetMapping("/")
    public ModelAndView index(HttpServletRequest hsr) {
        return new ModelAndView("admin-ui-entry");
    }

    @GetMapping("/login")
    public ModelAndView login(HttpServletRequest hsr) {

        //用户已登录则不再响应视图
        if (authService.verifyUser(hsr) != null) {
            return new ModelAndView("redirect:/");
        }

        String loginBrand = "EAS - 单点登录";
        String allowRegister = globalConfigService.getValue(GlobalConfigEnum.ALLOW_USER_REGISTER.getKey());

        ModelAndView mav = new ModelAndView("login");
        mav.addObject("loginBrand", StringUtils.isBlank(loginBrand) ? "" : loginBrand);
        mav.addObject("allowRegister", StringUtils.isNotBlank(allowRegister) && "true".equals(allowRegister));
        return mav;
    }

    @GetMapping("/register")
    public ModelAndView register(HttpServletRequest hsr, RedirectAttributes ra) {

        if (authService.verifyUser(hsr) != null) {
            return new ModelAndView("redirect:/");
        }
        String allowRegister = globalConfigService.getValue(GlobalConfigEnum.ALLOW_USER_REGISTER.getKey());

        if(StringUtils.isBlank(allowRegister) || allowRegister.equals("false")){
            ra.addFlashAttribute("vo", Result.error("管理员已禁用注册!"));
            return new ModelAndView("redirect:/login");
        }

        String loginBrand = "EAS - 端点分析服务";
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("loginBrand", StringUtils.isBlank(loginBrand) ? "" : loginBrand);
        return mav;
    }

}
