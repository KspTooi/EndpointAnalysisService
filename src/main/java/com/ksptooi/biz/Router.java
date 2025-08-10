package com.ksptooi.biz;

import com.ksptooi.biz.core.service.GlobalConfigService;
import com.ksptooi.biz.core.service.PanelInstallWizardService;
import com.ksptooi.biz.user.service.AuthService;
import com.ksptooi.commons.enums.GlobalConfigEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Router {

    @Autowired
    private AuthService authService;

    @Autowired
    private GlobalConfigService globalConfigService;

    @Autowired
    private PanelInstallWizardService installWizardService;

    @GetMapping("/")
    public ModelAndView index(HttpServletRequest hsr) {

        //如果启用向导模式 跳转到向导
        if(installWizardService.hasInstallWizardMode()){
            return new ModelAndView("redirect:/install-wizard/");
        }

        return new ModelAndView("admin-ui-entry");
    }

    @GetMapping("/login")
    public ModelAndView login(HttpServletRequest hsr) {

        //如果启用向导模式 跳转到向导
        if(installWizardService.hasInstallWizardMode()){
            return new ModelAndView("redirect:/install-wizard/");
        }

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

    

}
