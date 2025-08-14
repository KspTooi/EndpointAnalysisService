package com.ksptooi.biz.simplefilter.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageableResult;
import com.ksptooi.commons.utils.web.Result;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.biz.simplefilter.service.SimpleFilterService;
import com.ksptooi.biz.simplefilter.model.vo.GetSimpleFilterListVo;
import com.ksptooi.biz.simplefilter.model.dto.GetSimpleFilterListDto;
import com.ksptooi.biz.simplefilter.model.vo.GetSimpleFilterDetailsVo;
import com.ksptooi.biz.simplefilter.model.dto.EditSimpleFilterDto;
import com.ksptooi.biz.simplefilter.model.dto.AddSimpleFilterDto;
import com.ksptooi.commons.utils.web.PageResult;

@PrintLog
@RestController
@RequestMapping("/simpleFilter")
@Tag(name = "SimpleFilter", description = "过滤器表")
@Slf4j
public class SimpleFilterController {

    @Autowired
    private SimpleFilterService simpleFilterService;

    @PostMapping("/getSimpleFilterList")
    @Operation(summary ="列表查询")
    public PageResult<GetSimpleFilterListVo> getSimpleFilterList(@RequestBody @Valid GetSimpleFilterListDto dto) throws Exception{
        return simpleFilterService.getSimpleFilterList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addSimpleFilter")
    public Result<String> addSimpleFilter(@RequestBody @Valid AddSimpleFilterDto dto) throws Exception{
        simpleFilterService.addSimpleFilter(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editSimpleFilter")
    public Result<String> editSimpleFilter(@RequestBody @Valid EditSimpleFilterDto dto) throws Exception{
        simpleFilterService.editSimpleFilter(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getSimpleFilterDetails")
    public Result<GetSimpleFilterDetailsVo> getSimpleFilterDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetSimpleFilterDetailsVo details = simpleFilterService.getSimpleFilterDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeSimpleFilter")
    public Result<String> removeSimpleFilter(@RequestBody @Valid CommonIdDto dto) throws Exception{
        simpleFilterService.removeSimpleFilter(dto);
        return Result.success("操作成功");
    }

}
