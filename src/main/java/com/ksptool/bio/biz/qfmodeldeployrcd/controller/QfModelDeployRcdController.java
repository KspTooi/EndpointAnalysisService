package com.ksptool.bio.biz.qfmodeldeployrcd.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.ksptool.bio.biz.qfmodeldeployrcd.service.QfModelDeployRcdService;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.dto.GetQfModelDeployRcdListDto;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.vo.GetQfModelDeployRcdListVo;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.vo.GetQfModelDeployRcdDetailsVo;


@RestController
@RequestMapping("/qfModelDeployRcd")
@Tag(name = "QF流程模型部署历史管理", description = "QF流程模型部署历史管理")
@Slf4j
public class QfModelDeployRcdController {

    @Autowired
    private QfModelDeployRcdService qfModelDeployRcdService;

    @PostMapping("/getQfModelDeployRcdList")
    @Operation(summary ="查询流程模型部署历史列表")
    public PageResult<GetQfModelDeployRcdListVo> getQfModelDeployRcdList(@RequestBody @Valid GetQfModelDeployRcdListDto dto) throws Exception{
        return qfModelDeployRcdService.getQfModelDeployRcdList(dto);
    }

    @Operation(summary ="查询流程模型部署历史详情")
    @PostMapping("/getQfModelDeployRcdDetails")
    public Result<GetQfModelDeployRcdDetailsVo> getQfModelDeployRcdDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetQfModelDeployRcdDetailsVo details = qfModelDeployRcdService.getQfModelDeployRcdDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除流程模型部署历史")
    @PostMapping("/removeQfModelDeployRcd")
    public Result<String> removeQfModelDeployRcd(@RequestBody @Valid CommonIdDto dto) throws Exception{
        qfModelDeployRcdService.removeQfModelDeployRcd(dto);
        return Result.success("操作成功");
    }

}
