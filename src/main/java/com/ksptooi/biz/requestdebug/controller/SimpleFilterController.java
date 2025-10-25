package com.ksptooi.biz.requestdebug.controller;

import com.ksptooi.biz.requestdebug.model.filter.dto.AddSimpleFilterDto;
import com.ksptooi.biz.requestdebug.model.filter.dto.EditSimpleFilterDto;
import com.ksptooi.biz.requestdebug.model.filter.dto.GetSimpleFilterListDto;
import com.ksptooi.biz.requestdebug.model.filter.vo.GetSimpleFilterDetailsVo;
import com.ksptooi.biz.requestdebug.model.filter.vo.GetSimpleFilterListVo;
import com.ksptooi.biz.requestdebug.service.SimpleFilterService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.dataprocess.Str;
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
@RequestMapping("/simpleFilter")
@Tag(name = "简单过滤器")
@Slf4j
public class SimpleFilterController {

    @Autowired
    private SimpleFilterService simpleFilterService;

    @PostMapping("/getSimpleFilterList")
    @Operation(summary = "查询简单过滤器列表")
    public PageResult<GetSimpleFilterListVo> getSimpleFilterList(@RequestBody @Valid GetSimpleFilterListDto dto) throws Exception {
        return simpleFilterService.getSimpleFilterList(dto);
    }

    @Operation(summary = "新增简单过滤器")
    @PostMapping("/addSimpleFilter")
    public Result<String> addSimpleFilter(@RequestBody @Valid AddSimpleFilterDto dto) throws Exception {

        //验证参数
        String validate = dto.validate();
        if (Str.isNotBlank(validate)) {
            return Result.error(validate);
        }

        return Result.success(simpleFilterService.addSimpleFilter(dto));
    }

    @Operation(summary = "编辑简单过滤器")
    @PostMapping("/editSimpleFilter")
    public Result<String> editSimpleFilter(@RequestBody @Valid EditSimpleFilterDto dto) throws Exception {

        //验证参数
        String validate = dto.validate();
        if (Str.isNotBlank(validate)) {
            return Result.error(validate);
        }

        simpleFilterService.editSimpleFilter(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询简单过滤器详情")
    @PostMapping("/getSimpleFilterDetails")
    public Result<GetSimpleFilterDetailsVo> getSimpleFilterDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetSimpleFilterDetailsVo details = simpleFilterService.getSimpleFilterDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除简单过滤器")
    @PostMapping("/removeSimpleFilter")
    public Result<String> removeSimpleFilter(@RequestBody @Valid CommonIdDto dto) throws Exception {
        simpleFilterService.removeSimpleFilter(dto);
        return Result.success("操作成功");
    }

}
