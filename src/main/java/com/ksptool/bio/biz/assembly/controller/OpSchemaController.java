package com.ksptool.bio.biz.assembly.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.assembly.model.opschema.dto.AddOpSchemaDto;
import com.ksptool.bio.biz.assembly.model.opschema.dto.EditOpSchemaDto;
import com.ksptool.bio.biz.assembly.model.opschema.dto.GetOpSchemaListDto;
import com.ksptool.bio.biz.assembly.model.opschema.dto.PreviewOpBluePrintDto;
import com.ksptool.bio.biz.assembly.model.opschema.vo.GetOpBluePrintListVo;
import com.ksptool.bio.biz.assembly.model.opschema.vo.GetOpSchemaDetailsVo;
import com.ksptool.bio.biz.assembly.model.opschema.vo.GetOpSchemaListVo;
import com.ksptool.bio.biz.assembly.service.OpSchemaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
    public PageResult<GetOpSchemaListVo> getOpSchemaList(@RequestBody @Valid GetOpSchemaListDto dto) throws Exception {
        return opSchemaService.getOpSchemaList(dto);
    }

    @Operation(summary = "新增输出方案")
    @PostMapping("/addOpSchema")
    public Result<String> addOpSchema(@RequestBody @Valid AddOpSchemaDto dto) throws Exception {
        var message = opSchemaService.addOpSchema(dto);
        return Result.success(message, null);
    }

    @Operation(summary = "编辑输出方案")
    @PostMapping("/editOpSchema")
    public Result<String> editOpSchema(@RequestBody @Valid EditOpSchemaDto dto) throws Exception {
        var message = opSchemaService.editOpSchema(dto);
        return Result.success(message, null);
    }

    @Operation(summary = "查询输出方案详情")
    @PostMapping("/getOpSchemaDetails")
    public Result<GetOpSchemaDetailsVo> getOpSchemaDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetOpSchemaDetailsVo details = opSchemaService.getOpSchemaDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除输出方案")
    @PostMapping("/removeOpSchema")
    public Result<String> removeOpSchema(@RequestBody @Valid CommonIdDto dto) throws Exception {
        opSchemaService.removeOpSchema(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "查询蓝图文件列表")
    @PostMapping("/getOpBluePrintList")
    public Result<List<GetOpBluePrintListVo>> getOpBluePrintList(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return Result.success(opSchemaService.getOpBluePrintList(dto));
    }

    @Operation(summary = "预览蓝图输出")
    @PostMapping("/previewOpBluePrint")
    public Result<String> previewOpBluePrint(@RequestBody @Valid PreviewOpBluePrintDto dto) throws Exception {
        return Result.success(opSchemaService.previewOpBluePrint(dto.getOpSchemaId(),dto.getSha256Hex()));
    }


    @Operation(summary = "预览方案参数")
    @PostMapping("/previewOpSchemaParams")
    public Result<String> previewOpSchemaParams(@RequestBody @Valid CommonIdDto dto) throws Exception {
        opSchemaService.previewOpSchemaParams(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "执行输出方案")
    @PostMapping("/executeOpSchema")
    public Result<String> executeOpSchema(@RequestBody @Valid CommonIdDto dto) throws Exception {

        // 不支持批处理，因为执行输出方案是单个执行的
        if (dto.isBatch()) {
            return Result.error("不支持批处理");
        }

        opSchemaService.executeOpSchema(dto);
        return Result.success("操作成功");
    }
}
