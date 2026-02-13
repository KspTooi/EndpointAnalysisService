package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.noticercd.NoticeRcdPo;
import com.ksptooi.biz.core.model.noticercd.dto.AddNoticeRcdDto;
import com.ksptooi.biz.core.model.noticercd.dto.EditNoticeRcdDto;
import com.ksptooi.biz.core.model.noticercd.dto.GetUserNoticeRcdListDto;
import com.ksptooi.biz.core.model.noticercd.vo.GetNoticeRcdDetailsVo;
import com.ksptooi.biz.core.model.noticercd.vo.GetUserNoticeRcdListVo;
import com.ksptooi.biz.core.repository.NoticeRepository;
import com.ksptooi.biz.core.repository.NoticeRcdRepository;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.ksptooi.biz.auth.service.SessionService.session;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


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
     * 阅读用户通知记录
     *
     * @param dto
     */
    public void readUserNoticeRcd(CommonIdDto dto) throws Exception {

        var userId = session().getUserId();

        //搜集需要阅读的消息接收记录ID列表
        var ids = dto.getIds();

        //根据ID列表和用户ID查询消息接收记录列表
        var noticeRcdPos = repository.getNotifyRcdByIdsAndUserId(ids, userId);

        if (noticeRcdPos.isEmpty()) {
            throw new BizException("阅读失败,数据不存在或无权限访问.");
        }

        for (var noticeRcdPo : noticeRcdPos) {
            noticeRcdPo.setReadTime(LocalDateTime.now());
            repository.save(noticeRcdPo);
        }

    }


    /**
     * 查询消息接收记录列表
     *
     * @param dto
     * @return
     */
    public PageResult<GetUserNoticeRcdListVo> getUserNoticeRcdList(GetUserNoticeRcdListDto dto) throws Exception{

        //先分页当前用户查消息Rcd的Ids
        var rcdIds = repository.getNoticeRcdIds(session().getUserId(), dto.pageRequest());

        if (rcdIds.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        //根据RcdId查询对应的消息记录
        var noticeRcdPos = repository.findAllById(rcdIds);

        if (noticeRcdPos.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        //消息记录Po转Vo
        List<GetUserNoticeRcdListVo> vos = as(noticeRcdPos, GetUserNoticeRcdListVo.class);
        return PageResult.success(vos, (int) noticeRcdPos.size());
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
    public void removeNoticeRcd(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}