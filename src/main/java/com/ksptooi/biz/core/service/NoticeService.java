package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.notice.NoticePo;
import com.ksptooi.biz.core.model.notice.dto.AddNoticeDto;
import com.ksptooi.biz.core.model.notice.dto.EditNoticeDto;
import com.ksptooi.biz.core.model.notice.dto.GetNoticeListDto;
import com.ksptooi.biz.core.model.notice.vo.GetNoticeDetailsVo;
import com.ksptooi.biz.core.model.notice.vo.GetNoticeListVo;
import com.ksptooi.biz.core.repository.NoticeRepository;
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
public class NoticeService {

    @Autowired
    private NoticeRepository repository;

    /**
     * 查询消息列表
     * 
     * @param dto 查询条件
     * @return 消息列表
     */
    public PageResult<GetNoticeListVo> getNoticeList(GetNoticeListDto dto) {
        NoticePo query = new NoticePo();
        assign(dto, query);

        Page<NoticePo> page = repository.getNoticeList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetNoticeListVo> vos = as(page.getContent(), GetNoticeListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增消息
     * 
     * @param dto 新增消息DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void addNotice(AddNoticeDto dto) {
        NoticePo insertPo = as(dto, NoticePo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑消息
     * 
     * @param dto 编辑消息DTO
     * @throws BizException 编辑消息失败
     */
    @Transactional(rollbackFor = Exception.class)
    public void editNotice(EditNoticeDto dto) throws BizException {
        NoticePo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询消息详情
     * 
     * @param dto 查询消息详情DTO
     * @return 消息详情
     * @throws BizException 查询消息详情失败
     */
    public GetNoticeDetailsVo getNoticeDetails(CommonIdDto dto) throws BizException {
        NoticePo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetNoticeDetailsVo.class);
    }

    /**
     * 删除消息
     * 
     * @param dto 删除消息DTO
     * @throws BizException 删除消息失败
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeNotice(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}