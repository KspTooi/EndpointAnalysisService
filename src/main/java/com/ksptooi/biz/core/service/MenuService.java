package com.ksptooi.biz.core.service;

import com.ksptooi.biz.auth.repository.PermissionRepository;
import com.ksptooi.biz.auth.service.SessionService;
import com.ksptooi.biz.core.model.menu.dto.AddMenuDto;
import com.ksptooi.biz.core.model.menu.dto.EditMenuDto;
import com.ksptooi.biz.core.model.menu.dto.GetMenuTreeDto;
import com.ksptooi.biz.core.model.menu.vo.GetMenuDetailsVo;
import com.ksptooi.biz.core.model.menu.vo.GetMenuTreeVo;
import com.ksptooi.biz.core.model.menu.vo.GetUserMenuTreeVo;
import com.ksptooi.biz.core.model.resource.ResourcePo;
import com.ksptooi.biz.core.repository.ResourceRepository;
import com.ksptooi.commons.dataprocess.Str;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class MenuService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * 获取用户菜单与按钮树(该函数带有缓存)
     *
     * @param uid 用户ID 用于缓存键
     * @return 用户菜单与按钮树
     * @throws BizException 业务异常
     */
    @Cacheable(cacheNames = "menuTree", key = "'userMenuTree:' + #uid")
    public List<GetUserMenuTreeVo> getUserMenuTree(Long uid) throws BizException, AuthException {

        var allMenuPos = resourceRepository.getUserMenuTree();
        var flatVos = new ArrayList<GetUserMenuTreeVo>();

        var authorities = SessionService.authorities();

        //将list转换为平面vo
        for (ResourcePo po : allMenuPos) {

            //在此过滤当前用户无权限访问的菜单
            if (!po.hasPermission(authorities)) {
                continue;
            }

            var vo = as(po, GetUserMenuTreeVo.class);
            vo.setChildren(new ArrayList<>());
            vo.setParentId(null);
            if (po.getParent() != null) {
                vo.setParentId(po.getParent().getId());
            }
            flatVos.add(vo);
        }

        //将平面vo转换为tree
        List<GetUserMenuTreeVo> treeVos = new ArrayList<>();
        Map<Long, GetUserMenuTreeVo> map = new HashMap<>();

        for (GetUserMenuTreeVo vo : flatVos) {
            map.put(vo.getId(), vo);
        }

        for (GetUserMenuTreeVo vo : flatVos) {
            if (vo.getParentId() == null) {
                treeVos.add(vo);
                continue;
            }

            GetUserMenuTreeVo parent = map.get(vo.getParentId());
            if (parent != null) {
                parent.getChildren().add(vo);
                continue;
            }
            treeVos.add(vo);
        }

        return treeVos;
    }


    /**
     * 获取菜单与按钮树
     *
     * @param dto 获取菜单与按钮树参数
     * @return 菜单与按钮树
     * @throws BizException 业务异常
     */
    public List<GetMenuTreeVo> getMenuTree(GetMenuTreeDto dto) throws BizException {

        List<ResourcePo> list = resourceRepository.getMenuTree(dto);

        List<GetMenuTreeVo> flatVos = new ArrayList<>();

        //将list转换为平面vo
        for (ResourcePo po : list) {
            GetMenuTreeVo vo = as(po, GetMenuTreeVo.class);
            vo.setChildren(new ArrayList<>());
            vo.setParentId(null);
            if (po.getParent() != null) {
                vo.setParentId(po.getParent().getId());
            }

            flatVos.add(vo);
        }

        //将平面vo转换为tree
        List<GetMenuTreeVo> treeVos = new ArrayList<>();
        Map<Long, GetMenuTreeVo> map = new HashMap<>();

        for (GetMenuTreeVo vo : flatVos) {
            map.put(vo.getId(), vo);
        }

        for (GetMenuTreeVo vo : flatVos) {
            if (vo.getParentId() == null) {
                treeVos.add(vo);
                continue;
            }

            GetMenuTreeVo parent = map.get(vo.getParentId());
            if (parent != null) {
                parent.getChildren().add(vo);
            } else {
                treeVos.add(vo);
            }
        }

        //搜集菜单中的权限列表
        var permissions = new HashSet<String>();
        for (ResourcePo menuPo : list) {
            if (StringUtils.isNotBlank(menuPo.getPermission())) {
                permissions.addAll(Str.safeSplit(menuPo.getPermission(), ";"));
            }
        }

        //查找数据库中不存在的权限
        Set<String> existingPermissions = permissionRepository.getExistingPermissionsByCode(permissions);
        Set<String> missingPermissions = new HashSet<>(permissions);
        missingPermissions.removeAll(existingPermissions);

        // 设置缺失权限标记
        for (GetMenuTreeVo vo : flatVos) {
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

        return treeVos;
    }

    /**
     * 新增菜单与按钮(这个接口会全量清除所有用户菜单缓存)
     *
     * @param dto 新增菜单与按钮参数
     * @throws BizException 业务异常
     */
    @CacheEvict(cacheNames = "menuTree", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void addMenu(AddMenuDto dto) throws BizException {

        //构建资源PO
        ResourcePo resourcePo = as(dto, ResourcePo.class);

        //如果父级id不为空，查询父级资源
        if (dto.getParentId() != null) {
            ResourcePo parent = resourceRepository.findById(dto.getParentId()).orElseThrow(() -> new BizException("新增失败,父级资源不存在."));

            //校验父级资源类型
            if (parent.getKind() != 0) {
                throw new BizException("新增失败,父级资源不能是接口.");
            }

            //校验父级菜单可达性 0:目录 1:菜单 2:按钮
            if (dto.getMenuKind() == 0) {

                //目录无法放置于菜单与按钮之下
                if (parent.getMenuKind() != 0) {
                    throw new BizException("新增失败,目录无法放置于菜单与按钮之下.");
                }

            }

            //1:菜单
            if (dto.getMenuKind() == 1) {
                //菜单不能放置于按钮之下
                if (parent.getMenuKind() == 2) {
                    throw new BizException("新增失败,菜单不能放置于按钮之下.");
                }
            }

            //2:按钮
            if (dto.getMenuKind() == 2) {
                //按钮必须放置于菜单之下
                if (parent.getMenuKind() != 1) {
                    throw new BizException("新增失败,按钮必须放置于菜单之下.");
                }
            }


            resourcePo.setParent(parent);
        }

        resourcePo.setKind(0); //资源类型 0:菜单 1:接口
        resourceRepository.save(resourcePo);
    }

    /**
     * 编辑菜单与按钮(这个接口会全量清除所有用户菜单缓存)
     *
     * @param dto 编辑菜单与按钮参数
     * @throws BizException 业务异常
     */
    @CacheEvict(cacheNames = "menuTree", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void editMenu(EditMenuDto dto) throws BizException {

        ResourcePo resourcePo = resourceRepository.findById(dto.getId()).orElseThrow(() -> new BizException("编辑失败,数据不存在或无权限访问."));
        assign(dto, resourcePo);

        //如果父级id不为空，查询父级资源
        if (dto.getParentId() != null) {
            ResourcePo parent = resourceRepository.findById(dto.getParentId()).orElseThrow(() -> new BizException("新增失败,父级资源不存在."));

            //校验父级资源类型
            if (parent.getKind() != 0) {
                throw new BizException("新增失败,父级资源不能是接口.");
            }

            //校验父级菜单可达性 0:目录 1:菜单 2:按钮
            if (dto.getMenuKind() == 0) {

                //目录无法放置于菜单与按钮之下
                if (parent.getMenuKind() != 0) {
                    throw new BizException("新增失败,目录无法放置于菜单与按钮之下.");
                }

            }

            //1:菜单
            if (dto.getMenuKind() == 1) {
                //菜单不能放置于按钮之下
                if (parent.getMenuKind() == 2) {
                    throw new BizException("新增失败,菜单不能放置于按钮之下.");
                }
            }

            //2:按钮
            if (dto.getMenuKind() == 2) {
                //按钮必须放置于菜单之下
                if (parent.getMenuKind() != 1) {
                    throw new BizException("新增失败,按钮必须放置于菜单之下.");
                }
            }

            //校验父级资源不能是自身
            if (parent.getId().equals(resourcePo.getId())) {
                throw new BizException("编辑失败,父级资源不能是自身.");
            }

            resourcePo.setParent(parent);
        }

        resourcePo.setKind(0); //资源类型 0:菜单 1:接口
        resourceRepository.save(resourcePo);
    }

    /**
     * 获取菜单与按钮详情
     *
     * @param dto 获取菜单与按钮详情参数
     * @return 菜单与按钮详情
     * @throws BizException 业务异常
     */
    public GetMenuDetailsVo getMenuDetails(CommonIdDto dto) throws BizException {

        ResourcePo resourcePo = resourceRepository.findById(dto.getId()).orElseThrow(() -> new BizException("获取详情失败,数据不存在或无权限访问."));
        GetMenuDetailsVo vo = as(resourcePo, GetMenuDetailsVo.class);
        if (resourcePo.getParent() != null) {
            vo.setParentId(resourcePo.getParent().getId());
        }
        return vo;
    }

    /**
     * 删除菜单与按钮(这个接口会全量清除所有用户菜单缓存)
     *
     * @param dto 删除菜单与按钮参数
     * @throws BizException 业务异常
     */
    @CacheEvict(cacheNames = "menuTree", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeMenu(CommonIdDto dto) throws BizException {

        //删除单个菜单与按钮
        if (!dto.isBatch()) {
            ResourcePo po = resourceRepository.findById(dto.getId()).orElseThrow(() -> new BizException("删除失败,数据不存在."));

            if (resourceRepository.getMenuChildrenCount(po.getId()) > 0) {
                throw new BizException("无法移除: " + po.getName() + " ,请先移除子项.");
            }

            resourceRepository.deleteById(po.getId());
            return;
        }

        //如果删除多个菜单与按钮，则需要判断是否有子资源
        for (Long id : dto.getIds()) {
            ResourcePo po = resourceRepository.findById(id).orElseThrow(() -> new BizException("删除失败,数据不存在."));

            if (resourceRepository.getMenuChildrenCount(id) > 0) {
                throw new BizException("无法移除: " + po.getName() + " ,请先移除子项.");
            }

            resourceRepository.deleteById(id);
        }

    }


}

