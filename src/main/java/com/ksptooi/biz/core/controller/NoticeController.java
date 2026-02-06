package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.notice.dto.AddNoticeDto;
import com.ksptooi.biz.core.model.notice.dto.EditNoticeDto;
import com.ksptooi.biz.core.model.notice.dto.GetNoticeListDto;
import com.ksptooi.biz.core.model.notice.vo.GetNoticeDetailsVo;
import com.ksptooi.biz.core.model.notice.vo.GetNoticeListVo;
import com.ksptooi.biz.core.service.NoticeService;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/notice")
@Tag(name = "notice", description = "消息控制器")
@Slf4j
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping("/getNoticeList")
    @Operation(summary = "查询消息列表")
    public PageResult<GetNoticeListVo> getNoticeList(@RequestBody @Valid GetNoticeListDto dto) throws Exception {
        return noticeService.getNoticeList(dto);
    }

    @Operation(summary = "新增消息")
    @PostMapping("/addNotice")
    public Result<String> addNotice(@RequestBody @Valid AddNoticeDto dto) throws Exception {
        noticeService.addNotice(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑消息")
    @PostMapping("/editNotice")
    public Result<String> editNotice(@RequestBody @Valid EditNoticeDto dto) throws Exception {
        noticeService.editNotice(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询消息详情")
    @PostMapping("/getNoticeDetails")
    public Result<GetNoticeDetailsVo> getNoticeDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetNoticeDetailsVo details = noticeService.getNoticeDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除消息")
    @PostMapping("/removeNotice")
    public Result<String> removeNotice(@RequestBody @Valid CommonIdDto dto) throws Exception {
        noticeService.removeNotice(dto);
        return Result.success("操作成功");
    }

}
