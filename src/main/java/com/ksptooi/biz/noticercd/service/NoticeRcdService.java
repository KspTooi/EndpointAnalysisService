package com.ksptooi.biz.noticercd.service;

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
import com.ksptooi.biz.noticercd.repository.NoticeRcdRepository;
import com.ksptooi.biz.noticercd.model.NoticeRcdPo;
import com.ksptooi.biz.noticercd.model.vo.GetNoticeRcdListVo;
import com.ksptooi.biz.noticercd.model.dto.GetNoticeRcdListDto;
import com.ksptooi.biz.noticercd.model.vo.GetNoticeRcdDetailsVo;
import com.ksptooi.biz.noticercd.model.dto.EditNoticeRcdDto;
import com.ksptooi.biz.noticercd.model.dto.AddNoticeRcdDto;


@Service
public class NoticeRcdService {

    @Autowired
    private NoticeRcdRepository repository;

    public PageResult<GetNoticeRcdListVo> getNoticeRcdList(GetNoticeRcdListDto dto){
        NoticeRcdPo query = new NoticeRcdPo();
        assign(dto,query);

        Page<NoticeRcdPo> page = repository.getNoticeRcdList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetNoticeRcdListVo> vos = as(page.getContent(), GetNoticeRcdListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addNoticeRcd(AddNoticeRcdDto dto){
        NoticeRcdPo insertPo = as(dto,NoticeRcdPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editNoticeRcd(EditNoticeRcdDto dto) throws BizException {
        NoticeRcdPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetNoticeRcdDetailsVo getNoticeRcdDetails(CommonIdDto dto) throws BizException {
        NoticeRcdPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetNoticeRcdDetailsVo.class);
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