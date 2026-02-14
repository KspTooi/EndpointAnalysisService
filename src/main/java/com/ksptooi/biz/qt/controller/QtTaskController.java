package com.ksptooi.biz.qt.controller;

import com.ksptooi.biz.qt.model.qttask.dto.AddQtTaskDto;
import com.ksptooi.biz.qt.model.qttask.dto.EditQtTaskDto;
import com.ksptooi.biz.qt.model.qttask.dto.GetQtTaskListDto;
import com.ksptooi.biz.qt.model.qttask.vo.GetLocalBeanListVo;
import com.ksptooi.biz.qt.model.qttask.vo.GetQtTaskDetailsVo;
import com.ksptooi.biz.qt.model.qttask.vo.GetQtTaskListVo;
import com.ksptooi.biz.qt.service.QtTaskService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.dataprocess.Str;
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

import java.util.List;


@PrintLog
@RestController
@RequestMapping("/qtTask")
@Tag(name = "qtTask", description = "任务调度表")
@Slf4j
public class QtTaskController {

    @Autowired
    private QtTaskService qtTaskService;

    @PreAuthorize("@auth.hasCode('qt:task:view')")
    @PostMapping("/getQtTaskList")
    @Operation(summary = "获取任务列表")
    public PageResult<GetQtTaskListVo> getQtTaskList(@RequestBody @Valid GetQtTaskListDto dto) throws Exception {
        return qtTaskService.getQtTaskList(dto);
    }

    @PreAuthorize("@auth.hasCode('qt:task:add')")
    @Operation(summary = "新增任务")
    @PostMapping("/addQtTask")
    public Result<String> addQtTask(@RequestBody @Valid AddQtTaskDto dto) throws Exception {

        //暂不支持HTTP模式
        if (dto.getKind() == 1) {
            return Result.error("暂不支持HTTP模式");
        }

        //验证参数
        String validate = dto.validate();
        if (Str.isNotBlank(validate)) {
            return Result.error(validate);
        }

        qtTaskService.addQtTask(dto);
        return Result.success("新增成功");
    }

    @PreAuthorize("@auth.hasCode('qt:task:edit')")
    @Operation(summary = "编辑任务")
    @PostMapping("/editQtTask")
    public Result<String> editQtTask(@RequestBody @Valid EditQtTaskDto dto) throws Exception {

        //暂不支持HTTP模式
        if (dto.getKind() == 1) {
            return Result.error("暂不支持HTTP模式");
        }

        //验证参数
        String validate = dto.validate();
        if (Str.isNotBlank(validate)) {
            return Result.error(validate);
        }

        qtTaskService.editQtTask(dto);
        return Result.success("修改成功");
    }

    @PreAuthorize("@auth.hasCode('qt:task:view')")
    @Operation(summary = "获取任务详情")
    @PostMapping("/getQtTaskDetails")
    public Result<GetQtTaskDetailsVo> getQtTaskDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetQtTaskDetailsVo details = qtTaskService.getQtTaskDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @PreAuthorize("@auth.hasCode('qt:task:remove')")
    @Operation(summary = "删除任务")
    @PostMapping("/removeQtTask")
    public Result<String> removeQtTask(@RequestBody @Valid CommonIdDto dto) throws Exception {
        qtTaskService.removeQtTask(dto);
        return Result.success("操作成功");
    }

    @PreAuthorize("@auth.hasCode('qt:task:edit')")
    @Operation(summary = "获取本地任务Bean列表")
    @PostMapping("/getLocalBeanList")
    public Result<List<GetLocalBeanListVo>> getLocalBeanList() throws Exception {
        return Result.success(qtTaskService.getLocalBeanList());
    }

}
