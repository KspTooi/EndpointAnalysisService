package com.ksptooi.biz.relay.service;

import com.ksptooi.biz.relay.model.routerule.dto.AddRouteRuleDto;
import com.ksptooi.biz.relay.model.routerule.dto.EditRouteRuleDto;
import com.ksptooi.biz.relay.model.routerule.dto.GetRouteRuleListDto;
import com.ksptooi.biz.relay.model.routerule.po.RouteRulePo;
import com.ksptooi.biz.relay.model.routerule.vo.GetRouteRuleDetailsVo;
import com.ksptooi.biz.relay.model.routerule.vo.GetRouteRuleListVo;
import com.ksptooi.biz.relay.model.routeserver.po.RouteServerPo;
import com.ksptooi.biz.relay.repository.RouteRuleRepository;
import com.ksptooi.biz.relay.repository.RouteServerRepository;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class RouteRuleService {

    @Autowired
    private RouteRuleRepository repository;

    @Autowired
    private RouteServerRepository routeServerRepository;

    public PageResult<GetRouteRuleListVo> getRouteRuleList(GetRouteRuleListDto dto) {
        RouteRulePo query = new RouteRulePo();
        assign(dto, query);

        Page<RouteRulePo> page = repository.getRouteRuleList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetRouteRuleListVo> vos = new ArrayList<>();

        for (RouteRulePo po : page.getContent()) {
            GetRouteRuleListVo vo = as(po, GetRouteRuleListVo.class);
            vo.setRouteServerName(po.getRouteServer().getName());
            vos.add(vo);
        }

        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addRouteRule(AddRouteRuleDto dto) throws BizException {
        RouteRulePo insertPo = as(dto, RouteRulePo.class);

        //检查路由策略名是否存在
        String validate = checkRouteRuleName(dto.getName(), null);
        if (StringUtils.isNotBlank(validate)) {
            throw new BizException(validate);
        }

        //查询目标服务器
        RouteServerPo routeServer = routeServerRepository.findById(dto.getRouteServerId())
                .orElseThrow(() -> new BizException("目标服务器不存在或无权限访问."));
        insertPo.setRouteServer(routeServer);

        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editRouteRule(EditRouteRuleDto dto) throws BizException {
        RouteRulePo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        //检查路由策略名是否存在
        String validate = checkRouteRuleName(dto.getName(), updatePo.getId());
        if (StringUtils.isNotBlank(validate)) {
            throw new BizException(validate);
        }

        //查询目标服务器
        RouteServerPo routeServer = routeServerRepository.findById(dto.getRouteServerId())
                .orElseThrow(() -> new BizException("目标服务器不存在或无权限访问."));
        updatePo.setRouteServer(routeServer);

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetRouteRuleDetailsVo getRouteRuleDetails(CommonIdDto dto) throws BizException {
        RouteRulePo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("数据不存在或无权限访问."));

        GetRouteRuleDetailsVo ret = as(po, GetRouteRuleDetailsVo.class);
        ret.setRouteServerId(po.getRouteServer().getId());
        return ret;
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeRouteRule(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            throw new BizException("批量删除不支持.");
        }
        if (!dto.isBatch()) {

            RouteRulePo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("操作失败,数据不存在或无权限访问."));

            //获取有多少请求组在使用该路由规则
            int groupCount = po.getRelayServerRoutes().size();
            if (groupCount > 0) {
                throw new BizException(String.format("该路由规则正在被 %d 个中继服务器使用，请先取消所有关联关系后再尝试移除", groupCount));
            }

            repository.deleteById(dto.getId());
        }
    }


    /**
     * 检查路由策略名是否存在
     *
     * @param name 路由策略名
     * @param id   路由策略ID(可选,用于排除当前路由策略)
     * @return 错误信息 当参数合法时返回null
     */
    public String checkRouteRuleName(String name, Long id) {

        if (id == null) {
            Long count = repository.countByName(name);
            if (count > 0) {
                return "路由规则:[" + name + "]已存在";
            }
        }

        if (id != null) {
            Long count = repository.countByNameExcludeId(name, id);
            if (count > 0) {
                return "路由规则:[" + name + "]已存在";
            }
        }

        return null;
    }

}