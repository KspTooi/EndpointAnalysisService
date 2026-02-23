package com.ksptool.bio.biz.core.service;

import com.ksptooi.biz.auth.repository.PermissionRepository;
import com.ksptool.bio.biz.core.model.endpoint.DynamicEndpointAuthorizationRule;
import com.ksptool.bio.biz.core.model.endpoint.dto.AddEndpointDto;
import com.ksptool.bio.biz.core.model.endpoint.dto.EditEndpointDto;
import com.ksptool.bio.biz.core.model.endpoint.dto.GetEndpointTreeDto;
import com.ksptool.bio.biz.core.model.endpoint.vo.GetEndpointDetailsVo;
import com.ksptool.bio.biz.core.model.endpoint.vo.GetEndpointTreeVo;
import com.ksptool.bio.biz.core.model.resource.ResourcePo;
import com.ksptool.bio.biz.core.repository.ResourceRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.bio.commons.dataprocess.Str;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class EndpointService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private PermissionRepository permissionRepository;


    /**
     * 添加端点
     *
     * @param dto 添加参数
     * @throws BizException 端点名称已存在
     */
    @Transactional(rollbackFor = Exception.class)
    public void addEndpoint(AddEndpointDto dto) throws BizException {
        ResourcePo po = as(dto, ResourcePo.class);
        po.setKind(1);

        if (dto.getParentId() != null && dto.getParentId() != -1) {
            ResourcePo parent = resourceRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new BizException("新增失败,父级端点不存在"));

            if (parent.getKind() != 1) {
                throw new BizException("新增失败,父级资源类型必须为端点");
            }

            if (parent.getId().equals(po.getId())) {
                throw new BizException("新增失败,父级端点不能是自身");
            }

            po.setParent(parent);
        }

        if (dto.getSeq() == null) {
            po.setSeq(0);
        }

        if (StringUtils.isBlank(po.getPermission())) {
            po.setPermission("*");
        }

        resourceRepository.save(po);
    }

    /**
     * 编辑端点
     *
     * @param dto 编辑参数
     * @throws BizException 端点不存在或无权限访问
     */
    @Transactional(rollbackFor = Exception.class)
    public void editEndpoint(EditEndpointDto dto) throws BizException {
        ResourcePo po = resourceRepository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,端点不存在或无权限访问"));

        assign(dto, po);

        if (dto.getParentId() != null && dto.getParentId() != -1) {
            ResourcePo parent = resourceRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new BizException("更新失败,父级端点不存在"));

            if (parent.getKind() != 1) {
                throw new BizException("更新失败,父级资源类型必须为端点");
            }

            if (parent.getId().equals(po.getId())) {
                throw new BizException("更新失败,父级端点不能是自身");
            }

            po.setParent(parent);
        }

        if (dto.getParentId() == null || dto.getParentId() == -1) {
            po.setParent(null);
        }

        if (po.getKind() != 1) {
            throw new BizException("更新失败,资源类型必须为端点");
        }

        if (dto.getSeq() == null) {
            po.setSeq(0);
        }

        if (StringUtils.isBlank(po.getPermission())) {
            po.setPermission("*");
        }

        resourceRepository.save(po);
    }

    /**
     * 查询端点详情
     *
     * @param id 端点ID
     * @return 端点详情
     * @throws BizException 端点不存在或无权限访问
     */
    public GetEndpointDetailsVo getEndpointDetails(Long id) throws BizException {
        ResourcePo po = resourceRepository.findById(id).orElseThrow(() -> new BizException("端点不存在或无权限访问"));

        if (po.getKind() != 1) {
            throw new BizException("查询失败,资源类型必须为端点");
        }

        GetEndpointDetailsVo vo = as(po, GetEndpointDetailsVo.class);
        if (po.getParent() != null) {
            vo.setParentId(po.getParent().getId());
        }
        return vo;
    }

    /**
     * 删除端点
     *
     * @param dto 删除参数
     * @throws BizException 端点不存在或无权限访问
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeEndpoint(CommonIdDto dto) throws BizException {
        if (!dto.isBatch()) {
            ResourcePo po = resourceRepository.findById(dto.getId())
                    .orElseThrow(() -> new BizException("删除失败,端点不存在或无权限访问"));

            if (po.getKind() != 1) {
                throw new BizException("删除失败,资源类型必须为端点");
            }

            if (resourceRepository.getMenuChildrenCount(po.getId()) > 0) {
                throw new BizException("删除失败,该端点下有子端点,不能删除");
            }

            resourceRepository.deleteById(po.getId());
            return;
        }

        for (Long id : dto.getIds()) {
            ResourcePo po = resourceRepository.findById(id)
                    .orElseThrow(() -> new BizException("删除失败,端点不存在或无权限访问"));

            if (po.getKind() != 1) {
                throw new BizException("删除失败,资源类型必须为端点");
            }

            if (resourceRepository.getMenuChildrenCount(id) > 0) {
                throw new BizException("无法移除: " + po.getName() + " ,请先移除子端点");
            }

            resourceRepository.deleteById(id);
        }
    }

    /**
     * 获取端点树
     *
     * @param dto 查询参数
     * @return 端点树
     */
    public List<GetEndpointTreeVo> getEndpointTree(GetEndpointTreeDto dto) {

        // 查询所有端点类型的资源
        List<ResourcePo> allEndpoints = resourceRepository.findByKind(1);
        if (allEndpoints.isEmpty()) {
            return new ArrayList<>();
        }

        // 如果有查询条件，进行过滤
        if (dto != null) {
            if (StringUtils.isNotBlank(dto.getName())) {
                allEndpoints = allEndpoints.stream()
                        .filter(po -> po.getName() != null && po.getName().contains(dto.getName()))
                        .collect(Collectors.toList());
            }

            if (StringUtils.isNotBlank(dto.getPath())) {
                allEndpoints = allEndpoints.stream()
                        .filter(po -> po.getPath() != null && po.getPath().contains(dto.getPath()))
                        .collect(Collectors.toList());
            }
        }

        if (allEndpoints.isEmpty()) {
            return new ArrayList<>();
        }


        // 转换为VO
        List<GetEndpointTreeVo> allVos = as(allEndpoints, GetEndpointTreeVo.class);


        // 设置parentId
        for (int i = 0; i < allEndpoints.size(); i++) {
            ResourcePo po = allEndpoints.get(i);
            if (po.getParent() != null) {
                allVos.get(i).setParentId(po.getParent().getId());
            }
        }

        //搜集端点中的权限列表
        var permissions = new HashSet<String>();

        for (ResourcePo endpoint : allEndpoints) {
            if (StringUtils.isNotBlank(endpoint.getPermission())) {
                permissions.addAll(Str.safeSplit(endpoint.getPermission(), ";"));
            }
        }

        //查找数据库中不存在的权限
        Set<String> existingPermissions = permissionRepository.getExistingPermissionsByCode(permissions);
        Set<String> missingPermissions = new HashSet<>(permissions);
        missingPermissions.removeAll(existingPermissions);

        // 设置缺失权限标记
        for (var vo : allVos) {

            if (Str.in(vo.getPermission(), "*")) {
                vo.setMissingPermission(0);
                continue;
            }

            // 找到对应的ResourcePo
            ResourcePo po = allEndpoints.stream()
                    .filter(endpoint -> endpoint.getId().equals(vo.getId()))
                    .findFirst()
                    .orElse(null);

            if (po == null || StringUtils.isBlank(po.getPermission())) {
                vo.setMissingPermission(0);
                continue;
            }

            List<String> perms = Str.safeSplit(po.getPermission(), ";");
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

        // 按parentId分组
        Map<Long, List<GetEndpointTreeVo>> parentIdMap = allVos.stream()
                .filter(vo -> vo.getParentId() != null)
                .collect(Collectors.groupingBy(GetEndpointTreeVo::getParentId));

        // 找出根节点
        List<GetEndpointTreeVo> roots = allVos.stream()
                .filter(vo -> vo.getParentId() == null)
                .collect(Collectors.toList());

        // 递归构建树
        for (GetEndpointTreeVo vo : roots) {
            buildChildren(vo, parentIdMap);
        }

        return roots;
    }

    /**
     * 递归构建子节点
     *
     * @param parent      父节点
     * @param parentIdMap 按parentId分组的节点
     */
    private void buildChildren(GetEndpointTreeVo parent, Map<Long, List<GetEndpointTreeVo>> parentIdMap) {
        List<GetEndpointTreeVo> children = parentIdMap.get(parent.getId());
        if (children == null || children.isEmpty()) {
            return;
        }

        parent.setChildren(children);
        for (GetEndpointTreeVo child : children) {
            buildChildren(child, parentIdMap);
        }
    }

    /**
     * 获取全量端点权限配置数据 这是一个带有缓存的查询方法 缓存配置位于com.ksptooi.commons.config.CacheConfig
     *
     * @return 端点权限配置数据
     */
    @Cacheable(cacheNames = "endpoint", key = "'all_endpoints'")
    public List<DynamicEndpointAuthorizationRule> getEndpointPermissionConfig() {

        //直接全量查询接口PO
        var pos = resourceRepository.findByKind(1);

        //构建VO
        var rules = new ArrayList<DynamicEndpointAuthorizationRule>();

        for (ResourcePo po : pos) {
            var rule = new DynamicEndpointAuthorizationRule();
            rule.setPathPattern(po.getPath());
            rule.setPermissionCodes(po.getPermission());
            rules.add(rule);
        }

        return rules;
    }

    /**
     * 清空端点缓存
     */
    @CacheEvict(cacheNames = "endpoint", key = "'all_endpoints'")
    public void clearEndpointCache() {

    }

}

