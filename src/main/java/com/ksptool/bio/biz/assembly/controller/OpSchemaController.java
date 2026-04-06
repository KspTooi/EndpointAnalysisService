package com.ksptool.bio.biz.assembly.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.assembly.model.opschema.dto.AddOpSchemaDto;
import com.ksptool.bio.biz.assembly.model.opschema.dto.EditOpSchemaDto;
import com.ksptool.bio.biz.assembly.model.opschema.dto.GetOpSchemaListDto;
import com.ksptool.bio.biz.assembly.model.opschema.vo.GetOpSchemaDetailsVo;
import com.ksptool.bio.biz.assembly.model.opschema.vo.GetOpSchemaListVo;
import com.ksptool.bio.biz.assembly.service.OpSchemaService;
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
@RequestMapping("/opSchema")
@Tag(name = "代码装配-输出方案管理", description = "输出方案管理")
@Slf4j
public class OpSchemaController {

    @Autowired
    private OpSchemaService opSchemaService;

    @PostMapping("/getOpSchemaList")
    @Operation(summary = "查询输出方案列表")
    public PageResult<GetOpSchemaListVo> getOutSchemaList(@RequestBody @Valid GetOpSchemaListDto dto) throws Exception {
        return opSchemaService.getOutSchemaList(dto);
    }

    @Operation(summary = "新增输出方案")
    @PostMapping("/addOutSchema")
    public Result<String> addOutSchema(@RequestBody @Valid AddOpSchemaDto dto) throws Exception {
        var message = opSchemaService.addOutSchema(dto);
        return Result.success(message, null);
    }

    @Operation(summary = "编辑输出方案")
    @PostMapping("/editOutSchema")
    public Result<String> editOutSchema(@RequestBody @Valid EditOpSchemaDto dto) throws Exception {
        var message = opSchemaService.editOutSchema(dto);
        return Result.success(message, null);
    }

    @Operation(summary = "查询输出方案详情")
    @PostMapping("/getOutSchemaDetails")
    public Result<GetOpSchemaDetailsVo> getOutSchemaDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetOpSchemaDetailsVo details = opSchemaService.getOutSchemaDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除输出方案")
    @PostMapping("/removeOutSchema")
    public Result<String> removeOutSchema(@RequestBody @Valid CommonIdDto dto) throws Exception {
        opSchemaService.removeOutSchema(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "预览方案参数")
    @PostMapping("/previewOutSchemaParams")
    public Result<String> sortOutSchema(@RequestBody @Valid CommonIdDto dto) throws Exception {
        opSchemaService.previewOutSchemaParams(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "执行输出方案")
    @PostMapping("/executeOutSchema")
    public Result<String> executeOutSchema(@RequestBody @Valid CommonIdDto dto) throws Exception {

        // 不支持批处理，因为执行输出方案是单个执行的
        if (dto.isBatch()) {
            return Result.error("不支持批处理");
        }

        opSchemaService.executeOutSchema(dto);
        return Result.success("操作成功");
    }
}
