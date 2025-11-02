package com.ksptooi.biz.relay.service;

import com.ksptooi.biz.relay.model.routeserver.dto.AddRouteServerDto;
import com.ksptooi.biz.relay.model.routeserver.dto.EditRouteServerDto;
import com.ksptooi.biz.relay.model.routeserver.dto.GetRouteServerListDto;
import com.ksptooi.biz.relay.model.routeserver.po.RouteServerPo;
import com.ksptooi.biz.relay.model.routeserver.vo.GetRouteServerDetailsVo;
import com.ksptooi.biz.relay.model.routeserver.vo.GetRouteServerListVo;
import com.ksptooi.biz.relay.repository.RouteServerRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
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

    /**
     * 获取路由服务器列表
     *
     * @param dto 查询条件
     * @return 路由服务器列表
     */
    public PageResult<GetRouteServerListVo> getRouteServerList(GetRouteServerListDto dto) {
        RouteServerPo query = new RouteServerPo();
        assign(dto, query);

        Page<RouteServerPo> page = repository.getRouteServerList(query, dto.pageRequest());

        List<GetRouteServerListVo> vos = as(page.getContent(), GetRouteServerListVo.class);
        return PageResult.success(vos, page.getTotalElements());
    }

    /**
     * 新增路由服务器
     *
     * @param dto 新增路由服务器
     */
    @Transactional(rollbackFor = Exception.class)
    public void addRouteServer(AddRouteServerDto dto) {
        RouteServerPo insertPo = as(dto, RouteServerPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑路由服务器
     *
     * @param dto 编辑路由服务器
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editRouteServer(EditRouteServerDto dto) throws BizException {
        RouteServerPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 获取路由服务器详情
     *
     * @param dto 查询条件
     * @return 路由服务器详情
     * @throws BizException 业务异常
     */
    public GetRouteServerDetailsVo getRouteServerDetails(CommonIdDto dto) throws BizException {
        RouteServerPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetRouteServerDetailsVo.class);
    }

    /**
     * 删除路由服务器
     *
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeRouteServer(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {

            RouteServerPo po = repository.findById(dto.getId())
                    .orElseThrow(() -> new BizException("操作失败,数据不存在或无权限访问."));

            //获取有多少路由规则在使用该路由服务器
            int routeRuleCount = po.getRouteRules().size();
            if (routeRuleCount > 0) {
                throw new BizException(String.format("该路由服务器正在被 %d 个路由规则使用，请先取消所有关联关系后再尝试移除", routeRuleCount));
            }

            repository.deleteById(dto.getId());
        }
    }

}