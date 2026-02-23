package com.ksptooi.biz.document.service;

import com.ksptooi.biz.document.model.epsite.EpSitePo;
import com.ksptooi.biz.document.model.epsite.dto.AddEpSiteDto;
import com.ksptooi.biz.document.model.epsite.dto.EditEpSiteDto;
import com.ksptooi.biz.document.model.epsite.dto.GetEpSiteListDto;
import com.ksptooi.biz.document.model.epsite.dto.ImportEpSiteDto;
import com.ksptooi.biz.document.model.epsite.vo.ExportEpSiteVo;
import com.ksptooi.biz.document.model.epsite.vo.GetEpSiteDetailsVo;
import com.ksptooi.biz.document.model.epsite.vo.GetEpSiteListVo;
import com.ksptooi.biz.document.repository.EpSiteRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.commons.utils.PinyinUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class EpSiteService {

    @Autowired
    private EpSiteRepository repository;

    public PageResult<GetEpSiteListVo> getEpSiteList(GetEpSiteListDto dto) {
        EpSitePo query = new EpSitePo();
        assign(dto, query);

        Page<EpSitePo> page = repository.getEpSiteList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetEpSiteListVo> vos = as(page.getContent(), GetEpSiteListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addEpSite(AddEpSiteDto dto) throws BizException {
        EpSitePo existingSite = repository.getSiteByName(dto.getName());
        if (existingSite != null) {
            throw new BizException("站点名称已存在，请使用其他名称");
        }

        EpSitePo insertPo = as(dto, EpSitePo.class);

        //处理拼音索引
        insertPo.setNamePyIdx(PinyinUtils.getFirstLetters(dto.getName()));
        insertPo.setUsernamePyIdx(PinyinUtils.getFirstLetters(dto.getUsername()));
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editEpSite(EditEpSiteDto dto) throws BizException {
        EpSitePo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        EpSitePo existingSite = repository.getSiteByNameExcludeId(dto.getName(), dto.getId());
        if (existingSite != null) {
            throw new BizException("站点名称已存在，请使用其他名称");
        }

        assign(dto, updatePo);

        //处理拼音索引
        updatePo.setNamePyIdx(PinyinUtils.getFirstLetters(dto.getName()));
        updatePo.setUsernamePyIdx(PinyinUtils.getFirstLetters(dto.getUsername()));
        repository.save(updatePo);
    }

    public GetEpSiteDetailsVo getEpSiteDetails(CommonIdDto dto) throws BizException {
        EpSitePo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetEpSiteDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeEpSite(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

    /**
     * 导入站点
     *
     * @param data 导入数据
     * @return 导入结果
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public int importEpSite(List<ImportEpSiteDto> data) throws BizException {
        var addPos = new ArrayList<EpSitePo>();
        var updatePos = new ArrayList<EpSitePo>();

        for (var item : data) {
            var existingSite = repository.getSiteByName(item.getName());

            if (existingSite != null) {
                existingSite.setAddress(item.getAddress());
                existingSite.setUsername(item.getUsername());
                existingSite.setPassword(item.getPassword());
                existingSite.setRemark(item.getRemark());
                existingSite.setSeq(item.getSeq());

                //处理拼音索引
                existingSite.setNamePyIdx(PinyinUtils.getFirstLetters(item.getName()));
                existingSite.setUsernamePyIdx(PinyinUtils.getFirstLetters(item.getUsername()));
                updatePos.add(existingSite);
                continue;
            }
            var addPo = as(item, EpSitePo.class);

            //处理拼音索引
            addPo.setNamePyIdx(PinyinUtils.getFirstLetters(item.getName()));
            addPo.setUsernamePyIdx(PinyinUtils.getFirstLetters(item.getUsername()));
            addPos.add(addPo);
        }

        repository.saveAll(addPos);
        repository.saveAll(updatePos);
        return addPos.size() + updatePos.size();
    }

    /**
     * 导出站点
     *
     * @param dto 导出参数
     * @return 导出数据
     * @throws BizException 业务异常
     */
    public List<ExportEpSiteVo> exportEpSite(GetEpSiteListDto dto) throws BizException {
        EpSitePo query = new EpSitePo();
        assign(dto, query);
        List<EpSitePo> pos = repository.getEpSiteListNotPage(query);
        return as(pos, ExportEpSiteVo.class);
    }

}
