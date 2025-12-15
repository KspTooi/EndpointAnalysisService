package com.ksptooi.biz.core.service;


import com.ksptooi.biz.core.model.group.*;
import com.ksptooi.biz.core.model.permission.PermissionPo;
import com.ksptooi.biz.core.model.session.UserSessionPo;
import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.core.repository.GroupRepository;
import com.ksptooi.biz.core.repository.PermissionRepository;
import com.ksptooi.biz.core.repository.UserSessionRepository;
import com.ksptooi.commons.enums.GroupEnum;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ksptool.entities.Entities.as;

@Service
public class GroupService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private GroupRepository repository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private AuthService authService;

    public List<GetGroupDefinitionsVo> getGroupDefinitions() {
        List<GroupPo> pos = repository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
        return as(pos, GetGroupDefinitionsVo.class);
    }

    public PageResult<GetGroupListVo> getGroupList(GetGroupListDto dto) {
        Page<GetGroupListVo> pagePos = repository.getGroupList(dto, dto.pageRequest());
        return PageResult.success(pagePos.getContent(), pagePos.getTotalElements());
    }

    public GetGroupDetailsVo getGroupDetails(long id) throws BizException {

        GroupPo po = repository.findById(id).orElseThrow(() -> new BizException("用户组不存在"));

        List<PermissionPo> allPermPos = permissionRepository.findAll();
        Set<PermissionPo> hasPermPos = po.getPermissions();

        GetGroupDetailsVo vo = as(po, GetGroupDetailsVo.class);
        List<GroupPermissionDefinitionVo> defVos = new ArrayList<>();

        for (var permission : allPermPos) {

            var defVo = as(permission, GroupPermissionDefinitionVo.class);
            defVo.setHas(1);

            for (var hasPermPo : hasPermPos) {
                if (permission.getId().equals(hasPermPo.getId())) {
                    defVo.setHas(0);
                }
            }

            defVos.add(defVo);
        }

        vo.setPermissions(defVos);
        return vo;
    }

    @Transactional
    public void addGroup(AddGroupDto dto) throws BizException {
        if (repository.existsByCode(dto.getCode())) {
            throw new BizException("用户组标识已存在");
        }

        GroupPo group = new GroupPo();
        group.setCode(dto.getCode());
        group.setName(dto.getName());
        group.setDescription(dto.getDescription());
        group.setStatus(dto.getStatus());
        group.setSortOrder(dto.getSortOrder());

        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            Set<PermissionPo> newPermissions = new HashSet<>(
                    permissionRepository.findAllById(dto.getPermissionIds())
            );
            group.getPermissions().addAll(newPermissions);
        }

        repository.save(group);
    }

    @Transactional
    public void editGroup(EditGroupDto dto) throws BizException {
        GroupPo group = repository.findById(dto.getId()).orElseThrow(() -> new BizException("用户组不存在"));

        if (!group.getCode().equals(dto.getCode())) {
            if (repository.existsByCode(dto.getCode())) {
                throw new BizException("用户组标识重复");
            }
        }

        group.setCode(dto.getCode());
        group.setName(dto.getName());
        group.setDescription(dto.getDescription());
        group.setStatus(dto.getStatus());
        group.setSortOrder(dto.getSortOrder());

        group.getPermissions().clear();

        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            Set<PermissionPo> newPermissions = new HashSet<>(
                    permissionRepository.findAllById(dto.getPermissionIds())
            );
            group.getPermissions().addAll(newPermissions);
        }

        repository.save(group);

        List<UserSessionPo> activeSessions = userSessionRepository.getUserSessionByGroupId(group.getId());

        for (UserSessionPo session : activeSessions) {
            authService.refreshUserSession(session.getUserId());
        }
    }

    @Transactional
    public void applyPermission(ApplyPermissionDto dto) throws BizException {

        GroupPo group = repository.findById(dto.getGroupId()).orElseThrow(() -> new BizException("用户组不存在"));
        if (group == null) {
            throw new BizException("用户组不存在");
        }
    
        //去重权限代码
        var pSet = new HashSet<>(dto.getPermissionCodes());

        //获取权限列表
        var permPos = permissionRepository.getPermissionsByCodes(pSet);

        //if(pSet.size() != permPos.size()){
        //    throw new BizException("无法完成应用,至少有一个或多个权限不存在，请刷新页面重试。");
        //}

        //清空用户组与权限的关联关系
        group.getPermissions().clear();
        group.getPermissions().addAll(permPos);
        repository.save(group);

        //处理在线用户会话
        List<UserSessionPo> activeSessions = userSessionRepository.getUserSessionByGroupId(group.getId());
        for (UserSessionPo session : activeSessions) {
            authService.refreshUserSession(session.getUserId());
        }
    }




    @Transactional
    public void removeGroup(long id) throws BizException {

        GroupPo group = repository.getGroupWithUserAndPermission(id);

        if (group == null) {
            throw new BizException("用户组不存在");
        }

        if (group.getIsSystem()) {
            throw new BizException("系统用户组不能删除");
        }

        //获取有多少用户在该组下
        int userCount = group.getUsers().size();

        if (userCount > 0) {
            throw new BizException(String.format("该用户组下有 %d 个用户，请先取消所有关联关系后再尝试移除", userCount));
        }


        // 获取该用户组下所有在线用户的会话
        List<UserSessionPo> activeSessions = userSessionRepository.getUserSessionByGroupId(group.getId());

        // 清空用户组与用户的关联关系
        if (!group.getUsers().isEmpty()) {
            for (UserPo user : new HashSet<>(group.getUsers())) {
                user.getGroups().remove(group);
            }
            group.getUsers().clear();
        }


        // 清空用户组与权限的关联关系
        if (group.getPermissions() != null) {
            group.getPermissions().clear();
        }

        // 保存更改并刷新
        repository.save(group);
        repository.flush();
        repository.delete(group);

        // 刷新每个用户的会话
        for (UserSessionPo session : activeSessions) {
            authService.refreshUserSession(session.getUserId());
        }
    }

    /**
     * 校验系统内置组
     * 检查数据库中是否存在所有系统内置组，如果不存在则自动创建
     * 对于管理员组，会赋予所有现有权限
     *
     * @return 校验结果消息
     */
    @Transactional(rollbackFor = Exception.class)
    public String validateSystemGroups() {
        // 获取所有系统内置组枚举
        GroupEnum[] groupEnums = GroupEnum.values();

        // 记录已存在和新增的组数量
        int existCount = 0;
        int addedCount = 0;
        List<String> addedGroups = new ArrayList<>();

        // 获取所有权限（用于管理员组）
        List<PermissionPo> allPermissions = permissionRepository.findAll();

        // 遍历所有系统内置组
        for (GroupEnum groupEnum : groupEnums) {
            String code = groupEnum.getCode();

            // 检查组是否已存在
            if (repository.existsByCode(code)) {
                existCount++;

                //管理员组重新赋予所有权限
                if (code.equals(GroupEnum.ADMIN.getCode())) {
                    GroupPo byCode = repository.findByCode(code);
                    byCode.getPermissions().addAll(allPermissions);
                    repository.save(byCode);
                }

                continue;
            }

            // 创建新的组
            GroupPo group = new GroupPo();
            group.setCode(code);
            group.setName(groupEnum.getName());
            group.setDescription(groupEnum.getName());
            group.setIsSystem(true);
            group.setSortOrder(repository.findMaxSortOrder() + 1);
            group.setStatus(1); // 启用状态

            // 如果是管理员组，赋予所有权限
            if (groupEnum == GroupEnum.ADMIN) {
                group.getPermissions().addAll(allPermissions);
            }

            // 保存组
            repository.save(group);

            addedCount++;
            addedGroups.add(code);
        }

        // 返回结果消息
        if (addedCount > 0) {
            return String.format("校验完成，已添加 %d 个缺失的用户组（%s），已存在 %d 个用户组",
                    addedCount, String.join("、", addedGroups), existCount);
        }

        return String.format("校验完成，所有 %d 个系统用户组均已存在", existCount);
    }

}
