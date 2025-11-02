package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.group.GroupPo;
import com.ksptooi.biz.core.model.permission.PermissionPo;
import com.ksptooi.biz.core.model.user.*;
import com.ksptooi.biz.core.repository.GroupRepository;
import com.ksptooi.biz.core.repository.UserRepository;
import com.ksptooi.commons.enums.UserEnum;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private AuthService authService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PageResult<GetUserListVo> getUserList(GetUserListDto dto) {
        // 创建分页对象
        Pageable pageable = PageRequest.of(dto.getPageNum() - 1, dto.getPageSize(), Sort.Direction.DESC, "updateTime");

        // 创建查询条件
        UserPo query = new UserPo();
        if (StringUtils.isNotBlank(dto.getUsername())) {
            query.setUsername(dto.getUsername());
        }
        if (dto.getStatus() != null) {
            query.setStatus(dto.getStatus());
        }

        // 创建Example查询对象
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreCase()
                .withIgnoreNullValues();

        // 查询数据
        Page<UserPo> userPage = userRepository.findAll(Example.of(query, matcher), pageable);

        // 转换为VO列表
        List<GetUserListVo> voList = new ArrayList<>();
        for (UserPo po : userPage.getContent()) {
            GetUserListVo vo = new GetUserListVo();
            assign(po, vo);
            if (po.getCreateTime() != null) {
                vo.setCreateTime(po.getCreateTime().format(DATE_TIME_FORMATTER));
            }
            if (po.getLastLoginTime() != null) {
                vo.setLastLoginTime(po.getLastLoginTime().format(DATE_TIME_FORMATTER));
            }
            voList.add(vo);
        }

        // 返回分页视图
        return new PageResult<>(voList, userPage.getTotalElements());
    }

    public GetUserDetailsVo getUserDetails(long id) throws BizException {
        UserPo user = userRepository.findById(id).orElseThrow(() -> new BizException("用户不存在"));
        GetUserDetailsVo vo = new GetUserDetailsVo();
        assign(user, vo);

        if (user.getCreateTime() != null) {
            vo.setCreateTime(user.getCreateTime().format(DATE_TIME_FORMATTER));
        }
        if (user.getLastLoginTime() != null) {
            vo.setLastLoginTime(user.getLastLoginTime().format(DATE_TIME_FORMATTER));
        }

        // 获取用户组信息
        List<UserGroupVo> groupVos = new ArrayList<>();
        List<GroupPo> allGroups = groupRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));
        HashSet<Long> userGroupIds = new HashSet<>();
        for (GroupPo group : user.getGroups()) {
            userGroupIds.add(group.getId());
        }

        for (GroupPo group : allGroups) {
            UserGroupVo groupVo = new UserGroupVo();
            assign(group, groupVo);
            groupVo.setHasGroup(userGroupIds.contains(group.getId()));
            groupVos.add(groupVo);
        }
        vo.setGroups(groupVos);

        // 获取用户权限信息
        List<PermissionPo> userPermissions = userRepository.findUserPermissions(id);
        vo.setPermissions(as(userPermissions, UserPermissionVo.class));

        return vo;
    }

    @Transactional
    public void addUser(AddUserDto dto) throws BizException {
        if (StringUtils.isBlank(dto.getUsername())) {
            throw new BizException("用户名不能为空");
        }

        UserPo existingUserByName = userRepository.findByUsername(dto.getUsername());
        if (existingUserByName != null) {
            throw new BizException("用户名 '" + dto.getUsername() + "' 已被使用");
        }

        if (StringUtils.isBlank(dto.getPassword())) {
            throw new BizException("新建用户时密码不能为空");
        }

        UserPo user = new UserPo();
        assign(dto, user);
        user.setPassword(encryptPassword(dto.getPassword(), dto.getUsername()));
        user.setGroups(getGroupSet(dto.getGroupIds()));
        userRepository.save(user);
    }

    @Transactional
    public void editUser(EditUserDto dto) throws BizException {
        if (StringUtils.isBlank(dto.getUsername())) {
            throw new BizException("用户名不能为空");
        }

        UserPo existingUserByName = userRepository.findByUsername(dto.getUsername());
        if (existingUserByName != null && !existingUserByName.getId().equals(dto.getId())) {
            throw new BizException("用户名 '" + dto.getUsername() + "' 已被使用");
        }

        UserPo user = userRepository.findById(dto.getId()).orElseThrow(() -> new BizException("用户不存在"));

        if (StringUtils.isNotBlank(dto.getPassword())) {
            dto.setPassword(encryptPassword(dto.getPassword(), dto.getUsername()));
        }
        if (StringUtils.isBlank(dto.getPassword())) {
            dto.setPassword(user.getPassword());
        }

        assign(dto, user);
        user.setGroups(getGroupSet(dto.getGroupIds()));
        userRepository.save(user);

        authService.refreshUserSession(user.getId());
    }

    @Transactional
    public void removeUser(long id) throws BizException {
        if (!userRepository.existsById(id)) {
            throw new BizException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    private HashSet<GroupPo> getGroupSet(List<Long> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(groupRepository.findAllById(groupIds));
    }

    /**
     * 密码加密
     *
     * @param password 密码
     * @param username 用户名（用作盐值）
     * @throws BizException 加密失败时抛出异常
     */
    private String encryptPassword(String password, String username) throws BizException {
        try {
            // 使用用户名作为盐，加密密码：password + username
            String salted = password + username;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BizException("密码加密失败", e);
        }
    }


    public UserPo getUser(String username) throws BizException {
        UserPo user = userRepository.findByUsername(username);
        if (user == null) {
            throw new BizException("该用户不存在");
        }
        return user;
    }

    public UserPo register(String username, String password) throws BizException {
        // 检查是否已存在同名用户
        if (userRepository.findByUsername(username) != null) {
            throw new BizException("用户名已存在");
        }

        // 使用用户名作为盐，加密密码：password + username
        String salted = password + username;
        String hashedPassword = hashSHA256(salted);

        UserPo newUser = new UserPo();
        newUser.setUsername(username);
        newUser.setPassword(hashedPassword);
        // 根据需要，可设置其它字段（如邮箱、昵称等）

        return userRepository.save(newUser);
    }

    private String hashSHA256(String input) throws BizException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BizException("密码加密失败", e);
        }
    }

    /**
     * 校验系统内置用户
     * 检查数据库中是否存在所有系统内置用户，如果不存在则自动创建
     * 对于admin用户，会赋予所有现有用户组
     *
     * @return 校验结果消息
     */
    @Transactional(rollbackFor = Exception.class)
    public String validateSystemUsers() {
        // 获取所有系统内置用户枚举
        UserEnum[] userEnums = UserEnum.values();

        // 记录已存在和新增的用户数量
        int existCount = 0;
        int addedCount = 0;
        List<String> addedUsers = new ArrayList<>();

        // 遍历所有系统内置用户
        for (UserEnum userEnum : userEnums) {
            String username = userEnum.getUsername();

            // 检查用户是否已存在
            UserPo existingUser = userRepository.findByUsername(username);
            if (existingUser != null) {
                existCount++;
                // 如果是admin用户，更新其用户组
                if (userEnum == UserEnum.ADMIN) {
                    updateAdminGroups(existingUser);
                }
                continue;
            }

            try {
                // 创建新用户，密码与用户名相同
                UserPo newUser = register(username, username);
                newUser.setNickname(userEnum.getNickname());

                // 如果是admin用户，赋予所有用户组
                if (userEnum == UserEnum.ADMIN) {
                    updateAdminGroups(newUser);
                }

                addedCount++;
                addedUsers.add(username);
            } catch (BizException e) {
                // 注册失败的情况已经在register方法中处理过了
                continue;
            }
        }

        // 返回结果消息
        if (addedCount > 0) {
            return String.format("校验完成，已添加 %d 个缺失的用户（%s），已存在 %d 个用户",
                    addedCount, String.join("、", addedUsers), existCount);
        }

        return String.format("校验完成，所有 %d 个系统用户均已存在", existCount);
    }

    /**
     * 更新admin用户的用户组，确保拥有所有现有用户组
     */
    private void updateAdminGroups(UserPo adminUser) {
        // 获取所有用户组
        List<GroupPo> allGroups = groupRepository.findAll();

        // 更新admin的用户组
        adminUser.getGroups().clear();
        adminUser.getGroups().addAll(allGroups);

        // 保存更改
        userRepository.save(adminUser);
    }
}
