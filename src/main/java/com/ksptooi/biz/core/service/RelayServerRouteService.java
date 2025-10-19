package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.relayserverroute.dto.AddRelayServerRouteDto;
import com.ksptooi.biz.core.model.relayserverroute.dto.EditRelayServerRouteDto;
import com.ksptooi.biz.core.model.relayserverroute.dto.GetRelayServerRouteListDto;
import com.ksptooi.biz.core.model.relayserverroute.po.RelayServerRoutePo;
import com.ksptooi.biz.core.model.relayserverroute.vo.GetRelayServerRouteDetailsVo;
import com.ksptooi.biz.core.model.relayserverroute.vo.GetRelayServerRouteListVo;
import com.ksptooi.biz.core.repository.RelayServerRouteRepository;
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
public class RelayServerRouteService {

    @Autowired
    private RelayServerRouteRepository repository;

    public PageResult<GetRelayServerRouteListVo> getRelayServerRouteList(GetRelayServerRouteListDto dto) {
        RelayServerRoutePo query = new RelayServerRoutePo();
        assign(dto, query);

        Page<RelayServerRoutePo> page = repository.getRelayServerRouteList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetRelayServerRouteListVo> vos = as(page.getContent(), GetRelayServerRouteListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addRelayServerRoute(AddRelayServerRouteDto dto) {
        RelayServerRoutePo insertPo = as(dto, RelayServerRoutePo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editRelayServerRoute(EditRelayServerRouteDto dto) throws BizException {
        RelayServerRoutePo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetRelayServerRouteDetailsVo getRelayServerRouteDetails(CommonIdDto dto) throws BizException {
        RelayServerRoutePo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetRelayServerRouteDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeRelayServerRoute(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}