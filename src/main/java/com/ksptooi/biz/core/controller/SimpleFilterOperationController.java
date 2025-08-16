package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.filter.dto.AddSimpleFilterOperationDto;
import com.ksptooi.biz.core.model.filter.dto.EditSimpleFilterOperationDto;
import com.ksptooi.biz.core.model.filter.dto.GetSimpleFilterOperationListDto;
import com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterOperationDetailsVo;
import com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterOperationListVo;
import com.ksptooi.biz.core.service.SimpleFilterOperationService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import com.ksptooi.commons.utils.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PrintLog
@RestController
@RequestMapping("/simpleFilterOperation")
@Tag(name = "SimpleFilterOperation", description = "${comments}")
@Slf4j
public class SimpleFilterOperationController {

    @Autowired
    private SimpleFilterOperationService simpleFilterOperationService;

    @PostMapping("/getSimpleFilterOperationList")
    @Operation(summary = "列表查询")
    public PageResult<GetSimpleFilterOperationListVo> getSimpleFilterOperationList(@RequestBody @Valid GetSimpleFilterOperationListDto dto) throws Exception {
        return simpleFilterOperationService.getSimpleFilterOperationList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addSimpleFilterOperation")
    public Result<String> addSimpleFilterOperation(@RequestBody @Valid AddSimpleFilterOperationDto dto) throws Exception {
        simpleFilterOperationService.addSimpleFilterOperation(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editSimpleFilterOperation")
    public Result<String> editSimpleFilterOperation(@RequestBody @Valid EditSimpleFilterOperationDto dto) throws Exception {
        simpleFilterOperationService.editSimpleFilterOperation(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getSimpleFilterOperationDetails")
    public Result<GetSimpleFilterOperationDetailsVo> getSimpleFilterOperationDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetSimpleFilterOperationDetailsVo details = simpleFilterOperationService.getSimpleFilterOperationDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeSimpleFilterOperation")
    public Result<String> removeSimpleFilterOperation(@RequestBody @Valid CommonIdDto dto) throws Exception {
        simpleFilterOperationService.removeSimpleFilterOperation(dto);
        return Result.success("操作成功");
    }

}
