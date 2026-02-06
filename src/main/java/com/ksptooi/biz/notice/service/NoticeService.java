package com.ksptooi.biz.notice.service;

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
import com.ksptooi.biz.notice.repository.NoticeRepository;
import com.ksptooi.biz.notice.model.NoticePo;
import com.ksptooi.biz.notice.model.vo.GetNoticeListVo;
import com.ksptooi.biz.notice.model.dto.GetNoticeListDto;
import com.ksptooi.biz.notice.model.vo.GetNoticeDetailsVo;
import com.ksptooi.biz.notice.model.dto.EditNoticeDto;
import com.ksptooi.biz.notice.model.dto.AddNoticeDto;


@Service
public class NoticeService {

    @Autowired
    private NoticeRepository repository;

    public PageResult<GetNoticeListVo> getNoticeList(GetNoticeListDto dto){
        NoticePo query = new NoticePo();
        assign(dto,query);

        Page<NoticePo> page = repository.getNoticeList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetNoticeListVo> vos = as(page.getContent(), GetNoticeListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addNotice(AddNoticeDto dto){
        NoticePo insertPo = as(dto,NoticePo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editNotice(EditNoticeDto dto) throws BizException {
        NoticePo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetNoticeDetailsVo getNoticeDetails(CommonIdDto dto) throws BizException {
        NoticePo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetNoticeDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeNotice(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}