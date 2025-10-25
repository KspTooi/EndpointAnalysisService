package com.ksptooi.biz.relay.controller;

import com.ksptooi.biz.relay.model.routeserver.dto.AddRouteServerDto;
import com.ksptooi.biz.relay.model.routeserver.dto.EditRouteServerDto;
import com.ksptooi.biz.relay.model.routeserver.dto.GetRouteServerListDto;
import com.ksptooi.biz.relay.model.routeserver.vo.GetRouteServerDetailsVo;
import com.ksptooi.biz.relay.model.routeserver.vo.GetRouteServerListVo;
import com.ksptooi.biz.relay.service.RouteServerService;
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
@RequestMapping("/routeServer")
@Tag(name = "RouteServer", description = "路由服务器")
@Slf4j
public class RouteServerController {

    @Autowired
    private RouteServerService routeServerService;

    @PostMapping("/getRouteServerList")
    @Operation(summary = "列表查询")
    public PageResult<GetRouteServerListVo> getRouteServerList(@RequestBody @Valid GetRouteServerListDto dto) throws Exception {
        return routeServerService.getRouteServerList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addRouteServer")
    public Result<String> addRouteServer(@RequestBody @Valid AddRouteServerDto dto) throws Exception {
        routeServerService.addRouteServer(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editRouteServer")
    public Result<String> editRouteServer(@RequestBody @Valid EditRouteServerDto dto) throws Exception {
        routeServerService.editRouteServer(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getRouteServerDetails")
    public Result<GetRouteServerDetailsVo> getRouteServerDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetRouteServerDetailsVo details = routeServerService.getRouteServerDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeRouteServer")
    public Result<String> removeRouteServer(@RequestBody @Valid CommonIdDto dto) throws Exception {
        routeServerService.removeRouteServer(dto);
        return Result.success("操作成功");
    }

}
