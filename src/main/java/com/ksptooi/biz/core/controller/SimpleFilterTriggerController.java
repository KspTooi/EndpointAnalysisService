package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.filter.dto.AddSimpleFilterTriggerDto;
import com.ksptooi.biz.core.model.filter.dto.EditSimpleFilterTriggerDto;
import com.ksptooi.biz.core.model.filter.dto.GetSimpleFilterTriggerListDto;
import com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterTriggerDetailsVo;
import com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterTriggerListVo;
import com.ksptooi.biz.core.service.SimpleFilterTriggerService;
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
@RequestMapping("/simpleFilterTrigger")
@Tag(name = "SimpleFilterTrigger", description = "${comments}")
@Slf4j
public class SimpleFilterTriggerController {

    @Autowired
    private SimpleFilterTriggerService simpleFilterTriggerService;

    @PostMapping("/getSimpleFilterTriggerList")
    @Operation(summary = "列表查询")
    public PageResult<GetSimpleFilterTriggerListVo> getSimpleFilterTriggerList(@RequestBody @Valid GetSimpleFilterTriggerListDto dto) throws Exception {
        return simpleFilterTriggerService.getSimpleFilterTriggerList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addSimpleFilterTrigger")
    public Result<String> addSimpleFilterTrigger(@RequestBody @Valid AddSimpleFilterTriggerDto dto) throws Exception {
        simpleFilterTriggerService.addSimpleFilterTrigger(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editSimpleFilterTrigger")
    public Result<String> editSimpleFilterTrigger(@RequestBody @Valid EditSimpleFilterTriggerDto dto) throws Exception {
        simpleFilterTriggerService.editSimpleFilterTrigger(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getSimpleFilterTriggerDetails")
    public Result<GetSimpleFilterTriggerDetailsVo> getSimpleFilterTriggerDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetSimpleFilterTriggerDetailsVo details = simpleFilterTriggerService.getSimpleFilterTriggerDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeSimpleFilterTrigger")
    public Result<String> removeSimpleFilterTrigger(@RequestBody @Valid CommonIdDto dto) throws Exception {
        simpleFilterTriggerService.removeSimpleFilterTrigger(dto);
        return Result.success("操作成功");
    }

}
