package com.ksptooi.biz.noticetemplate.controller;

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

import com.ksptooi.biz.noticetemplate.service.NoticeTemplateService;
import com.ksptooi.biz.noticetemplate.model.dto.AddNoticeTemplateDto;
import com.ksptooi.biz.noticetemplate.model.dto.EditNoticeTemplateDto;
import com.ksptooi.biz.noticetemplate.model.dto.GetNoticeTemplateListDto;
import com.ksptooi.biz.noticetemplate.model.vo.GetNoticeTemplateListVo;
import com.ksptooi.biz.noticetemplate.model.vo.GetNoticeTemplateDetailsVo;


@RestController
@RequestMapping("/noticeTemplate")
@Tag(name = "noticeTemplate", description = "通知模板表")
@Slf4j
public class NoticeTemplateController {

    @Autowired
    private NoticeTemplateService noticeTemplateService;

    @PostMapping("/getNoticeTemplateList")
    @Operation(summary ="列表查询")
    public PageResult<GetNoticeTemplateListVo> getNoticeTemplateList(@RequestBody @Valid GetNoticeTemplateListDto dto) throws Exception{
        return noticeTemplateService.getNoticeTemplateList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addNoticeTemplate")
    public Result<String> addNoticeTemplate(@RequestBody @Valid AddNoticeTemplateDto dto) throws Exception{
		noticeTemplateService.addNoticeTemplate(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editNoticeTemplate")
    public Result<String> editNoticeTemplate(@RequestBody @Valid EditNoticeTemplateDto dto) throws Exception{
		noticeTemplateService.editNoticeTemplate(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getNoticeTemplateDetails")
    public Result<GetNoticeTemplateDetailsVo> getNoticeTemplateDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetNoticeTemplateDetailsVo details = noticeTemplateService.getNoticeTemplateDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeNoticeTemplate")
    public Result<String> removeNoticeTemplate(@RequestBody @Valid CommonIdDto dto) throws Exception{
        noticeTemplateService.removeNoticeTemplate(dto);
        return Result.success("操作成功");
    }

}
