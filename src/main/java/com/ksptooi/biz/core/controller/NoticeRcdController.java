package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.noticercd.dto.AddNoticeRcdDto;
import com.ksptooi.biz.core.model.noticercd.dto.EditNoticeRcdDto;
import com.ksptooi.biz.core.model.noticercd.dto.GetNoticeRcdListDto;
import com.ksptooi.biz.core.model.noticercd.vo.GetNoticeRcdDetailsVo;
import com.ksptooi.biz.core.model.noticercd.vo.GetNoticeRcdListVo;
import com.ksptooi.biz.core.service.NoticeRcdService;
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
@RequestMapping("/noticeRcd")
@Tag(name = "noticeRcd", description = "用户通知记录(接收状态)")
@Slf4j
public class NoticeRcdController {

    @Autowired
    private NoticeRcdService noticeRcdService;

    @PostMapping("/getNoticeRcdList")
    @Operation(summary = "列表查询")
    public PageResult<GetNoticeRcdListVo> getNoticeRcdList(@RequestBody @Valid GetNoticeRcdListDto dto) throws Exception {
        return noticeRcdService.getNoticeRcdList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addNoticeRcd")
    public Result<String> addNoticeRcd(@RequestBody @Valid AddNoticeRcdDto dto) throws Exception {
        noticeRcdService.addNoticeRcd(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editNoticeRcd")
    public Result<String> editNoticeRcd(@RequestBody @Valid EditNoticeRcdDto dto) throws Exception {
        noticeRcdService.editNoticeRcd(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getNoticeRcdDetails")
    public Result<GetNoticeRcdDetailsVo> getNoticeRcdDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetNoticeRcdDetailsVo details = noticeRcdService.getNoticeRcdDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeNoticeRcd")
    public Result<String> removeNoticeRcd(@RequestBody @Valid CommonIdDto dto) throws Exception {
        noticeRcdService.removeNoticeRcd(dto);
        return Result.success("操作成功");
    }

}
