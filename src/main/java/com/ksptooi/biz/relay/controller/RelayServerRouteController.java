package com.ksptooi.biz.relay.controller;

import com.ksptooi.biz.relay.model.relayserverroute.dto.AddRelayServerRouteDto;
import com.ksptooi.biz.relay.model.relayserverroute.dto.EditRelayServerRouteDto;
import com.ksptooi.biz.relay.model.relayserverroute.dto.GetRelayServerRouteListDto;
import com.ksptooi.biz.relay.model.relayserverroute.vo.GetRelayServerRouteDetailsVo;
import com.ksptooi.biz.relay.model.relayserverroute.vo.GetRelayServerRouteListVo;
import com.ksptooi.biz.relay.service.RelayServerRouteService;
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
@RequestMapping("/relayServerRoute")
@Tag(name = "RelayServerRoute", description = "")
@Slf4j
public class RelayServerRouteController {

    @Autowired
    private RelayServerRouteService relayServerRouteService;

    @PostMapping("/getRelayServerRouteList")
    @Operation(summary = "列表查询")
    public PageResult<GetRelayServerRouteListVo> getRelayServerRouteList(@RequestBody @Valid GetRelayServerRouteListDto dto) throws Exception {
        return relayServerRouteService.getRelayServerRouteList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addRelayServerRoute")
    public Result<String> addRelayServerRoute(@RequestBody @Valid AddRelayServerRouteDto dto) throws Exception {
        relayServerRouteService.addRelayServerRoute(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editRelayServerRoute")
    public Result<String> editRelayServerRoute(@RequestBody @Valid EditRelayServerRouteDto dto) throws Exception {
        relayServerRouteService.editRelayServerRoute(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getRelayServerRouteDetails")
    public Result<GetRelayServerRouteDetailsVo> getRelayServerRouteDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetRelayServerRouteDetailsVo details = relayServerRouteService.getRelayServerRouteDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeRelayServerRoute")
    public Result<String> removeRelayServerRoute(@RequestBody @Valid CommonIdDto dto) throws Exception {
        relayServerRouteService.removeRelayServerRoute(dto);
        return Result.success("操作成功");
    }

}
