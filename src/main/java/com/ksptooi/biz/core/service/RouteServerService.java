package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.routeserver.dto.AddRouteServerDto;
import com.ksptooi.biz.core.model.routeserver.dto.EditRouteServerDto;
import com.ksptooi.biz.core.model.routeserver.dto.GetRouteServerListDto;
import com.ksptooi.biz.core.model.routeserver.po.RouteServerPo;
import com.ksptooi.biz.core.model.routeserver.vo.GetRouteServerDetailsVo;
import com.ksptooi.biz.core.model.routeserver.vo.GetRouteServerListVo;
import com.ksptooi.biz.core.repository.RouteServerRepository;
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
public class RouteServerService {

    @Autowired
    private RouteServerRepository repository;

    public PageResult<GetRouteServerListVo> getRouteServerList(GetRouteServerListDto dto) {
        RouteServerPo query = new RouteServerPo();
        assign(dto, query);

        Page<RouteServerPo> page = repository.getRouteServerList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty((int) page.getTotalElements());
        }

        List<GetRouteServerListVo> vos = as(page.getContent(), GetRouteServerListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addRouteServer(AddRouteServerDto dto) {
        RouteServerPo insertPo = as(dto, RouteServerPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editRouteServer(EditRouteServerDto dto) throws BizException {
        RouteServerPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetRouteServerDetailsVo getRouteServerDetails(CommonIdDto dto) throws BizException {
        RouteServerPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetRouteServerDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeRouteServer(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}