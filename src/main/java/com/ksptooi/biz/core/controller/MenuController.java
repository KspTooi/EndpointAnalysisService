package com.ksptooi.biz.core.controller;


import com.ksptooi.biz.core.model.menu.dto.AddMenuDto;
import com.ksptooi.biz.core.model.menu.dto.EditMenuDto;
import com.ksptooi.biz.core.model.menu.dto.GetMenuTreeDto;
import com.ksptooi.biz.core.model.menu.vo.GetMenuDetailsVo;
import com.ksptooi.biz.core.model.menu.vo.GetMenuTreeVo;
import com.ksptooi.biz.core.service.MenuService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PrintLog
@RestController
@RequestMapping("/menu")
@Tag(name = "菜单与按钮管理", description = "菜单与按钮管理")
@Slf4j
public class MenuController {

    @Autowired
    private MenuService menuService;


    @PostMapping("/getUserMenuTree")
    @Operation(summary = "获取用户菜单与按钮树(用于前端菜单展示,这个接口带有缓存)")
    @Cacheable(cacheNames = "menuTree", key = "'userMenuTree'")
    public Result<List<GetMenuTreeVo>> getUserMenuTree() throws Exception {
        return Result.success(menuService.getMenuTree(new GetMenuTreeDto()));
    }


    @PostMapping("/getMenuTree")
    @Operation(summary = "获取菜单与按钮树(用于菜单管理)")
    public Result<List<GetMenuTreeVo>> getMenuTree(@RequestBody @Valid GetMenuTreeDto dto) throws Exception {
        return Result.success(menuService.getMenuTree(dto));
    }

    @PostMapping("/addMenu")
    @Operation(summary = "新增菜单与按钮")
    public Result<String> addMenu(@RequestBody @Valid AddMenuDto dto) throws Exception {

        //纠正输入参数
        dto.adjust();

        //验证输入参数
        if (dto.validate() != null) {
            return Result.error(dto.validate());
        }

        menuService.addMenu(dto);
        return Result.success("新增成功");
    }

    @PostMapping("/editMenu")
    @Operation(summary = "编辑菜单与按钮")
    public Result<String> editMenu(@RequestBody @Valid EditMenuDto dto) throws Exception {

        //纠正输入参数
        dto.adjust();

        //验证输入参数
        if (dto.validate() != null) {
            return Result.error(dto.validate());
        }

        menuService.editMenu(dto);
        return Result.success("编辑成功");
    }

    @PostMapping("/getMenuDetails")
    @Operation(summary = "获取菜单与按钮详情")
    public Result<GetMenuDetailsVo> getMenuDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return Result.success(menuService.getMenuDetails(dto));
    }


    @PostMapping("/removeMenu")
    @Operation(summary = "删除菜单与按钮")
    public Result<String> removeMenu(@RequestBody @Valid CommonIdDto dto) throws Exception {
        menuService.removeMenu(dto);
        return Result.success("删除成功");
    }

}
