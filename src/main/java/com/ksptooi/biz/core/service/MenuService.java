package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.menu.dto.AddMenuDto;
import com.ksptooi.biz.core.model.menu.dto.EditMenuDto;
import com.ksptooi.biz.core.model.menu.dto.GetMenuTreeDto;
import com.ksptooi.biz.core.model.menu.vo.GetMenuDetailsVo;
import com.ksptooi.biz.core.model.menu.vo.GetMenuTreeVo;
import com.ksptooi.biz.core.model.resource.po.ResourcePo;
import com.ksptooi.biz.core.repository.PermissionRepository;
import com.ksptooi.biz.core.repository.ResourceRepository;
import com.ksptooi.commons.dataprocess.Str;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            }else{
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
     * 新增菜单与按钮
     *
     * @param dto 新增菜单与按钮参数
     * @throws BizException 业务异常
     */
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
     * 编辑菜单与按钮
     *
     * @param dto 编辑菜单与按钮参数
     * @throws BizException 业务异常
     */
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
     * 删除菜单与按钮
     *
     * @param dto 删除菜单与按钮参数
     * @throws BizException 业务异常
     */
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

