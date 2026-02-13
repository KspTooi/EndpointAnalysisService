package com.ksptooi.biz.auth.controller;

import com.ksptooi.biz.auth.service.UserProfileService;
import com.ksptooi.biz.core.model.auth.vo.GetCurrentUserProfile;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.ksptooi.biz.auth.service.SessionService.session;

@Tag(name = "Profile", description = "用户档案管理")
@RestController
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService authService;

    @Operation(summary = "获取当前用户信息")
    @PostMapping("/getCurrentUserProfile")
    @ResponseBody
    public Result<GetCurrentUserProfile> getCurrentUserProfile() throws AuthException {
        return Result.success(authService.getUserProfile(session().getUserId()));
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
