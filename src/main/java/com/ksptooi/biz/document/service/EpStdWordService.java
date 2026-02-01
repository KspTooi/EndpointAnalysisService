package com.ksptooi.biz.document.service;

import com.ksptooi.biz.document.model.epstdword.EpStdWordPo;
import com.ksptooi.biz.document.model.epstdword.dto.AddEpStdWordDto;
import com.ksptooi.biz.document.model.epstdword.dto.EditEpStdWordDto;
import com.ksptooi.biz.document.model.epstdword.dto.GetEpStdWordListDto;
import com.ksptooi.biz.document.model.epstdword.vo.GetEpStdWordDetailsVo;
import com.ksptooi.biz.document.model.epstdword.vo.GetEpStdWordListVo;
import com.ksptooi.biz.document.repository.EpStdWordRepository;
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
public class EpStdWordService {

    @Autowired
    private EpStdWordRepository repository;

    public PageResult<GetEpStdWordListVo> getEpStdWordList(GetEpStdWordListDto dto) {
        EpStdWordPo query = new EpStdWordPo();
        assign(dto, query);

        Page<EpStdWordPo> page = repository.getEpStdWordList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetEpStdWordListVo> vos = as(page.getContent(), GetEpStdWordListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addEpStdWord(AddEpStdWordDto dto) {
        EpStdWordPo insertPo = as(dto, EpStdWordPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editEpStdWord(EditEpStdWordDto dto) throws BizException {
        EpStdWordPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetEpStdWordDetailsVo getEpStdWordDetails(CommonIdDto dto) throws BizException {
        EpStdWordPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetEpStdWordDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeEpStdWord(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}