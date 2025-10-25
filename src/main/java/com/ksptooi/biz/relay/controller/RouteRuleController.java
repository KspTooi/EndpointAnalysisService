package com.ksptooi.biz.relay.controller;

import com.ksptooi.biz.relay.model.routerule.dto.AddRouteRuleDto;
import com.ksptooi.biz.relay.model.routerule.dto.EditRouteRuleDto;
import com.ksptooi.biz.relay.model.routerule.dto.GetRouteRuleListDto;
import com.ksptooi.biz.relay.model.routerule.vo.GetRouteRuleDetailsVo;
import com.ksptooi.biz.relay.model.routerule.vo.GetRouteRuleListVo;
import com.ksptooi.biz.relay.service.RouteRuleService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import com.ksptooi.commons.utils.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    @Operation(summary = "列表查询路由规则列表")
    public PageResult<GetRouteRuleListVo> getRouteRuleList(@RequestBody @Valid GetRouteRuleListDto dto) throws Exception {
        return routeRuleService.getRouteRuleList(dto);
    }

    @Operation(summary = "新增路由规则")
    @PostMapping("/addRouteRule")
    public Result<String> addRouteRule(@RequestBody @Valid AddRouteRuleDto dto) throws Exception {

        //验证参数
        String validate = dto.validate();

        if (StringUtils.isNotBlank(validate)) {
            return Result.error(validate);
        }

        routeRuleService.addRouteRule(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑路由规则")
    @PostMapping("/editRouteRule")
    public Result<String> editRouteRule(@RequestBody @Valid EditRouteRuleDto dto) throws Exception {

        //验证参数
        String validate = dto.validate();

        if (StringUtils.isNotBlank(validate)) {
            return Result.error(validate);
        }

        routeRuleService.editRouteRule(dto);
        return Result.success("编辑成功");
    }

    @Operation(summary = "查询路由规则详情")
    @PostMapping("/getRouteRuleDetails")
    public Result<GetRouteRuleDetailsVo> getRouteRuleDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetRouteRuleDetailsVo details = routeRuleService.getRouteRuleDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除路由规则")
    @PostMapping("/removeRouteRule")
    public Result<String> removeRouteRule(@RequestBody @Valid CommonIdDto dto) throws Exception {
        routeRuleService.removeRouteRule(dto);
        return Result.success("操作成功");
    }

}
