package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.endpoint.dto.AddEndpointDto;
import com.ksptooi.biz.core.model.endpoint.dto.EditEndpointDto;
import com.ksptooi.biz.core.model.endpoint.dto.GetEndpointTreeDto;
import com.ksptooi.biz.core.model.endpoint.vo.GetEndpointDetailsVo;
import com.ksptooi.biz.core.model.endpoint.vo.GetEndpointTreeVo;
import com.ksptooi.biz.core.service.EndpointService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PrintLog
@RestController
@RequestMapping("/endpoint")
@Tag(name = "Endpoint", description = "端点与权限")
public class EndpointController {

    @Autowired
    private EndpointService endpointService;

    @PostMapping("/getEndpointTree")
    @Operation(summary = "获取端点树")
    public Result<List<GetEndpointTreeVo>> getEndpointTree(@RequestBody GetEndpointTreeDto dto) {
        return Result.success(endpointService.getEndpointTree(dto));
    }

    @PostMapping("/addEndpoint")
    @Operation(summary = "添加端点")
    public Result<String> addEndpoint(@RequestBody @Valid AddEndpointDto dto) throws BizException {
        endpointService.addEndpoint(dto);
        return Result.success("添加成功");
    }

    @PostMapping("/editEndpoint")
    @Operation(summary = "编辑端点")
    public Result<String> editEndpoint(@RequestBody @Valid EditEndpointDto dto) throws BizException {
        endpointService.editEndpoint(dto);
        return Result.success("编辑成功");
    }

    @PostMapping("/getEndpointDetails")
    @Operation(summary = "查询端点详情")
    public Result<GetEndpointDetailsVo> getEndpointDetails(@RequestBody @Valid CommonIdDto dto) throws BizException {
        return Result.success(endpointService.getEndpointDetails(dto.getId()));
    }

    @PostMapping("/removeEndpoint")
    @Operation(summary = "删除端点")
    public Result<String> removeEndpoint(@RequestBody @Valid CommonIdDto dto) throws BizException {
        endpointService.removeEndpoint(dto);
        return Result.success("删除成功");
    }

    @PostMapping("/clearEndpointCache")
    @Operation(summary = "清空端点缓存")
    public Result<String> clearEndpointCache() {
        endpointService.clearEndpointCache();
        return Result.success("缓存已清空");
    }


}
