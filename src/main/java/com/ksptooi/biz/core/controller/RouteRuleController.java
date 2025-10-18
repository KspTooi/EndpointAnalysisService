package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.routerule.dto.AddRouteRuleDto;
import com.ksptooi.biz.core.model.routerule.dto.EditRouteRuleDto;
import com.ksptooi.biz.core.model.routerule.dto.GetRouteRuleListDto;
import com.ksptooi.biz.core.model.routerule.vo.GetRouteRuleDetailsVo;
import com.ksptooi.biz.core.model.routerule.vo.GetRouteRuleListVo;
import com.ksptooi.biz.core.service.RouteRuleService;
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
@RequestMapping("/routeRule")
@Tag(name = "RouteRule", description = "路由规则")
@Slf4j
public class RouteRuleController {

    @Autowired
    private RouteRuleService routeRuleService;

    @PostMapping("/getRouteRuleList")
    @Operation(summary = "列表查询")
    public PageResult<GetRouteRuleListVo> getRouteRuleList(@RequestBody @Valid GetRouteRuleListDto dto) throws Exception {
        return routeRuleService.getRouteRuleList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addRouteRule")
    public Result<String> addRouteRule(@RequestBody @Valid AddRouteRuleDto dto) throws Exception {
        routeRuleService.addRouteRule(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editRouteRule")
    public Result<String> editRouteRule(@RequestBody @Valid EditRouteRuleDto dto) throws Exception {
        routeRuleService.editRouteRule(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getRouteRuleDetails")
    public Result<GetRouteRuleDetailsVo> getRouteRuleDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetRouteRuleDetailsVo details = routeRuleService.getRouteRuleDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeRouteRule")
    public Result<String> removeRouteRule(@RequestBody @Valid CommonIdDto dto) throws Exception {
        routeRuleService.removeRouteRule(dto);
        return Result.success("操作成功");
    }

}
