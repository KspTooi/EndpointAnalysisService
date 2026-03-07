package com.ksptool.bio.biz.gen.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.gen.model.outblueprint.dto.AddOutBlueprintDto;
import com.ksptool.bio.biz.gen.model.outblueprint.dto.EditOutBlueprintDto;
import com.ksptool.bio.biz.gen.model.outblueprint.dto.GetOutBlueprintListDto;
import com.ksptool.bio.biz.gen.model.outblueprint.vo.GetOutBlueprintDetailsVo;
import com.ksptool.bio.biz.gen.model.outblueprint.vo.GetOutBlueprintListVo;
import com.ksptool.bio.biz.gen.service.OutBlueprintService;
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
@RequestMapping("/outBlueprint")
@Tag(name = "输出蓝图管理", description = "输出蓝图管理")
@Slf4j
public class OutBlueprintController {

    @Autowired
    private OutBlueprintService outBlueprintService;

    @PostMapping("/getOutBlueprintList")
    @Operation(summary = "查询输出蓝图列表")
    public PageResult<GetOutBlueprintListVo> getOutBlueprintList(@RequestBody @Valid GetOutBlueprintListDto dto) throws Exception {
        return outBlueprintService.getOutBlueprintList(dto);
    }

    @Operation(summary = "新增输出蓝图")
    @PostMapping("/addOutBlueprint")
    public Result<String> addOutBlueprint(@RequestBody @Valid AddOutBlueprintDto dto) throws Exception {
        outBlueprintService.addOutBlueprint(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "编辑输出蓝图")
    @PostMapping("/editOutBlueprint")
    public Result<String> editOutBlueprint(@RequestBody @Valid EditOutBlueprintDto dto) throws Exception {
        outBlueprintService.editOutBlueprint(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "查询输出蓝图详情")
    @PostMapping("/getOutBlueprintDetails")
    public Result<GetOutBlueprintDetailsVo> getOutBlueprintDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetOutBlueprintDetailsVo details = outBlueprintService.getOutBlueprintDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除输出蓝图")
    @PostMapping("/removeOutBlueprint")
    public Result<String> removeOutBlueprint(@RequestBody @Valid CommonIdDto dto) throws Exception {
        outBlueprintService.removeOutBlueprint(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "测试SCM连接")
    @PostMapping("/testScmConnection")
    public Result<String> testScmConnection(@RequestBody @Valid CommonIdDto dto) throws Exception {
        outBlueprintService.testScmConnection(dto);
        return Result.success("操作成功");
    }

}
