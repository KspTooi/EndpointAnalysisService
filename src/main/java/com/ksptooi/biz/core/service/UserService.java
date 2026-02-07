package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.group.GroupPo;
import com.ksptooi.biz.core.model.org.OrgPo;
import com.ksptooi.biz.core.model.permission.PermissionPo;
import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.core.model.user.dto.AddUserDto;
import com.ksptooi.biz.core.model.user.dto.BatchEditUserDto;
import com.ksptooi.biz.core.model.user.dto.EditUserDto;
import com.ksptooi.biz.core.model.user.dto.GetUserListDto;
import com.ksptooi.biz.core.model.user.dto.ImportUserDto;
import com.ksptooi.biz.core.model.user.vo.GetUserDetailsVo;
import com.ksptooi.biz.core.model.user.vo.GetUserListVo;
import com.ksptooi.biz.core.model.user.vo.UserGroupVo;
import com.ksptooi.biz.core.model.user.vo.UserPermissionVo;
import com.ksptooi.biz.core.repository.GroupRepository;
import com.ksptooi.biz.core.repository.OrgRepository;
import com.ksptooi.biz.core.repository.UserRepository;
import com.ksptooi.commons.dataprocess.Str;
import com.ksptooi.commons.enums.UserEnum;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class UserService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private OrgRepository orgRepository;
    
    @Autowired
    private SessionService sessionService;


    /**
     * 获取用户列表
     *
     * @param dto 获取用户列表DTO
     * @return 用户列表VO
     */
    public PageResult<GetUserListVo> getUserList(GetUserListDto dto) {

        var vPos = userRepository.getUserList(dto, dto.pageRequest());

        // 返回分页视图
        return PageResult.success(vPos.getContent(), vPos.getTotalElements());
    }

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户详情VO
     * @throws BizException 用户不存在
     */
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
        List<PermissionPo> userPermissions = userRepository.getUserPermissions(id);
        vo.setPermissions(as(userPermissions, UserPermissionVo.class));
        return vo;
    }

    /**
     * 新增用户
     * 新增用户时，用户默认不是系统内置用户
     *
     * @param dto 新增用户DTO
     * @throws BizException 用户名已存在或无法新增用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUser(AddUserDto dto) throws BizException {
        if (StringUtils.isBlank(dto.getUsername())) {
            throw new BizException("用户名不能为空");
        }

        if (StringUtils.isBlank(dto.getPassword())) {
            throw new BizException("新建用户时密码不能为空");
        }

        if (userRepository.countByUsername(dto.getUsername()) > 0) {
            throw new BizException("用户名 '" + dto.getUsername() + "' 已被使用");
        }

        UserPo user = new UserPo();
        assign(dto, user);
        user.setPassword(encryptPassword(dto.getPassword(), dto.getUsername()));
        user.setGroups(getGroupSet(dto.getGroupIds()));
        user.setIsSystem(0);

        //处理组织架构
        if (dto.getDeptId() != null) {

            OrgPo org = orgRepository.getDeptById(dto.getDeptId());

            if (org == null) {
                throw new BizException("部门:" + dto.getDeptId() + "不存在");
            }

            //获取部门所在的公司
            OrgPo rootOrg = orgRepository.getRootById(org.getRootId());

            if (rootOrg == null) {
                throw new BizException("公司:" + org.getRootId() + "不存在");
            }

            //设置用户所属公司和部门
            user.setRootId(rootOrg.getId());
            user.setRootName(rootOrg.getName());
            user.setDeptId(org.getId());
            user.setDeptName(org.getName());
        }

        if (dto.getDeptId() == null) {
            user.setRootId(null);
            user.setRootName(null);
            user.setDeptId(null);
            user.setDeptName(null);
        }


        userRepository.save(user);
    }

    /**
     * 编辑用户
     *
     * @param dto 编辑用户DTO
     * @throws BizException 用户不存在或无法编辑系统内置用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void editUser(EditUserDto dto) throws Exception {

        UserPo user = userRepository.findById(dto.getId()).orElseThrow(() -> new BizException("用户不存在"));

        if (StringUtils.isNotBlank(dto.getPassword())) {
            dto.setPassword(encryptPassword(dto.getPassword(), user.getUsername()));
        }
        if (StringUtils.isBlank(dto.getPassword())) {
            dto.setPassword(user.getPassword());
        }

        assign(dto, user);
        user.setGroups(getGroupSet(dto.getGroupIds()));

        //处理组织架构
        if (dto.getDeptId() != null) {

            OrgPo org = orgRepository.getDeptById(dto.getDeptId());

            if (org == null) {
                throw new BizException("部门:" + dto.getDeptId() + "不存在");
            }

            //获取部门所在的公司
            OrgPo rootOrg = orgRepository.getRootById(org.getRootId());

            if (rootOrg == null) {
                throw new BizException("公司:" + org.getRootId() + "不存在");
            }

            //设置用户所属公司和部门
            user.setRootId(rootOrg.getId());
            user.setRootName(rootOrg.getName());
            user.setDeptId(org.getId());
            user.setDeptName(org.getName());
        }

        if (dto.getDeptId() == null) {
            user.setRootId(null);
            user.setRootName(null);
            user.setDeptId(null);
            user.setDeptName(null);
        }

        userRepository.save(user);
        sessionService.updateSession(user.getId());
    }

    /**
     * 移除用户
     * 如果用户是系统内置用户，则无法移除
     *
     * @param id 用户ID
     * @throws BizException 用户不存在或无法移除系统内置用户
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeUser(long id) throws BizException {

        var userPo = userRepository.findById(id).orElseThrow(() -> new BizException("用户不存在"));

        if (userPo.getIsSystem() == 1) {
            throw new BizException("无法移除系统内置用户:" + userPo.getUsername());
        }

        userRepository.delete(userPo);
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
        newUser.setGender(2);
        newUser.setIsSystem(0);
        // 根据需要，可设置其它字段（如邮箱、昵称等）

        return userRepository.save(newUser);
    }

    public UserPo registerSystemUser(String username, String password) throws BizException {
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
        newUser.setGender(2);
        newUser.setIsSystem(1);
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
                UserPo newUser = registerSystemUser(username, username);
                newUser.setNickname(userEnum.getNickname());

                // 如果是admin用户，赋予所有用户组
                if (userEnum == UserEnum.ADMIN) {
                    updateAdminGroups(newUser);
                    newUser.setIsSystem(1);
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


    /**
     * 导入用户
     *
     * @param data 导入用户数据
     * @return 导入用户数量
     * @throws BizException 导入用户失败
     */
    public int importUser(List<ImportUserDto> data) throws BizException {

        var addPos = new ArrayList<UserPo>();

        //搜集所有用户名
        var usernameSet = new HashSet<String>();
        for (ImportUserDto dto : data) {
            usernameSet.add(dto.getUsername());
        }

        //搜集所有被占用的用户名
        var occupiedNames = userRepository.getUsernameSetByUsernames(new ArrayList<>(usernameSet));

        if (occupiedNames.size() > 0) {
            throw new BizException("以下用户名已被占用: " + String.join(",", occupiedNames));
        }

        for (ImportUserDto dto : data) {

            UserPo user = new UserPo();
            user.setUsername(dto.getUsername());
            user.setPassword(encryptPassword(dto.getPassword(), dto.getUsername()));
            user.setNickname(dto.getNickname());

            //处理性别 0:男 1:女 2:不愿透露
            if (Str.isNotBlank(dto.getGender())) {

                var genderStr = dto.getGender();

                if (genderStr.equals("男")) {
                    user.setGender(0);
                }

                if (genderStr.equals("女")) {
                    user.setGender(1);
                }

                if (genderStr.equals("不愿透露")) {
                    user.setGender(2);
                }

            }

            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            user.setLoginCount(0);
            user.setStatus(0);//正常

            //处理所属企业(如果有)
            if (Str.isNotBlank(dto.getRootName())) {
                var rootName = dto.getRootName();
                var root = orgRepository.getRootByName(rootName);
                if (root == null) {
                    throw new BizException("所属企业:" + rootName + "不存在");
                }
                user.setRootId(root.getId());
                user.setRootName(root.getName());
            }

            //处理所属部门(如果有)
            if (Str.isNotBlank(dto.getDeptName())) {
                var deptName = dto.getDeptName();

                try {
                    var dept = orgRepository.getDeptByName(deptName, user.getRootId());

                    if (dept == null) {
                        throw new BizException("所属部门:" + deptName + "不存在或不属于指定企业");
                    }
                    user.setDeptId(dept.getId());
                    user.setDeptName(dept.getName());

                } catch (IncorrectResultSizeDataAccessException e) {
                    throw new BizException("公司:" + user.getRootName() + "下存在多个同名称部门:" + deptName + "，不支持批量导入");
                }

            }

            user.setActiveCompany(null);
            user.setActiveEnv(null);
            user.setAvatarAttach(null);
            user.setIsSystem(0);//0:否 1:是
            addPos.add(user);
        }

        //批量新增用户
        userRepository.saveAll(addPos);
        return addPos.size();
    }

    /**
     * 批量编辑用户
     * 
     * @param dto 批量编辑用户DTO
     * @throws BizException 批量编辑用户失败
     */
    public int batchEditUser(BatchEditUserDto dto) throws BizException {

        var userIds = dto.getIds();
        var userPos = userRepository.findAllById(userIds);
        
        if (userPos.size() != userIds.size()) {
            throw new BizException("部分用户不存在");
        }


        //处理批量解封
        if (dto.getKind() == 0) {
            for (UserPo user : userPos) {
                user.setStatus(0);
            }
            userRepository.saveAll(userPos);
            return userPos.size();
        }

        //批量封禁
        if (dto.getKind() == 1) {
            for (UserPo user : userPos) {

                //跳过内置用户
                if (user.isSystem()) {
                    continue;
                }

                user.setStatus(1);
            }
            userRepository.saveAll(userPos);
            return userPos.size();
        }

        //批量删除
        if (dto.getKind() == 2) {

            //直接剔除内置用户
            userPos = userPos.stream().filter(user -> !user.isSystem()).collect(Collectors.toList());

            //批量删除用户
            userRepository.deleteAll(userPos);

            //销毁被删除用户的session(如果有) 强行踢他们下线
            

            return userPos.size();
        }


        //批量变更部门
        if (dto.getKind() == 3) {

            var dept = orgRepository.getDeptById(dto.getDeptId());

            if (dept == null) {
                throw new BizException("部门:" + dto.getDeptId() + "不存在");
            }

            var root = orgRepository.getRootById(dept.getRootId());

            if (root == null) {
                throw new BizException("部门上级企业:" + dept.getRootId() + "不存在");
            }

            for (UserPo user : userPos) {
                user.setRootId(root.getId());
                user.setRootName(root.getName());
                user.setDeptId(dept.getId());
                user.setDeptName(dept.getName());
            }

            //批量更新用户
            userRepository.saveAll(userPos);
            return userPos.size();
        }

        throw new BizException("不支持的批量操作类型: " + dto.getKind() + "，请检查kind参数");
    }


}
