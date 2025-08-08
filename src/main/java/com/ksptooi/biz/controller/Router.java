package com.ksptooi.biz.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Router {

    @GetMapping("/ras")
    public ModelAndView index(HttpServletRequest hsr) {
        return new ModelAndView("admin-ui-entry");
    }

}
