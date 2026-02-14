package com.ksptooi.biz.qt.controller;

import com.ksptooi.biz.qt.model.qttaskrcd.dto.GetQtTaskRcdListDto;
import com.ksptooi.biz.qt.model.qttaskrcd.vo.GetQtTaskRcdDetailsVo;
import com.ksptooi.biz.qt.model.qttaskrcd.vo.GetQtTaskRcdListVo;
import com.ksptooi.biz.qt.service.QtTaskRcdService;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/qtTaskRcd")
@Tag(name = "qtTaskRcd", description = "任务调度日志表")
@Slf4j
public class QtTaskRcdController {

    @Autowired
    private QtTaskRcdService qtTaskRcdService;

    @PreAuthorize("@auth.hasCode('qt:rcd:view')")
    @PostMapping("/getQtTaskRcdList")
    @Operation(summary = "查询调度日志列表")
    public PageResult<GetQtTaskRcdListVo> getQtTaskRcdList(@RequestBody @Valid GetQtTaskRcdListDto dto) throws Exception {
        return qtTaskRcdService.getQtTaskRcdList(dto);
    }

    @PreAuthorize("@auth.hasCode('qt:rcd:view')")
    @Operation(summary = "查询调度日志详情")
    @PostMapping("/getQtTaskRcdDetails")
    public Result<GetQtTaskRcdDetailsVo> getQtTaskRcdDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetQtTaskRcdDetailsVo details = qtTaskRcdService.getQtTaskRcdDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @PreAuthorize("@auth.hasCode('qt:rcd:remove')")
    @Operation(summary = "删除调度日志")
    @PostMapping("/removeQtTaskRcd")
    public Result<String> removeQtTaskRcd(@RequestBody @Valid CommonIdDto dto) throws Exception {
        qtTaskRcdService.removeQtTaskRcd(dto);
        return Result.success("操作成功");
    }

}
