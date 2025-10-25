package com.ksptooi.biz.document.service;

import com.ksptooi.biz.document.model.epdocsynclog.EndpointDocSyncLogPo;
import com.ksptooi.biz.document.model.epdocsynclog.GetEpDocSyncLogListDto;
import com.ksptooi.biz.document.model.epdocsynclog.GetEpDocSyncLogListVo;
import com.ksptooi.biz.document.repository.EndpointDocSyncLogRepository;
import com.ksptooi.commons.utils.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EpDocSyncLogService {

    @Autowired
    private EndpointDocSyncLogRepository endpointDocSyncLogRepository;

    /**
     * 获取端点文档拉取记录列表
     *
     * @param dto 查询条件
     * @return 端点文档拉取记录列表
     */
    public PageResult<GetEpDocSyncLogListVo> getEpSyncLogList(GetEpDocSyncLogListDto dto) {

        Page<EndpointDocSyncLogPo> pPos = endpointDocSyncLogRepository.getEpSyncLogList(dto, dto.pageRequest());
        List<GetEpDocSyncLogListVo> vos = new ArrayList<>();

        for (EndpointDocSyncLogPo p : pPos.getContent()) {
            GetEpDocSyncLogListVo vo = new GetEpDocSyncLogListVo();
            vo.setId(p.getId());
            vo.setEpDocId(p.getEndpointDoc().getId());
            vo.setHash(p.getHash());
            vo.setPullUrl(p.getPullUrl());
            vo.setStatus(p.getStatus());
            vo.setCreateTime(p.getCreateTime());
            vo.setNewVersionCreated(p.getNewVersionCreated());
            vo.setNewVersionNum(p.getNewVersionNum());
            vos.add(vo);
        }

        return PageResult.success(vos, pPos.getTotalElements());
    }


}
