package com.ksptool.bio.biz.qf.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.qf.model.qfmodel.dto.AddQfModelDto;
import com.ksptool.bio.biz.qf.model.qfmodel.dto.EditQfModelDto;
import com.ksptool.bio.biz.qf.model.qfmodel.dto.GetQfModelListDto;
import com.ksptool.bio.biz.qf.model.qfmodel.vo.GetQfModelDetailsVo;
import com.ksptool.bio.biz.qf.model.qfmodel.vo.GetQfModelListVo;
import com.ksptool.bio.biz.qf.service.QfModelService;
import com.ksptool.bio.commons.annotation.PrintLog;
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

@PrintLog
@RestController
@RequestMapping("/qfModel")
@Tag(name = "流程模型", description = "流程模型")
@Slf4j
public class QfModelController {

    @Autowired
    private QfModelService qfModelService;

    @PreAuthorize("@auth.hasCode('qf:model:view')")
    @PostMapping("/getQfModelList")
    @Operation(summary = "查询流程模型列表")
    public PageResult<GetQfModelListVo> getQfModelList(@RequestBody @Valid GetQfModelListDto dto) throws Exception {
        return qfModelService.getQfModelList(dto);
    }

    @PreAuthorize("@auth.hasCode('qf:model:add')")
    @Operation(summary = "新增流程模型")
    @PostMapping("/addQfModel")
    public Result<String> addQfModel(@RequestBody @Valid AddQfModelDto dto) throws Exception {
        qfModelService.addQfModel(dto);
        return Result.success("新增成功");
    }

    @PreAuthorize("@auth.hasCode('qf:model:edit')")
    @Operation(summary = "编辑流程模型")
    @PostMapping("/editQfModel")
    public Result<String> editQfModel(@RequestBody @Valid EditQfModelDto dto) throws Exception {
        qfModelService.editQfModel(dto);
        return Result.success("修改成功");
    }

    @PreAuthorize("@auth.hasCode('qf:model:view')")
    @Operation(summary = "查询流程模型详情")
    @PostMapping("/getQfModelDetails")
    public Result<GetQfModelDetailsVo> getQfModelDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetQfModelDetailsVo details = qfModelService.getQfModelDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @PreAuthorize("@auth.hasCode('qf:model:remove')")
    @Operation(summary = "删除流程模型")
    @PostMapping("/removeQfModel")
    public Result<String> removeQfModel(@RequestBody @Valid CommonIdDto dto) throws Exception {

        //不支持批量删除
        if (dto.isBatch()) {
            return Result.error("不支持批量删除");
        }

        qfModelService.removeQfModel(dto);
        return Result.success("操作成功");
    }

}
