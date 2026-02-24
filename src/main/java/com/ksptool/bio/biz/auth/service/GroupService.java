package com.ksptool.bio.biz.auth.service;


import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.auth.model.GroupDeptPo;
import com.ksptool.bio.biz.auth.model.GroupPermissionPo;
import com.ksptool.bio.biz.auth.model.group.GroupPo;
import com.ksptool.bio.biz.auth.model.group.dto.*;
import com.ksptool.bio.biz.auth.model.group.vo.*;
import com.ksptool.bio.biz.auth.model.permission.PermissionPo;
import com.ksptool.bio.biz.auth.repository.*;
import com.ksptool.bio.biz.core.model.resource.ResourcePo;
import com.ksptool.bio.biz.core.repository.OrgRepository;
import com.ksptool.bio.biz.core.repository.ResourceRepository;
import com.ksptool.bio.commons.dataprocess.Str;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @Autowired
    private GroupDeptRepository gdRepository;

    @Autowired
    private OrgRepository orgRepository;


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

        //如果数据权限为5(指定部门)时，则需要获取部门列表
        if (po.getRowScope() == 5) {
            var deptIds = gdRepository.getDeptIdsByGroupId(id);
            vo.setDeptIds(deptIds);
        }

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
        group.setCode(dto.getCode());
        group.setName(dto.getName());
        group.setRemark(dto.getRemark());
        group.setStatus(dto.getStatus());
        group.setSeq(dto.getSeq());
        group.setRowScope(dto.getRowScope());
        group.setIsSystem(0);

        //保存用户组
        GroupPo save = repository.save(group);

        //处理权限关系
        var permissionPos = permissionRepository.findAllById(dto.getPermissionIds());
        var gpPos = new ArrayList<GroupPermissionPo>();

        for (var permission : permissionPos) {
            var gpPo = new GroupPermissionPo();
            gpPo.setGroupId(save.getId());
            gpPo.setPermissionId(permission.getId());
            gpPos.add(gpPo);
        }

        //保存用户组关联权限码关系
        if (!gpPos.isEmpty()) {
            gpRepository.saveAll(gpPos);
        }

        //处理部门关系 先清除该用户组下挂载的部门关系
        gdRepository.clearGroupDeptByGroupId(save.getId());

        //如果数据权限为5(指定部门)时，则需要处理部门关系
        if (dto.getRowScope() == 5) {

            var deptPos = orgRepository.getDeptsByIds(dto.getDeptIds());
            var gdPos = new ArrayList<GroupDeptPo>();
            var rootId = 0L;

            if (deptPos.size() != dto.getDeptIds().size()) {
                throw new BizException("至少有一个部门不存在!");
            }

            //获取第一个部门的租户ID
            rootId = deptPos.get(0).getRootId();

            //组装GD关系
            for (var dept : deptPos) {

                if (dept.getRootId() != rootId) {
                    throw new BizException("部门[" + dept.getName() + "]不属于同一租户!");
                }

                //组装GD关系
                var gdPo = new GroupDeptPo();
                gdPo.setGroupId(save.getId());
                gdPo.setDeptId(dept.getId());
                gdPos.add(gdPo);
            }

            //保存GD关系
            if (!gdPos.isEmpty()) {
                gdRepository.saveAll(gdPos);
            }

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
        group.setRemark(dto.getRemark());
        group.setStatus(dto.getStatus());
        group.setSeq(dto.getSeq());
        group.setRowScope(dto.getRowScope());
        group.setIsSystem(0);

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

        //处理部门关系 先清除该用户组下挂载的部门关系
        gdRepository.clearGroupDeptByGroupId(group.getId());

        //如果数据权限为5(指定部门)时，则需要处理部门关系
        if (dto.getRowScope() == 5) {

            var deptPos = orgRepository.getDeptsByIds(dto.getDeptIds());
            var gdPos = new ArrayList<GroupDeptPo>();
            var rootId = 0L;

            if (deptPos.size() != dto.getDeptIds().size()) {
                throw new BizException("至少有一个部门不存在!");
            }

            //获取第一个部门的租户ID
            rootId = deptPos.get(0).getRootId();

            //组装GD关系
            for (var dept : deptPos) {

                if (dept.getRootId() != rootId) {
                    throw new BizException("部门[" + dept.getName() + "]不属于同一租户!");
                }

                //组装GD关系
                var gdPo = new GroupDeptPo();
                gdPo.setGroupId(group.getId());
                gdPo.setDeptId(dept.getId());
                gdPos.add(gdPo);
            }

            //保存GD关系
            if (!gdPos.isEmpty()) {
                gdRepository.saveAll(gdPos);
            }

        }

        //处理受影响的在线用户会话 先查询拥有该组的用户ID列表
        var userIds = ugRepository.getUserIdsByGroupId(group.getId());

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

        //获取当前组拥有的权限
        var groupPerms = permissionRepository.getPermissionsByGroupId(group.getId());

        //获取要操作的权限
        var permPos = permissionRepository.getPermissionsByCodes(dto.getPermissionCodes());

        //清空该组下挂载的权限关系
        gpRepository.clearPermissionByGroupId(group.getId());

        //最终的GP权限关系
        var gpPos = new ArrayList<GroupPermissionPo>();

        //模式授权 0:授权 1:取消授权
        if (dto.getType() == 0) {

            //授权 = 当前组拥有的权限 + 要操作的权限
            var mergePerms = new HashSet<>(groupPerms);
            mergePerms.addAll(permPos);

            for (var perm : mergePerms) {
                var gpPo = new GroupPermissionPo();
                gpPo.setGroupId(group.getId());
                gpPo.setPermissionId(perm.getId());
                gpPos.add(gpPo);
            }

        }

        //取消授权
        if (dto.getType() == 1) {

            //取消授权 = 当前组拥有的权限 - 要操作的权限
            var mergePerms = new HashSet<PermissionPo>(groupPerms);

            for (var perm : permPos) {
                mergePerms.remove(perm);
            }

            for (var perm : mergePerms) {
                var gpPo = new GroupPermissionPo();
                gpPo.setGroupId(group.getId());
                gpPo.setPermissionId(perm.getId());
                gpPos.add(gpPo);
            }

        }

        //保存更改
        if (!gpPos.isEmpty()) {
            gpRepository.saveAll(gpPos);
        }
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
            if (group.getIsSystem() != null && group.getIsSystem() == 1) {
                errorMessage = "系统用户组无法删除";
                continue;
            }

            //用户组下面还有用户也不能删除
            var userCount = ugRepository.countUserByGroupId(group.getId());

            if (userCount > 0) {
                errorMessage = String.format("该用户组下有 %d 个用户，请先取消所有关联关系后再尝试移除", userCount);
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

        //删除该组下挂载的权限关系
        gpRepository.clearPermissionByGroupIds(safeRemoveIds);


        //执行静默删除
        repository.deleteAllById(safeRemoveIds);
    }


}
