package com.ksptooi.biz.auth.service;

import com.ksptooi.biz.auth.repository.GroupRepository;
import com.ksptooi.biz.auth.repository.PermissionRepository;
import com.ksptooi.biz.core.model.auth.vo.GetCurrentUserProfile;
import com.ksptooi.biz.core.model.auth.vo.GetCurrentUserProfilePermissionVo;
import com.ksptooi.biz.core.repository.UserRepository;
import com.ksptooi.biz.core.service.AttachService;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttachService attachService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PermissionRepository permissionRepository;


    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    @Cacheable(cacheNames = "userProfile", key = "#uid")
    public GetCurrentUserProfile getUserProfile(Long uid) throws AuthException {

        var userPo = userRepository.findById(uid).orElseThrow(() -> new AuthException("查询用户信息时出现错误，用户不存在[uid:" + uid + "]"));

        //组装vo
        var vo = new GetCurrentUserProfile();
        vo.setId(userPo.getId());
        vo.setUsername(userPo.getUsername());
        vo.setNickname(userPo.getNickname());
        vo.setGender(userPo.getGender());
        vo.setPhone(userPo.getPhone());
        vo.setEmail(userPo.getEmail());
        vo.setStatus(userPo.getStatus());
        vo.setCreateTime(userPo.getCreateTime());
        vo.setLastLoginTime(userPo.getLastLoginTime());
        vo.setIsSystem(userPo.getIsSystem());
        vo.setAvatarAttachId(null);

        //查询该用户拥有的角色
        var roles = groupRepository.getGroupsByUserId(uid);

        //查询该用户拥有的权限码
        var permissionsPos = permissionRepository.getPermissionsByUserId(uid);

        var permissionsVos = new ArrayList<GetCurrentUserProfilePermissionVo>();
        var roleNames = new ArrayList<String>();

        //处理角色
        for (var role : roles) {
            roleNames.add(role.getName());
        }

        //处理权限
        for (var permissionPo : permissionsPos) {
            var pVo = new GetCurrentUserProfilePermissionVo();
            pVo.setCode(permissionPo.getCode());
            pVo.setName(permissionPo.getName());
            permissionsVos.add(pVo);
        }

        vo.setGroups(roleNames);
        vo.setPermissions(permissionsVos);

        //处理头像
        var avatarAttach = userPo.getAvatarAttach();
        if (avatarAttach != null) {
            vo.setAvatarAttachId(avatarAttach.getId());
        }

        return vo;
    }


    /**
     * 获取当前用户头像
     *
     * @return 用户头像
     */
    public ResponseEntity<Resource> getUserAvatar() throws AuthException {

        var userPo = sessionService.requireUser();
        var avatarAttach = userPo.getAvatarAttach();

        //返回默认头像
        if (avatarAttach == null) {
            return getDefaultAvatar();
        }

        //返回用户头像
        var absolutePath = attachService.getAttachLocalPath(Paths.get(avatarAttach.getPath()));
        if (!Files.exists(absolutePath)) {
            return getDefaultAvatar();
        }

        var resource = new FileSystemResource(absolutePath);
        var filename = avatarAttach.getName();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * 更新当前用户头像
     *
     * @param file 头像文件
     * @return 更新后的头像
     */
    public ResponseEntity<Resource> updateUserAvatar(MultipartFile file) throws AuthException {

        var userPo = sessionService.requireUser();

        if (file == null || file.isEmpty()) {
            throw new AuthException("头像文件不能为空");
        }

        if (StringUtils.isBlank(file.getOriginalFilename())) {
            throw new AuthException("头像文件名不能为空");
        }

        try {
            var attachId = attachService.uploadAttach(file, "user_avatar");
            var attachPo = attachService.requireAttach(attachId);
            userPo.setAvatarAttach(attachPo);
            userRepository.save(userPo);
            return getUserAvatar();
        } catch (BizException e) {
            throw new AuthException(e.getMessage());
        }
    }


    /**
     * 获取默认头像
     *
     * @return 默认头像资源
     */
    private ResponseEntity<Resource> getDefaultAvatar() {
        var resource = new ClassPathResource("views/webconsole/src/assets/EAS_CROWN_SHORT_LOGO.jpg");
        var filename = "EAS_CROWN_SHORT_LOGO.jpg";
        if (!resource.exists()) {
            throw new RuntimeException("默认头像文件不存在");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}
