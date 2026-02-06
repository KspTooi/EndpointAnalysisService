package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.noticercd.NoticeRcdPo;
import com.ksptooi.biz.core.model.noticercd.dto.AddNoticeRcdDto;
import com.ksptooi.biz.core.model.noticercd.dto.EditNoticeRcdDto;
import com.ksptooi.biz.core.model.noticercd.dto.GetNoticeRcdListDto;
import com.ksptooi.biz.core.model.noticercd.vo.GetNoticeRcdDetailsVo;
import com.ksptooi.biz.core.model.noticercd.vo.GetNoticeRcdListVo;
import com.ksptooi.biz.core.repository.NoticeRcdRepository;
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
public class NoticeRcdService {

    @Autowired
    private NoticeRcdRepository repository;

    public PageResult<GetNoticeRcdListVo> getNoticeRcdList(GetNoticeRcdListDto dto) {
        NoticeRcdPo query = new NoticeRcdPo();
        assign(dto, query);

        Page<NoticeRcdPo> page = repository.getNoticeRcdList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetNoticeRcdListVo> vos = as(page.getContent(), GetNoticeRcdListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addNoticeRcd(AddNoticeRcdDto dto) {
        NoticeRcdPo insertPo = as(dto, NoticeRcdPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editNoticeRcd(EditNoticeRcdDto dto) throws BizException {
        NoticeRcdPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetNoticeRcdDetailsVo getNoticeRcdDetails(CommonIdDto dto) throws BizException {
        NoticeRcdPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetNoticeRcdDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeNoticeRcd(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}