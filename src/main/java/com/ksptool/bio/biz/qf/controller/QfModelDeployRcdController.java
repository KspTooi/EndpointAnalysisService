package com.ksptool.bio.biz.qf.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.dto.AddQfModelDeployRcdDto;
import com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.dto.EditQfModelDeployRcdDto;
import com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.dto.GetQfModelDeployRcdListDto;
import com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.vo.GetQfModelDeployRcdDetailsVo;
import com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.vo.GetQfModelDeployRcdListVo;
import com.ksptool.bio.biz.qf.service.QfModelDeployRcdService;
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
@RequestMapping("/qfModelDeployRcd")
@Tag(name = "流程模型部署历史", description = "流程模型部署历史")
@Slf4j
public class QfModelDeployRcdController {

    @Autowired
    private QfModelDeployRcdService qfModelDeployRcdService;

    @PreAuthorize("@auth.hasCode('qf:model:deploy:view')")
    @PostMapping("/getQfModelDeployRcdList")
    @Operation(summary = "查询流程模型部署历史列表")
    public PageResult<GetQfModelDeployRcdListVo> getQfModelDeployRcdList(@RequestBody @Valid GetQfModelDeployRcdListDto dto) throws Exception {
        return qfModelDeployRcdService.getQfModelDeployRcdList(dto);
    }

    @PreAuthorize("@auth.hasCode('qf:model:deploy:add')")
    @Operation(summary = "新增流程模型部署历史")
    @PostMapping("/addQfModelDeployRcd")
    public Result<String> addQfModelDeployRcd(@RequestBody @Valid AddQfModelDeployRcdDto dto) throws Exception {
        qfModelDeployRcdService.addQfModelDeployRcd(dto);
        return Result.success("新增成功");
    }

    @PreAuthorize("@auth.hasCode('qf:model:deploy:edit')")
    @Operation(summary = "编辑流程模型部署历史")
    @PostMapping("/editQfModelDeployRcd")
    public Result<String> editQfModelDeployRcd(@RequestBody @Valid EditQfModelDeployRcdDto dto) throws Exception {
        qfModelDeployRcdService.editQfModelDeployRcd(dto);
        return Result.success("修改成功");
    }

    @PreAuthorize("@auth.hasCode('qf:model:deploy:view')")
    @Operation(summary = "查询流程模型部署历史详情")
    @PostMapping("/getQfModelDeployRcdDetails")
    public Result<GetQfModelDeployRcdDetailsVo> getQfModelDeployRcdDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetQfModelDeployRcdDetailsVo details = qfModelDeployRcdService.getQfModelDeployRcdDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @PreAuthorize("@auth.hasCode('qf:model:deploy:remove')")
    @Operation(summary = "删除流程模型部署历史")
    @PostMapping("/removeQfModelDeployRcd")
    public Result<String> removeQfModelDeployRcd(@RequestBody @Valid CommonIdDto dto) throws Exception {
        qfModelDeployRcdService.removeQfModelDeployRcd(dto);
        return Result.success("操作成功");
    }

}
