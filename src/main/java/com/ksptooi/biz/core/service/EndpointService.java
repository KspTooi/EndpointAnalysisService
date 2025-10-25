package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.endpoint.dto.AddEndpointDto;
import com.ksptooi.biz.core.model.endpoint.dto.EditEndpointDto;
import com.ksptooi.biz.core.model.endpoint.dto.GetEndpointTreeDto;
import com.ksptooi.biz.core.model.endpoint.vo.GetEndpointDetailsVo;
import com.ksptooi.biz.core.model.endpoint.vo.GetEndpointTreeVo;
import com.ksptooi.biz.core.model.resource.po.ResourcePo;
import com.ksptooi.biz.core.repository.ResourceRepository;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class EndpointService {

    @Autowired
    private ResourceRepository resourceRepository;

    private List<ResourcePo> endpointCache = null;


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

        // 设置缓存状态
        if (this.endpointCache != null) {
            Set<Long> cachedIds = this.endpointCache.stream().map(ResourcePo::getId).collect(Collectors.toSet());
            allVos.forEach(vo -> vo.setCached(cachedIds.contains(vo.getId()) ? 1 : 0));
        } else {
            allVos.forEach(vo -> vo.setCached(0));
        }

        // 设置parentId
        for (int i = 0; i < allEndpoints.size(); i++) {
            ResourcePo po = allEndpoints.get(i);
            if (po.getParent() != null) {
                allVos.get(i).setParentId(po.getParent().getId());
            }
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
     * @param parent 父节点
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
     * 获取端点所需权限
     *
     * @param urlPath 请求路径
     * @return 所需权限
     */
    public String getEndpointRequiredPermission(String urlPath) {

        if (StringUtils.isBlank(urlPath)) {
            return null;
        }

        if (this.endpointCache == null) {
            this.endpointCache = resourceRepository.findByKind(1);
        }

        List<ResourcePo> endpoints = this.endpointCache;

        if (endpoints.isEmpty()) {
            return null;
        }

        PathMatcher pathMatcher = new AntPathMatcher();

        List<ResourcePo> matchedEndpoints = new ArrayList<>();

        for (ResourcePo endpoint : endpoints) {
            if (StringUtils.isNotBlank(endpoint.getPath()) && pathMatcher.match(endpoint.getPath(), urlPath)) {
                matchedEndpoints.add(endpoint);
            }
        }

        if (matchedEndpoints.isEmpty()) {
            return null;
        }

        if (matchedEndpoints.size() == 1) {
            return matchedEndpoints.get(0).getPermission();
        }

        Comparator<ResourcePo> comparator = (e1, e2) -> pathMatcher.getPatternComparator(urlPath).compare(e1.getPath(), e2.getPath());
        Optional<ResourcePo> bestMatch = matchedEndpoints.stream().min(comparator);

        return bestMatch.map(ResourcePo::getPermission).orElse(null);
    }


    /**
     * 清空端点缓存
     */
    public void clearEndpointCache() {
        this.endpointCache = null;
    }


}
