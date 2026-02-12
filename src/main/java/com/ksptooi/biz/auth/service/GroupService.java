package com.ksptooi.biz.auth.service;


import com.ksptooi.biz.auth.model.GroupPermissionPo;
import com.ksptooi.biz.auth.model.group.GroupPo;
import com.ksptooi.biz.auth.model.group.dto.*;
import com.ksptooi.biz.auth.model.group.vo.*;
import com.ksptooi.biz.auth.model.permission.PermissionPo;
import com.ksptooi.biz.auth.model.session.UserSessionPo;
import com.ksptooi.biz.auth.repository.*;
import com.ksptooi.biz.core.model.resource.ResourcePo;
import com.ksptooi.biz.core.repository.ResourceRepository;
import com.ksptooi.commons.dataprocess.Str;
import com.ksptooi.commons.enums.GroupEnum;
import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ksptool.entities.Entities.as;


@Slf4j
@Service
public class GroupService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private GroupRepository repository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserGroupRepository ugRepository;

    @Autowired
    private GroupPermissionRepository gpRepository;


    /**
     * 获取用户组定义列表
     *
     * @return 用户组定义列表
     */
    public List<GetGroupDefinitionsVo> getGroupDefinitions() {
        List<GroupPo> pos = repository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
        return as(pos, GetGroupDefinitionsVo.class);
    }

    /**
     * 获取用户组列表
     *
     * @param dto 获取用户组列表参数
     * @return 用户组列表
     */
    public PageResult<GetGroupListVo> getGroupList(GetGroupListDto dto) {
        Page<GetGroupListVo> pagePos = repository.getGroupList(dto, dto.pageRequest());
        return PageResult.success(pagePos.getContent(), pagePos.getTotalElements());
    }

    /**
     * 获取用户组详情
     *
     * @param id 用户组ID
     * @return 用户组详情
     * @throws BizException 用户组不存在
     */
    public GetGroupDetailsVo getGroupDetails(long id) throws BizException {

        GroupPo po = repository.findById(id).orElseThrow(() -> new BizException("用户组不存在"));

        //获取系统中的全部权限列表
        List<PermissionPo> allPermPos = permissionRepository.findAll();

        //获取该用户组拥有的权限IDS
        var groupPermIds = gpRepository.getPermissionIdsByGroupId(id);

        GetGroupDetailsVo vo = as(po, GetGroupDetailsVo.class);
        List<GroupPermissionDefinitionVo> defVos = new ArrayList<>();

        for (var permission : allPermPos) {

            var defVo = as(permission, GroupPermissionDefinitionVo.class);
            defVo.setHas(1);

            //如果该用户组拥有该权限 则设置为0
            if (groupPermIds.contains(permission.getId())) {
                defVo.setHas(0);
            }

            defVos.add(defVo);
        }

        vo.setPermissions(defVos);
        return vo;
    }

    /**
     * 添加用户组
     *
     * @param dto 添加用户组参数
     * @throws BizException 用户组标识已存在
     */
    @Transactional(rollbackFor = Exception.class)
    public void addGroup(AddGroupDto dto) throws BizException {
        if (repository.existsByCode(dto.getCode())) {
            throw new BizException("用户组标识已存在");
        }

        GroupPo group = new GroupPo();
        group.setId(IdWorker.nextId()); //用户组在新增时需要手动设置ID 因为用户组关联表需要用户组ID
        group.setCode(dto.getCode());
        group.setName(dto.getName());
        group.setDescription(dto.getDescription());
        group.setStatus(dto.getStatus());
        group.setSortOrder(dto.getSortOrder());

        //处理权限关系
        var permissionPos = permissionRepository.findAllById(dto.getPermissionIds());
        var gpPos = new ArrayList<GroupPermissionPo>();

        for (var permission : permissionPos) {
            var gpPo = new GroupPermissionPo();
            gpPo.setGroupId(group.getId());
            gpPo.setPermissionId(permission.getId());
            gpPos.add(gpPo);
        }

        //保存用户组
        repository.save(group);

        //保存用户组关联权限码关系
        if (!gpPos.isEmpty()) {
            gpRepository.saveAll(gpPos);
        }
    }

    /**
     * 编辑用户组
     *
     * @param dto 编辑用户组参数
     * @throws BizException 用户组不存在
     */
    @Transactional(rollbackFor = Exception.class)
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

        //处理权限关系 先清除该用户组下挂载的权限关系
        gpRepository.clearPermissionByGroupId(group.getId());
        var permissionPos = permissionRepository.findAllById(dto.getPermissionIds());
        var gpPos = new ArrayList<GroupPermissionPo>();

        for (var permission : permissionPos) {
            var gpPo = new GroupPermissionPo();
            gpPo.setGroupId(group.getId());
            gpPo.setPermissionId(permission.getId());
            gpPos.add(gpPo);
        }

        //保存用户组
        repository.save(group);

        //保存用户组关联权限码关系
        if (!gpPos.isEmpty()) {
            gpRepository.saveAll(gpPos);
        }

        //处理受影响的在线用户会话 先查询拥有该组的用户ID列表
        var userIds = ugRepository.getUserIdsByGroupId(group.getId());

        //获取当前在线的用户会话
        var onlineSessions = userSessionRepository.getSessionByUserIds(userIds);

        //更新在线用户会话
        for (UserSessionPo session : onlineSessions) {
            try {
                sessionService.updateSession(session.getUserId());
            } catch (Exception e) {
                log.error("更新用户会话失败:{}", e.getMessage());
            }

        }
    }


    /**
     * 获取用户组权限菜单视图
     *
     * @param dto 获取用户组权限菜单视图参数
     * @return 用户组权限菜单视图列表
     * @throws BizException 用户组不存在
     */
    public List<GetGroupPermissionMenuViewVo> getGroupPermissionMenuView(GetGroupPermissionMenuViewDto dto) throws BizException {

        GroupPo group = repository.findById(dto.getGroupId()).orElseThrow(() -> new BizException("用户组不存在"));
        if (group == null) {
            throw new BizException("用户组不存在");
        }

        //查找菜单树
        var menuPos = resourceRepository.getMenuTreeByKeyword(dto.getKeyword());

        List<GetGroupPermissionMenuViewVo> flatVos = new ArrayList<>();

        //将menuPos转换为flatVos
        for (ResourcePo po : menuPos) {
            GetGroupPermissionMenuViewVo vo = as(po, GetGroupPermissionMenuViewVo.class);
            vo.setChildren(new ArrayList<>());
            vo.setParentId(null);
            if (po.getParent() != null) {
                vo.setParentId(po.getParent().getId());
            }
            flatVos.add(vo);
        }

        //将平面vo转换为tree
        List<GetGroupPermissionMenuViewVo> treeVos = new ArrayList<>();
        Map<Long, GetGroupPermissionMenuViewVo> map = new HashMap<>();

        for (GetGroupPermissionMenuViewVo vo : flatVos) {
            map.put(vo.getId(), vo);
        }

        for (GetGroupPermissionMenuViewVo vo : flatVos) {
            if (vo.getParentId() == null) {
                treeVos.add(vo);
                continue;
            }

            GetGroupPermissionMenuViewVo parent = map.get(vo.getParentId());
            if (parent != null) {
                parent.getChildren().add(vo);
            } else {
                treeVos.add(vo);
            }
        }

        //搜集菜单中的权限列表
        var permissions = new HashSet<String>();
        for (ResourcePo menuPo : menuPos) {
            if (StringUtils.isNotBlank(menuPo.getPermission())) {
                permissions.addAll(Str.safeSplit(menuPo.getPermission(), ";"));
            }
        }

        //查找数据库中不存在的权限
        Set<String> existingPermissions = permissionRepository.getExistingPermissionsByCode(permissions);
        Set<String> missingPermissions = new HashSet<>(permissions);
        missingPermissions.removeAll(existingPermissions);

        // 设置缺失权限标记
        for (GetGroupPermissionMenuViewVo vo : flatVos) {
            if (Str.in(vo.getPermission(), "*")) {
                vo.setMissingPermission(0);
                continue;
            }

            if (StringUtils.isBlank(vo.getPermission())) {
                vo.setMissingPermission(0);
                continue;
            }

            List<String> perms = Str.safeSplit(vo.getPermission(), ";");
            int missingCount = 0;
            int totalCount = perms.size();

            for (String perm : perms) {
                if (missingPermissions.contains(perm)) {
                    missingCount++;
                }
            }

            if (missingCount == 0) {
                vo.setMissingPermission(0);
                continue;
            }

            if (missingCount == totalCount) {
                vo.setMissingPermission(1);
                continue;
            }

            vo.setMissingPermission(2);
        }

        //获取该组拥有的权限
        var groupPerms = permissionRepository.getPermissionsByGroupId(group.getId());

        //设置菜单当前用户是否有权限
        for (var vo : flatVos) {

            //获取菜单权限列表
            var menuPerms = vo.getPermissions();

            var total = menuPerms.size();
            var has = 0;

            for (var menuPerm : menuPerms) {
                for (var groupPerm : groupPerms) {
                    if (menuPerm.contains(groupPerm.getCode())) {
                        has++;
                    }
                }
            }

            //0:没有权限 1:有权限
            vo.setHasPermission(0);

            if (has >= total) {
                vo.setHasPermission(1);
            }
            //部分授权
            if (has > 0 && has < total) {
                vo.setHasPermission(2);
            }

        }

        //如果hasPermission不为空，则递归过滤出hasPermission为hasPermission的菜单
        if (dto.getHasPermission() != null) {
            treeVos = filterMenuTreeByHasPermission(treeVos, dto.getHasPermission());
        }

        return treeVos;
    }

    /**
     * 递归过滤菜单树，只保留hasPermission匹配的节点
     * 如果父节点不符合条件但子节点符合，子节点会被保留
     */
    private List<GetGroupPermissionMenuViewVo> filterMenuTreeByHasPermission(List<GetGroupPermissionMenuViewVo> treeVos, Integer hasPermission) {
        if (treeVos == null || treeVos.isEmpty()) {
            return new ArrayList<>();
        }

        List<GetGroupPermissionMenuViewVo> filtered = new ArrayList<>();

        for (GetGroupPermissionMenuViewVo vo : treeVos) {
            // 递归过滤子节点
            List<GetGroupPermissionMenuViewVo> filteredChildren = filterMenuTreeByHasPermission(vo.getChildren(), hasPermission);

            // 如果当前节点符合条件
            if (vo.getHasPermission() != null && vo.getHasPermission().equals(hasPermission)) {
                vo.setChildren(filteredChildren);
                filtered.add(vo);
                continue;
            }

            // 如果当前节点不符合条件，但子节点符合，保留子节点
            if (!filteredChildren.isEmpty()) {
                filtered.addAll(filteredChildren);
            }
        }

        return filtered;
    }

    /**
     * 获取组权限节点视图
     *
     * @param dto 获取组权限节点视图参数
     * @return 组权限节点视图列表
     * @throws BizException 业务异常
     */
    public PageResult<GetGroupPermissionNodeVo> getGroupPermissionNodeView(GetGroupPermissionNodeDto dto) throws BizException {

        GroupPo group = repository.findById(dto.getGroupId()).orElseThrow(() -> new BizException("用户组不存在"));
        if (group == null) {
            throw new BizException("用户组不存在");
        }

        //查找权限节点
        var pPos = permissionRepository.getPermissionsByKeywordAndGroup(dto.getKeyword(), group.getId(), dto.getHasPermission(), dto.pageRequest());
        List<GetGroupPermissionNodeVo> vos = as(pPos.getContent(), GetGroupPermissionNodeVo.class);

        var groupPerms = permissionRepository.getPermissionsByGroupId(group.getId());

        for (var vo : vos) {
            vo.setHasPermission(0);
            for (var groupPerm : groupPerms) {
                if (vo.getCode().equals(groupPerm.getCode())) {
                    vo.setHasPermission(1);
                    break;
                }
            }
        }

        return PageResult.success(vos, pPos.getTotalElements());
    }

    /**
     * 授权和取消授权
     *
     * @param dto 授权和取消授权参数
     * @throws BizException 用户组不存在
     */
    @Transactional(rollbackFor = Exception.class)
    public void grantAndRevoke(GrantAndRevokeDto dto) throws BizException {

        GroupPo group = repository.findById(dto.getGroupId()).orElseThrow(() -> new BizException("用户组不存在"));

        if (group == null) {
            throw new BizException("用户组不存在");
        }

        //加载全部权限列表
        var allPermPos = permissionRepository.getPermissionsByCodes(dto.getPermissionCodes());

        //获取当前组拥有的权限
        var groupPerms = permissionRepository.getPermissionsByGroupId(group.getId());

        //模式授权 0:授权 1:取消授权
        if (dto.getType() == 0) {
            groupPerms.addAll(allPermPos);
        }

        //取消授权
        if (dto.getType() == 1) {
            groupPerms.removeAll(allPermPos);
        }

        //保存更改
        repository.save(group);
    }


    /**
     * 移除用户组
     *
     * @param dto 移除用户组参数
     * @throws BizException 用户组不存在
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeGroup(CommonIdDto dto) throws BizException {

        var ids = dto.toIds();

        if (ids == null || ids.isEmpty()) {
            throw new BizException("用户组ID不能为空");
        }

        //查询存在的用户组记录
        var groups = repository.getGroupsByIds(ids);

        if (groups == null || groups.isEmpty()) {
            throw new BizException("一个或多个用户组不存在");
        }

        var safeRemoveIds = new ArrayList<Long>();
        String errorMessage = null;

        for (GroupPo group : groups) {

            //系统用户组无法删除
            if (group.getIsSystem()) {
                errorMessage = "系统用户组无法删除";
                continue;
            }

            //用户组下面还有用户也不能删除
            var userGroupIds = ugRepository.getGroupIdsByGrantedUserId(group.getId());

            if (!userGroupIds.isEmpty()) {
                errorMessage = String.format("该用户组下有 %d 个用户，请先取消所有关联关系后再尝试移除", userGroupIds.size());
                continue;
            }

            safeRemoveIds.add(group.getId());
        }

        //如果当前是单个删除模式且有错误 直接抛出异常
        if (!dto.isBatch() && errorMessage != null) {
            throw new BizException(errorMessage);
        }

        //当前是批量删除模式且没有任何一个用户组可以删除 则抛出异常
        if (dto.isBatch() && safeRemoveIds.isEmpty()) {
            throw new BizException("没有可以安全删除的用户组,请检查用户组状态或关联关系");
        }

        //执行静默删除
        repository.deleteAllById(safeRemoveIds);

        //还需要删除该组下挂载的权限关系


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

                    //查询管理员组
                    var adminGroup = repository.getGroupByCode(GroupEnum.ADMIN.getCode());
                    
                    //先清空管理员的全部GP关系
                    gpRepository.clearPermissionByGroupId(adminGroup.getId());

                    //查找全部权限IDS
                    var allPermissionIds = permissionRepository.getAllPermissionIds();

                    //创建GP关系列表
                    var gpPos = new ArrayList<GroupPermissionPo>();
                    for (var permissionId : allPermissionIds) {
                        var gpPo = new GroupPermissionPo();
                        gpPo.setGroupId(adminGroup.getId());
                        gpPo.setPermissionId(permissionId);
                        gpPos.add(gpPo);
                    }

                    //保存GP关系
                    gpRepository.saveAll(gpPos);
                }

                continue;
            }

            // 创建新的组
            GroupPo group = new GroupPo();
            group.setId(IdWorker.nextId()); //用户组在新增时需要手动设置ID 因为用户组关联表需要用户组ID
            group.setCode(code);
            group.setName(groupEnum.getName());
            group.setDescription(groupEnum.getName());
            group.setIsSystem(true);
            group.setSortOrder(repository.findMaxSortOrder() + 1);
            group.setStatus(1); // 启用状态

            // 如果是管理员组，赋予所有权限
            if (groupEnum == GroupEnum.ADMIN) {

                //直接查询所有权限IDS
                var allPermissionIds = permissionRepository.getAllPermissionIds();

                //创建GP关系列表
                var gpPos = new ArrayList<GroupPermissionPo>();
                for (var permissionId : allPermissionIds) {
                    var gpPo = new GroupPermissionPo();
                    gpPo.setGroupId(group.getId());
                    gpPo.setPermissionId(permissionId);
                    gpPos.add(gpPo);
                }

                //保存GP关系
                gpRepository.saveAll(gpPos);
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
