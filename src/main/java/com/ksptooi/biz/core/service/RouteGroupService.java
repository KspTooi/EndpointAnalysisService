package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.routegroup.dto.AddRouteGroupDto;
import com.ksptooi.biz.core.model.routegroup.dto.EditRouteGroupDto;
import com.ksptooi.biz.core.model.routegroup.dto.GetRouteGroupListDto;
import com.ksptooi.biz.core.model.routegroup.po.RouteGroupPo;
import com.ksptooi.biz.core.model.routegroup.vo.GetRouteGroupDetailsVo;
import com.ksptooi.biz.core.model.routegroup.vo.GetRouteGroupListVo;
import com.ksptooi.biz.core.repository.RouteGroupRepository;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class RouteGroupService {

    @Autowired
    private RouteGroupRepository repository;

    public PageResult<GetRouteGroupListVo> getRouteGroupList(GetRouteGroupListDto dto) {
        RouteGroupPo query = new RouteGroupPo();
        assign(dto, query);

        Page<RouteGroupPo> page = repository.getRouteGroupList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetRouteGroupListVo> vos = as(page.getContent(), GetRouteGroupListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addRouteGroup(AddRouteGroupDto dto) {
        RouteGroupPo insertPo = as(dto, RouteGroupPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editRouteGroup(EditRouteGroupDto dto) throws BizException {
        RouteGroupPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetRouteGroupDetailsVo getRouteGroupDetails(CommonIdDto dto) throws BizException {
        RouteGroupPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetRouteGroupDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeRouteGroup(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}