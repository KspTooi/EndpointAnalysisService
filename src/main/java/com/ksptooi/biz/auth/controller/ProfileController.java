package com.ksptooi.biz.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.ksptooi.biz.auth.service.AuthService;
import com.ksptooi.biz.core.model.auth.vo.GetCurrentUserProfile;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "Profile", description = "用户档案管理")
@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private AuthService authService;

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
