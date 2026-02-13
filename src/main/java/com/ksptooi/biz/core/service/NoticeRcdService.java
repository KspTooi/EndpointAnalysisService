package com.ksptooi.biz.core.service;


import com.ksptooi.biz.core.model.noticercd.dto.GetUserNoticeRcdListDto;
import com.ksptooi.biz.core.model.noticercd.vo.GetNoticeRcdDetailsVo;
import com.ksptooi.biz.core.model.noticercd.vo.GetUserNoticeRcdListVo;
import com.ksptooi.biz.core.repository.NoticeRcdRepository;
import com.ksptooi.biz.core.repository.NoticeRepository;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.ksptooi.biz.auth.service.SessionService.session;
import static com.ksptool.entities.Entities.as;


@Service
public class NoticeRcdService {

    @Autowired
    private NoticeRcdRepository repository;

    @Autowired
    private NoticeRepository noticeRepository;

    /**
     * 获取当前用户未读通知数量
     *
     * @return
     */
    public Integer getUserNoticeCount() throws AuthException {
        var userId = session().getUserId();
        return repository.getUserNoticeCount(userId);
    }

    /**
     * 阅读全部用户通知记录
     */
    public void readAllUserNoticeRcd() throws Exception {
        var userId = session().getUserId();
        repository.readAllUserNoticeRcd(userId);
    }


    /**
     * 查询消息接收记录列表
     *
     * @param dto
     * @return
     */
    public PageResult<GetUserNoticeRcdListVo> getUserNoticeRcdList(GetUserNoticeRcdListDto dto) throws Exception {

        //分页查询当前用户消息列表（VO投影）
        var voPage = repository.getNoticeRcdsByUserId(session().getUserId(), dto.pageRequest());

        if (voPage.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        //从分页结果中提取RCD ID
        var rcdIds = voPage.getContent().stream().map(GetUserNoticeRcdListVo::getId).toList();

        if (rcdIds.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        //把这些已经查询出来的RcdPo设为已读
        var rcdPos = repository.getNotifyRcdByIdsAndUserId(rcdIds, session().getUserId());
        var needUpdate = false;

        for (var rcdPo : rcdPos) {
            if (rcdPo.getReadTime() == null) {
                needUpdate = true;
                rcdPo.setReadTime(LocalDateTime.now());
            }
        }

        if (needUpdate) {
            repository.saveAll(rcdPos);
        }

        return PageResult.success(voPage.getContent(), voPage.getTotalElements());
    }

    /**
     * 查询消息接收记录详情
     *
     * @param dto
     * @return
     * @throws BizException
     */
    public GetNoticeRcdDetailsVo getUserNoticeRcdDetails(CommonIdDto dto) throws Exception {

        var noticePo = noticeRepository.getNoticeByRcdIdAndUserId(dto.getId(), session().getUserId());

        if (noticePo == null) {
            throw new BizException("查询详情失败,数据不存在或无权限访问.");
        }

        return as(noticePo, GetNoticeRcdDetailsVo.class);
    }

    /**
     * 删除消息接收记录
     *
     * @param dto
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeNoticeRcd(CommonIdDto dto) throws Exception {

        var ids = dto.getIds();

        if (ids.isEmpty()) {
            throw new BizException("删除失败,参数错误");
        }

        //根据RCDID + UID查询RCD
        var noticeRcdPos = repository.getNotifyRcdByIdsAndUserId(ids, session().getUserId());

        if (noticeRcdPos.isEmpty()) {
            throw new BizException("删除失败,数据不存在或无权限访问.");
        }

        //删除RCD
        repository.deleteAll(noticeRcdPos);
    }

}