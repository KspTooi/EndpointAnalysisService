package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.noticetemplate.dto.AddNoticeTemplateDto;
import com.ksptooi.biz.core.model.noticetemplate.dto.EditNoticeTemplateDto;
import com.ksptooi.biz.core.model.noticetemplate.dto.GetNoticeTemplateListDto;
import com.ksptooi.biz.core.model.noticetemplate.vo.GetNoticeTemplateDetailsVo;
import com.ksptooi.biz.core.model.noticetemplate.vo.GetNoticeTemplateListVo;
import com.ksptooi.biz.core.service.NoticeTemplateService;
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
@RequestMapping("/noticeTemplate")
@Tag(name = "noticeTemplate", description = "通知模板表")
@Slf4j
public class NoticeTemplateController {

    @Autowired
    private NoticeTemplateService noticeTemplateService;

    @PostMapping("/getNoticeTemplateList")
    @Operation(summary = "列表查询")
    public PageResult<GetNoticeTemplateListVo> getNoticeTemplateList(@RequestBody @Valid GetNoticeTemplateListDto dto) throws Exception {
        return noticeTemplateService.getNoticeTemplateList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addNoticeTemplate")
    public Result<String> addNoticeTemplate(@RequestBody @Valid AddNoticeTemplateDto dto) throws Exception {
        noticeTemplateService.addNoticeTemplate(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editNoticeTemplate")
    public Result<String> editNoticeTemplate(@RequestBody @Valid EditNoticeTemplateDto dto) throws Exception {
        noticeTemplateService.editNoticeTemplate(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getNoticeTemplateDetails")
    public Result<GetNoticeTemplateDetailsVo> getNoticeTemplateDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetNoticeTemplateDetailsVo details = noticeTemplateService.getNoticeTemplateDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeNoticeTemplate")
    public Result<String> removeNoticeTemplate(@RequestBody @Valid CommonIdDto dto) throws Exception {
        noticeTemplateService.removeNoticeTemplate(dto);
        return Result.success("操作成功");
    }

}
