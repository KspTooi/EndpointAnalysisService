package com.ksptooi.biz.noticetemplate.service;

import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.exception.BizException;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import java.util.Optional;
import com.ksptooi.biz.noticetemplate.repository.NoticeTemplateRepository;
import com.ksptooi.biz.noticetemplate.model.NoticeTemplatePo;
import com.ksptooi.biz.noticetemplate.model.vo.GetNoticeTemplateListVo;
import com.ksptooi.biz.noticetemplate.model.dto.GetNoticeTemplateListDto;
import com.ksptooi.biz.noticetemplate.model.vo.GetNoticeTemplateDetailsVo;
import com.ksptooi.biz.noticetemplate.model.dto.EditNoticeTemplateDto;
import com.ksptooi.biz.noticetemplate.model.dto.AddNoticeTemplateDto;


@Service
public class NoticeTemplateService {

    @Autowired
    private NoticeTemplateRepository repository;

    public PageResult<GetNoticeTemplateListVo> getNoticeTemplateList(GetNoticeTemplateListDto dto){
        NoticeTemplatePo query = new NoticeTemplatePo();
        assign(dto,query);

        Page<NoticeTemplatePo> page = repository.getNoticeTemplateList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetNoticeTemplateListVo> vos = as(page.getContent(), GetNoticeTemplateListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addNoticeTemplate(AddNoticeTemplateDto dto){
        NoticeTemplatePo insertPo = as(dto,NoticeTemplatePo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editNoticeTemplate(EditNoticeTemplateDto dto) throws BizException {
        NoticeTemplatePo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetNoticeTemplateDetailsVo getNoticeTemplateDetails(CommonIdDto dto) throws BizException {
        NoticeTemplatePo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetNoticeTemplateDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeNoticeTemplate(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}