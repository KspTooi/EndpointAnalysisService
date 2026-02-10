package com.ksptooi.biz.qt.controller;

import com.ksptooi.biz.qt.model.qttaskrcd.dto.AddQtTaskRcdDto;
import com.ksptooi.biz.qt.model.qttaskrcd.dto.EditQtTaskRcdDto;
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

    @PostMapping("/getQtTaskRcdList")
    @Operation(summary = "列表查询")
    public PageResult<GetQtTaskRcdListVo> getQtTaskRcdList(@RequestBody @Valid GetQtTaskRcdListDto dto) throws Exception {
        return qtTaskRcdService.getQtTaskRcdList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addQtTaskRcd")
    public Result<String> addQtTaskRcd(@RequestBody @Valid AddQtTaskRcdDto dto) throws Exception {
        qtTaskRcdService.addQtTaskRcd(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editQtTaskRcd")
    public Result<String> editQtTaskRcd(@RequestBody @Valid EditQtTaskRcdDto dto) throws Exception {
        qtTaskRcdService.editQtTaskRcd(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getQtTaskRcdDetails")
    public Result<GetQtTaskRcdDetailsVo> getQtTaskRcdDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetQtTaskRcdDetailsVo details = qtTaskRcdService.getQtTaskRcdDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeQtTaskRcd")
    public Result<String> removeQtTaskRcd(@RequestBody @Valid CommonIdDto dto) throws Exception {
        qtTaskRcdService.removeQtTaskRcd(dto);
        return Result.success("操作成功");
    }

}
