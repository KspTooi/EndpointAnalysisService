package com.ksptooi.biz.core.service;

import com.ksptooi.biz.auth.model.GroupPermissionPo;
import com.ksptooi.biz.auth.model.group.GroupPo;
import com.ksptooi.biz.auth.model.permission.PermissionPo;
import com.ksptooi.biz.auth.repository.GroupPermissionRepository;
import com.ksptooi.biz.auth.repository.GroupRepository;
import com.ksptooi.biz.auth.repository.PermissionRepository;
import com.ksptooi.biz.core.model.maintain.vo.MaintainUpdateVo;
import com.ksptooi.commons.enums.GroupEnum;
import com.ksptool.assembly.entity.exception.BizException;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 维护中心服务
 * 提供维护中心所需的各项服务
 */
@Slf4j
@Service
public class MaintainService {

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping rmhm;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupPermissionRepository gpRepository;

    @Autowired
    private PermissionRepository permissionRepository;


    /**
     * 校验系统内置权限节点
     * 检查数据库中是否存在所有系统内置权限码,如果缺失则自动创建
     *
     * @return 校验结果
     */
    @Transactional(rollbackFor = Exception.class)
    public MaintainUpdateVo validatePermissions() {

        //扫描搜集系统中已定义的全部权限码
        Set<PermissionPo> scannedPermissions = new HashSet<>();

        var handlerMethods = rmhm.getHandlerMethods();

        for (var entry : handlerMethods.entrySet()) {

            var info = entry.getKey();
            var method = entry.getValue();

            //提取Swagger注解
            var name = "未命名接口";
            var remark = "";

            if (method.hasMethodAnnotation(Operation.class)) {
                var operation = method.getMethodAnnotation(Operation.class);
                if (operation != null) {
                    name = operation.summary();
                    remark = operation.description();
                }
            }

            //提取SpringSecurity注解上的权限码
            var permissionCode = "?";
            if (method.hasMethodAnnotation(PreAuthorize.class)) {

                var preAuthorize = method.getMethodAnnotation(PreAuthorize.class);

                if (preAuthorize != null) {

                    var val = preAuthorize.value().trim().replace(" ", "");

                    if (val.startsWith("@auth.hasCode('")) {
                        val = val.replace("@auth.hasCode('", "");
                    }

                    if (val.endsWith("')")) {
                        val = val.substring(0, val.length() - 2);
                    }

                    permissionCode = val.toLowerCase();
                }

            }

            //如果权限码提取失败则不进行任何动作
            if (permissionCode.equals("?")) {
                log.warn("接口:{},方法:{} 未找到权限码，跳过处理", info.getDirectPaths(), method.getMethod().getName());
                continue;
            }

            //构建一个权限码的PO
            var po = new PermissionPo();
            po.setCode(permissionCode);
            po.setName(name);
            po.setDescription(remark);
            po.setSortOrder(0);
            po.setIsSystem(1);
            scannedPermissions.add(po);
        }

        //添加超级权限
        var superCode = new PermissionPo();
        superCode.setCode("*:*:*");
        superCode.setName("超级权限");
        superCode.setDescription("拥有此权限的用户组不受任何权限限制");
        superCode.setSortOrder(0);
        superCode.setIsSystem(1);
        scannedPermissions.add(superCode);

        //扫描数据库中已定义的全部权限码(这不包含那些用户自己定义的权限码 只获取系统权限码)
        Set<PermissionPo> existingPermissions = permissionRepository.getAllSystemPermissions();

        //需要新增的权限码
        var addedPermissions = new HashSet<PermissionPo>();

        //需要移除的权限码
        var removedPermissions = new HashSet<PermissionPo>();

        //遍历远程权限码
        for (var existingPermission : existingPermissions) {

            //远程有 本地无 需删除远程多余
            if (!scannedPermissions.contains(existingPermission)) {
                removedPermissions.add(existingPermission);
            }

        }

        //遍历本地权限码
        for (var scannedPermission : scannedPermissions) {

            //本地有 远程无 需补充远程
            if (!existingPermissions.contains(scannedPermission)) {
                addedPermissions.add(scannedPermission);
            }

        }

        //执行变更 移除数据库中多余的权限码
        if (!removedPermissions.isEmpty()) {

            //需要先移除这些权限码的全部关联关系
            var permissionIds = removedPermissions.stream().map(PermissionPo::getId).collect(Collectors.toList());
            gpRepository.clearGpByPermissionIds(permissionIds);

            //然后删除这些权限码
            permissionRepository.deleteAllInBatch(removedPermissions);
        }


        //执行变更操作 新增权限码
        if (!addedPermissions.isEmpty()) {
            permissionRepository.saveAll(addedPermissions);
        }

        //构建响应Vo
        var vo = new MaintainUpdateVo();
        vo.setAddedCount(addedPermissions.size());
        vo.setRemovedCount(removedPermissions.size());
        vo.setAddedList(addedPermissions.stream().map(PermissionPo::getCode).collect(Collectors.toList()));
        vo.setRemovedList(removedPermissions.stream().map(PermissionPo::getCode).collect(Collectors.toList()));
        return vo;
    }

    /**
     * 校验系统内置组
     * 检查数据库中是否存在所有系统内置组，如果不存在则自动创建
     * 对于管理员组，会赋予所有现有权限
     *
     * @return 校验结果消息
     */
    @Transactional(rollbackFor = Exception.class)
    public MaintainUpdateVo validateSystemGroups() throws BizException {

        //获取超级权限
        var superPermission = permissionRepository.getSuperPermission();

        if (superPermission == null) {
            throw new BizException("在校验系统内置组时出现问题，超级权限不存在,请检查系统内置权限码是否完整!");
        }

        // 获取所有系统内置组枚举
        GroupEnum[] groupEnums = GroupEnum.values();

        // 记录已存在和新增的组数量
        int existCount = groupRepository.countBySystemGroup();
        List<String> addedGroups = new ArrayList<>();

        // 遍历所有系统内置组
        for (GroupEnum groupEnum : groupEnums) {
            String code = groupEnum.getCode();

            //先查找数据库组
            var group = groupRepository.getGroupByCode(code);

            //数据库组不存在 直接创建
            if(group == null){
                group = new GroupPo();
                group.setCode(code);
                group.setName(groupEnum.getName());
                group.setDescription(groupEnum.getName());
                group.setIsSystem(true);
                group.setSortOrder(groupRepository.findMaxSortOrder() + 1);
                group.setStatus(1);
                group = groupRepository.save(group);
            }

            //检查组是否是管理员组
            if(group.getCode().equals(GroupEnum.ADMIN.getCode())){
                //管理员组赋予超级权限
                var gpPo = new GroupPermissionPo();
                gpPo.setGroupId(group.getId());
                gpPo.setPermissionId(superPermission.getId());
                gpRepository.save(gpPo);
            }

            addedGroups.add(code);
        }

        // 返回结果消息
        var vo = new MaintainUpdateVo();
        vo.setExistCount(existCount);
        vo.setAddedCount(addedGroups.size());
        vo.setAddedList(addedGroups);
        vo.setRemovedCount(0);
        vo.setRemovedList(new ArrayList<>());
        return vo;
    }

}
