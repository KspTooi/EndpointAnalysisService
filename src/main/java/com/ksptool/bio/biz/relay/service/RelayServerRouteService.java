package com.ksptool.bio.biz.relay.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.relay.model.relayserverroute.dto.AddRelayServerRouteDto;
import com.ksptool.bio.biz.relay.model.relayserverroute.dto.EditRelayServerRouteDto;
import com.ksptool.bio.biz.relay.model.relayserverroute.dto.GetRelayServerRouteListDto;
import com.ksptool.bio.biz.relay.model.relayserverroute.po.RelayServerRoutePo;
import com.ksptool.bio.biz.relay.model.relayserverroute.vo.GetRelayServerRouteDetailsVo;
import com.ksptool.bio.biz.relay.model.relayserverroute.vo.GetRelayServerRouteListVo;
import com.ksptool.bio.biz.relay.repository.RelayServerRouteRepository;
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
        List<GetRelayServerRouteListVo> vos = as(page.getContent(), GetRelayServerRouteListVo.class);
        return PageResult.success(vos, page.getTotalElements());
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