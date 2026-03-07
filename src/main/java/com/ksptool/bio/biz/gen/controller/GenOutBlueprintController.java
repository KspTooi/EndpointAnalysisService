package com.ksptool.bio.biz.gen.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.gen.model.outblueprint.dto.AddGenOutBlueprintDto;
import com.ksptool.bio.biz.gen.model.outblueprint.dto.EditGenOutBlueprintDto;
import com.ksptool.bio.biz.gen.model.outblueprint.dto.GetGenOutBlueprintListDto;
import com.ksptool.bio.biz.gen.model.outblueprint.vo.GetGenOutBlueprintDetailsVo;
import com.ksptool.bio.biz.gen.model.outblueprint.vo.GetGenOutBlueprintListVo;
import com.ksptool.bio.biz.gen.service.GenOutBlueprintService;
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
@RequestMapping("/genOutBlueprint")
@Tag(name = "genOutBlueprint", description = "输出蓝图表")
@Slf4j
public class GenOutBlueprintController {

    @Autowired
    private GenOutBlueprintService genOutBlueprintService;

    @PostMapping("/getGenOutBlueprintList")
    @Operation(summary = "列表查询")
    public PageResult<GetGenOutBlueprintListVo> getGenOutBlueprintList(@RequestBody @Valid GetGenOutBlueprintListDto dto) throws Exception {
        return genOutBlueprintService.getGenOutBlueprintList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addGenOutBlueprint")
    public Result<String> addGenOutBlueprint(@RequestBody @Valid AddGenOutBlueprintDto dto) throws Exception {
        genOutBlueprintService.addGenOutBlueprint(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editGenOutBlueprint")
    public Result<String> editGenOutBlueprint(@RequestBody @Valid EditGenOutBlueprintDto dto) throws Exception {
        genOutBlueprintService.editGenOutBlueprint(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getGenOutBlueprintDetails")
    public Result<GetGenOutBlueprintDetailsVo> getGenOutBlueprintDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetGenOutBlueprintDetailsVo details = genOutBlueprintService.getGenOutBlueprintDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeGenOutBlueprint")
    public Result<String> removeGenOutBlueprint(@RequestBody @Valid CommonIdDto dto) throws Exception {
        genOutBlueprintService.removeGenOutBlueprint(dto);
        return Result.success("操作成功");
    }

}
