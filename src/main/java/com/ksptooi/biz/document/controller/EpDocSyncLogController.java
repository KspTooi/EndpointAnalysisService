package com.ksptooi.biz.document.controller;

import com.ksptooi.biz.document.model.epdocsynclog.GetEpDocSyncLogListDto;
import com.ksptooi.biz.document.model.epdocsynclog.GetEpDocSyncLogListVo;
import com.ksptooi.biz.document.service.EpDocSyncLogService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.utils.web.PageResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PrintLog
@Controller
@RestController
@RequestMapping("/epdocSyncLog")
public class EpDocSyncLogController {

    @Autowired
    private EpDocSyncLogService epDocSyncLogService;

    /**
     * 获取端点文档拉取记录列表
     *
     * @param dto 查询条件
     * @return 端点文档拉取记录列表
     */
    @PostMapping("/getEpSyncLogList")
    public PageResult<GetEpDocSyncLogListVo> getEpSyncLogList(@RequestBody @Valid GetEpDocSyncLogListDto dto) {
        return epDocSyncLogService.getEpSyncLogList(dto);
    }

}
