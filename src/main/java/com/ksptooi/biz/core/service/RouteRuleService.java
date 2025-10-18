package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.routerule.dto.AddRouteRuleDto;
import com.ksptooi.biz.core.model.routerule.dto.EditRouteRuleDto;
import com.ksptooi.biz.core.model.routerule.dto.GetRouteRuleListDto;
import com.ksptooi.biz.core.model.routerule.po.RouteRulePo;
import com.ksptooi.biz.core.model.routerule.vo.GetRouteRuleDetailsVo;
import com.ksptooi.biz.core.model.routerule.vo.GetRouteRuleListVo;
import com.ksptooi.biz.core.repository.RouteRuleRepository;
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
public class RouteRuleService {

    @Autowired
    private RouteRuleRepository repository;

    public PageResult<GetRouteRuleListVo> getRouteRuleList(GetRouteRuleListDto dto) {
        RouteRulePo query = new RouteRulePo();
        assign(dto, query);

        Page<RouteRulePo> page = repository.getRouteRuleList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetRouteRuleListVo> vos = as(page.getContent(), GetRouteRuleListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addRouteRule(AddRouteRuleDto dto) {
        RouteRulePo insertPo = as(dto, RouteRulePo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editRouteRule(EditRouteRuleDto dto) throws BizException {
        RouteRulePo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetRouteRuleDetailsVo getRouteRuleDetails(CommonIdDto dto) throws BizException {
        RouteRulePo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetRouteRuleDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeRouteRule(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}