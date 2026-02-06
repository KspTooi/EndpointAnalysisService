package com.ksptooi.biz.notice.controller;

import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptooi.biz.notice.service.NoticeService;
import com.ksptooi.biz.notice.model.dto.AddNoticeDto;
import com.ksptooi.biz.notice.model.dto.EditNoticeDto;
import com.ksptooi.biz.notice.model.dto.GetNoticeListDto;
import com.ksptooi.biz.notice.model.vo.GetNoticeListVo;
import com.ksptooi.biz.notice.model.vo.GetNoticeDetailsVo;


@RestController
@RequestMapping("/notice")
@Tag(name = "notice", description = "消息表")
@Slf4j
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping("/getNoticeList")
    @Operation(summary ="列表查询")
    public PageResult<GetNoticeListVo> getNoticeList(@RequestBody @Valid GetNoticeListDto dto) throws Exception{
        return noticeService.getNoticeList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addNotice")
    public Result<String> addNotice(@RequestBody @Valid AddNoticeDto dto) throws Exception{
		noticeService.addNotice(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editNotice")
    public Result<String> editNotice(@RequestBody @Valid EditNoticeDto dto) throws Exception{
		noticeService.editNotice(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getNoticeDetails")
    public Result<GetNoticeDetailsVo> getNoticeDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetNoticeDetailsVo details = noticeService.getNoticeDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeNotice")
    public Result<String> removeNotice(@RequestBody @Valid CommonIdDto dto) throws Exception{
        noticeService.removeNotice(dto);
        return Result.success("操作成功");
    }

}
