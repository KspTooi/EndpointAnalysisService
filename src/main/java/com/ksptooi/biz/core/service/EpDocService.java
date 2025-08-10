package com.ksptooi.biz.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ksptooi.commons.utils.OpenApiUtils;
import com.ksptooi.biz.core.model.epdoc.*;
import com.ksptooi.biz.core.model.epdocsynclog.EndpointDocSyncLogPo;
import com.ksptooi.biz.core.model.relayserver.RelayServerPo;
import com.ksptooi.biz.core.repository.EndpointDocSyncLogRepository;
import com.ksptooi.biz.core.repository.RelayServerRepository;
import com.ksptooi.commons.utils.web.PageResult;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.ksptooi.biz.core.repository.EndpointDocRepository;
import com.ksptooi.commons.exception.BizException;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EpDocService {

    @Autowired
    private EndpointDocRepository relayDocPullConfigRepository;

    @Autowired
    private EndpointDocSyncLogRepository endpointDocSyncLogRepository;

    @Autowired
    private RelayServerRepository relayServerRepository;

    @Transactional(rollbackFor = Exception.class)
    public void pullEpDoc(Long epDocId) throws BizException, JsonProcessingException {

        EndpointDocPo epDoc = relayDocPullConfigRepository.findById(epDocId)
                .orElseThrow(() -> new BizException("文档拉取配置不存在"));

        String pullUrl = epDoc.getDocPullUrl();

        //创建拉取记录
        EndpointDocSyncLogPo syncLogPo = new EndpointDocSyncLogPo();
        syncLogPo.setEndpointDoc(epDoc);
        syncLogPo.setHash(null);
        syncLogPo.setPullUrl(pullUrl);
        syncLogPo.setStatus(1); //0: 成功 1: 失败
        syncLogPo.setCreateTime(LocalDateTime.now());

        try{

            //从该地址拉取Swagger文档
            OpenAPI openAPI = new OpenAPIV3Parser().read(pullUrl);

            if (OpenApiUtils.isComplete(openAPI)) {
                String hash = OpenApiUtils.getHash(openAPI);
                if (hash != null) {
                    System.out.println("API文档MD5值: " + hash);
                }

                syncLogPo.setHash(hash);
                syncLogPo.setStatus(0); //0: 成功 1: 失败
            }

        }catch(Exception e){
            log.error("拉取文档失败", e);
            syncLogPo.setStatus(1); //0: 成功 1: 失败
        }finally{
            syncLogPo.setCreateTime(LocalDateTime.now());
            endpointDocSyncLogRepository.save(syncLogPo);
        }

    }

    /**
     * 获取文档拉取配置列表
     * @param dto 分页查询参数
     * @return
     */
    public PageResult<GetEpDocListVo> getEpDocList(GetEpDocListDto dto) {

        Page<EndpointDocPo> pPos = relayDocPullConfigRepository.getRelayDocPullConfigList(dto.pageRequest());
        List<GetEpDocListVo> vos = new ArrayList<>();

        for (EndpointDocPo p : pPos) {
            GetEpDocListVo vo = new GetEpDocListVo();
            vo.setId(p.getId());
            vo.setRelayServerId(p.getRelayServer().getId());
            vo.setRelayServerName(p.getRelayServer().getName());
            vo.setDocPullUrl(p.getDocPullUrl());
            vo.setPullTime(p.getPullTime());
            vo.setCreateTime(p.getCreateTime());
            vos.add(vo);
        }

        return PageResult.success(vos, pPos.getTotalElements());
    }

    /**
     * 添加文档拉取配置
     * @param dto 添加参数
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void addEpDoc(AddEpDocDto dto) throws BizException{

        // 查询中继通道是否存在
        RelayServerPo relayServer = relayServerRepository.findById(dto.getRelayServerId())
                .orElseThrow(() -> new BizException("中继通道不存在"));

        EndpointDocPo insertPo = new EndpointDocPo();
        insertPo.setRelayServer(relayServer);
        insertPo.setDocPullUrl(dto.getDocPullUrl());
        insertPo.setPullTime(null);
        insertPo.setCreateTime(LocalDateTime.now());
        relayDocPullConfigRepository.save(insertPo);
    }

    /**
     * 查询详情
     * @param id 文档拉取配置ID
     * @return
     */
    public GetEpDocDetailsVo getEpDocDetails(Long id) throws BizException{

        EndpointDocPo relayDocPullConfig = relayDocPullConfigRepository.findById(id)
                .orElseThrow(() -> new BizException("文档拉取配置不存在"));

        GetEpDocDetailsVo vo = new GetEpDocDetailsVo();
        vo.setId(relayDocPullConfig.getId());
        vo.setRelayServerId(relayDocPullConfig.getRelayServer().getId());
        vo.setRelayServerName(relayDocPullConfig.getRelayServer().getName());
        vo.setDocPullUrl(relayDocPullConfig.getDocPullUrl());
        vo.setPullTime(relayDocPullConfig.getPullTime());
        vo.setCreateTime(relayDocPullConfig.getCreateTime());
        return vo;
    }

    /**
     * 编辑文档拉取配置
     * @param dto 编辑参数
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void editEpDoc(EditEpDocDto dto) throws BizException{

        // 查询文档拉取配置是否存在
        EndpointDocPo relayDocPullConfig = relayDocPullConfigRepository.findById(dto.getId())
                .orElseThrow(() -> new BizException("文档拉取配置不存在"));

        // 查询中继通道是否存在
        RelayServerPo relayServer = relayServerRepository.findById(dto.getRelayServerId())
                .orElseThrow(() -> new BizException("中继通道不存在"));

        relayDocPullConfig.setRelayServer(relayServer);
        relayDocPullConfig.setDocPullUrl(dto.getDocPullUrl());
        relayDocPullConfig.setPullTime(null);
        relayDocPullConfigRepository.save(relayDocPullConfig);
    }



    /**
     * 删除文档拉取配置
     * @param id 文档拉取配置ID
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteEpDoc(Long id) throws BizException{

        // 查询文档拉取配置是否存在
        EndpointDocPo relayDocPullConfig = relayDocPullConfigRepository.findById(id)
                .orElseThrow(() -> new BizException("文档拉取配置不存在"));

        relayDocPullConfigRepository.delete(relayDocPullConfig);
    }


}
