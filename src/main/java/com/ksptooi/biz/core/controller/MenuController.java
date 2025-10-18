package com.ksptooi.biz.core.controller;


import com.ksptooi.biz.core.model.menu.*;
import com.ksptooi.biz.core.model.resource.dto.AddResourceDto;
import com.ksptooi.biz.core.model.resource.dto.EditResourceDto;
import com.ksptooi.biz.core.service.ResourceService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.utils.web.CommonIdDto;
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

import java.util.List;

import static com.ksptool.entities.Entities.as;

@PrintLog
@RestController
@RequestMapping("/menu")
@Tag(name = "菜单管理", description = "菜单管理")
@Slf4j
public class MenuController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping("/getUserMenuTree")
    @Operation(summary = "获取用户菜单树")
    public Result<List<GetUserMenuTreeVo>> getUserMenuTree() throws Exception {
        return Result.success(resourceService.getUserMenuTree());
    }


    @PostMapping("/addMenu")
    @Operation(summary = "新增菜单")
    public Result<String> addMenu(@RequestBody @Valid AddMenuDto dto) throws Exception {
        var addResourceDto = new AddResourceDto();
        addResourceDto.setParentId(dto.getParentId());
        addResourceDto.setName(dto.getName());
        addResourceDto.setDescription(dto.getDescription());
        addResourceDto.setKind(0);
        addResourceDto.setMenuKind(dto.getMenuKind());
        addResourceDto.setMenuPath(dto.getMenuPath());
        addResourceDto.setMenuQueryParam(dto.getMenuQueryParam());
        addResourceDto.setMenuIcon(dto.getMenuIcon());
        addResourceDto.setPermission(dto.getPermission());
        addResourceDto.setSeq(dto.getSeq());
        resourceService.addResource(addResourceDto);
        return Result.success("新增成功");
    }

    @PostMapping("/editMenu")
    @Operation(summary = "编辑菜单")
    public Result<String> editMenu(@RequestBody @Valid EditMenuDto dto) throws Exception {
        var editResourceDto = new EditResourceDto();
        editResourceDto.setId(dto.getId());
        editResourceDto.setParentId(dto.getParentId());
        editResourceDto.setName(dto.getName());
        editResourceDto.setDescription(dto.getDescription());
        editResourceDto.setKind(0);
        editResourceDto.setMenuKind(dto.getMenuKind());
        editResourceDto.setMenuPath(dto.getMenuPath());
        editResourceDto.setMenuQueryParam(dto.getMenuQueryParam());
        editResourceDto.setMenuIcon(dto.getMenuIcon());
        editResourceDto.setPermission(dto.getPermission());
        editResourceDto.setSeq(dto.getSeq());
        resourceService.editResource(editResourceDto);
        return Result.success("编辑成功");
    }

    @PostMapping("/getMenuDetails")
    @Operation(summary = "获取菜单详情")
    public Result<GetMenuDetailsVo> getMenuDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {

        GetMenuDetailsVo vo = as(resourceService.getResourceDetails(dto), GetMenuDetailsVo.class);

        if (vo.getPermission() != null && vo.getPermission().equals("*")) {
            vo.setPermission("*");
        }

        if (vo.getParentId() == null) {
            vo.setParentId(-1L);
        }

        return Result.success(vo);
    }

    @PostMapping("/getMenuTree")
    @Operation(summary = "获取菜单树")
    public Result<List<GetMenuTreeVo>> getMenuTree(@RequestBody @Valid GetMenuTreeDto dto) throws Exception {
        return Result.success(resourceService.getMenuTree(dto));
    }

    @PostMapping("/removeMenu")
    @Operation(summary = "删除菜单")
    public Result<String> removeMenu(@RequestBody @Valid CommonIdDto dto) throws Exception {
        resourceService.removeResource(dto);
        return Result.success("删除成功");
    }

}
