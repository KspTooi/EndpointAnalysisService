package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.routegroup.dto.AddRouteGroupDto;
import com.ksptooi.biz.core.model.routegroup.dto.EditRouteGroupDto;
import com.ksptooi.biz.core.model.routegroup.dto.GetRouteGroupListDto;
import com.ksptooi.biz.core.model.routegroup.vo.GetRouteGroupDetailsVo;
import com.ksptooi.biz.core.model.routegroup.vo.GetRouteGroupListVo;
import com.ksptooi.biz.core.service.RouteGroupService;
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
@RequestMapping("/routeGroup")
@Tag(name = "RouteGroup", description = "路由策略组")
@Slf4j
public class RouteGroupController {

    @Autowired
    private RouteGroupService routeGroupService;

    @PostMapping("/getRouteGroupList")
    @Operation(summary = "列表查询")
    public PageResult<GetRouteGroupListVo> getRouteGroupList(@RequestBody @Valid GetRouteGroupListDto dto) throws Exception {
        return routeGroupService.getRouteGroupList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addRouteGroup")
    public Result<String> addRouteGroup(@RequestBody @Valid AddRouteGroupDto dto) throws Exception {
        routeGroupService.addRouteGroup(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editRouteGroup")
    public Result<String> editRouteGroup(@RequestBody @Valid EditRouteGroupDto dto) throws Exception {
        routeGroupService.editRouteGroup(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getRouteGroupDetails")
    public Result<GetRouteGroupDetailsVo> getRouteGroupDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetRouteGroupDetailsVo details = routeGroupService.getRouteGroupDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeRouteGroup")
    public Result<String> removeRouteGroup(@RequestBody @Valid CommonIdDto dto) throws Exception {
        routeGroupService.removeRouteGroup(dto);
        return Result.success("操作成功");
    }

}
