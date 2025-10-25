package com.ksptooi.biz.core.service;


import com.ksptooi.biz.core.model.resource.dto.AddResourceDto;
import com.ksptooi.biz.core.model.resource.dto.EditResourceDto;
import com.ksptooi.biz.core.model.resource.dto.GetResourceListDto;
import com.ksptooi.biz.core.model.resource.po.ResourcePo;
import com.ksptooi.biz.core.model.resource.vo.GetResourceDetailsVo;
import com.ksptooi.biz.core.model.resource.vo.GetResourceListVo;
import com.ksptooi.biz.core.repository.ResourceRepository;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageableResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class ResourceService {

    @Autowired
    private ResourceRepository repository;


    /**
     * 新增资源
     *
     * @param dto 新增资源dto
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void addResource(AddResourceDto dto) throws BizException {

        //校验入参
        String validate = dto.validate();
        if (StringUtils.isNotBlank(validate)) {
            throw new BizException(validate);
        }

        ResourcePo po = as(dto, ResourcePo.class);

        //如果父级id不为空，查询父级资源
        if (dto.getParentId() != null && dto.getParentId() != -1) {
            ResourcePo parent = repository.findById(dto.getParentId())
                    .orElseThrow(() -> new BizException("新增失败,父级资源不存在."));

            //资源类型必须与父级资源类型一致
            if (dto.getKind() != parent.getKind()) {
                throw new BizException("新增失败,资源类型与父级资源类型不一致.");
            }

            //父级资源必须为目录
            if (parent.getKind() != 0) {
                throw new BizException("新增失败,父级资源必须为目录.");
            }

            //父级资源不能是自身
            if (parent.getId().equals(po.getId())) {
                throw new BizException("新增失败,父级资源不能是自身.");
            }

            po.setParent(parent);
        }


        //如果排序为空，则设置为0
        if (dto.getSeq() == null) {
            po.setSeq(0);
        }

        //权限为空默认为*
        if (StringUtils.isBlank(po.getPermission())) {
            po.setPermission("*");
        }

        repository.save(po);
    }

    /**
     * 编辑资源
     *
     * @param dto 编辑资源dto
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editResource(EditResourceDto dto) throws BizException {

        //校验入参
        String validate = dto.validate();
        if (StringUtils.isNotBlank(validate)) {
            throw new BizException(validate);
        }

        ResourcePo byId = repository.findById(dto.getId()).orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, byId);

        //如果父级id不为空，查询父级资源
        if (dto.getParentId() != null && dto.getParentId() != -1) {
            ResourcePo parent = repository.findById(dto.getParentId())
                    .orElseThrow(() -> new BizException("新增失败,父级资源不存在."));

            //资源类型必须与父级资源类型一致
            if (dto.getKind() != parent.getKind()) {
                throw new BizException("新增失败,资源类型与父级资源类型不一致.");
            }

            //父级资源必须为目录
            if (parent.getKind() != 0) {
                throw new BizException("新增失败,父级资源必须为目录.");
            }

            //父级资源不能是自身
            if (parent.getId().equals(byId.getId())) {
                throw new BizException("更新失败,父级资源不能是自身.");
            }

            byId.setParent(parent);
        }

        //如果父级id为空，则设置为-1
        if (dto.getParentId() == null || dto.getParentId() == -1) {
            byId.setParent(null);
        }


        //不允许变更资源类型 资源类型 0:菜单 1:接口
        if (dto.getKind() != (int) byId.getKind()) {
            throw new BizException("更新失败,资源类型不能修改.");
        }

        //如果排序为空，则设置为0
        if (dto.getSeq() == null) {
            byId.setSeq(0);
        }

        //权限为空默认为*
        if (StringUtils.isBlank(byId.getPermission())) {
            byId.setPermission("*");
        }

        repository.save(byId);
    }


    /**
     * 获取资源列表
     *
     * @param dto 获取资源列表dto
     * @return 资源列表
     */
    public PageableResult<GetResourceListVo> getResourceList(GetResourceListDto dto) {
        ResourcePo query = new ResourcePo();
        assign(dto, query);

        Page<ResourcePo> page = repository.getResourceList(query, PageRequest.of(dto.getPageNum() - 1, dto.getPageSize()));
        if (page.isEmpty()) {
            return PageableResult.successWithEmpty();
        }

        List<GetResourceListVo> vos = as(page.getContent(), GetResourceListVo.class);
        return PageableResult.success(vos, (int) page.getTotalElements());
    }


    /**
     * 获取资源详情
     *
     * @param dto 获取资源详情dto
     * @return 资源详情
     * @throws BizException 业务异常
     */
    public GetResourceDetailsVo getResourceDetails(CommonIdDto dto) throws BizException {
        ResourcePo po = repository.findById(dto.getId()).orElseThrow(() -> new BizException("获取详情失败,数据不存在或无权限访问."));
        GetResourceDetailsVo vo = as(po, GetResourceDetailsVo.class);
        if (po.getParent() != null) {
            vo.setParentId(po.getParent().getId());
        }
        return vo;
    }

    /**
     * 删除资源
     *
     * @param dto 删除资源dto
     * @throws BizException 业务异常
     */
    @Transactional
    public void removeResource(CommonIdDto dto) throws BizException {

        //删除单个资源
        if (!dto.isBatch()) {
            ResourcePo po = repository.findById(dto.getId()).orElseThrow(() -> new BizException("删除失败,数据不存在."));

            //如果有子资源，则不能删除
            if (repository.getMenuChildrenCount(po.getId()) > 0) {
                throw new BizException("删除失败,该资源下有子资源,不能删除.");
            }

            repository.deleteById(po.getId());
            return;
        }

        //如果删除多个资源，则需要判断是否有子资源
        for (Long id : dto.getIds()) {
            ResourcePo po = repository.findById(id).orElseThrow(() -> new BizException("删除失败,数据不存在."));

            if (repository.getMenuChildrenCount(id) > 0) {
                throw new BizException("无法移除: " + po.getName() + " ,请先移除子资源.");
            }

            repository.deleteById(id);
        }

    }

}
