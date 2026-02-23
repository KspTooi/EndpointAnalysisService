package com.ksptool.bio.biz.zremovedauthgroup.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

zremovedauthgroup.service.ZremovedAuthGroupService;
zremovedauthgroup.model.dto.AddZremovedAuthGroupDto;
zremovedauthgroup.model.dto.EditZremovedAuthGroupDto;
zremovedauthgroup.model.dto.GetZremovedAuthGroupListDto;
zremovedauthgroup.model.vo.GetZremovedAuthGroupListVo;zremovedauthgroup.model.vo.GetZremovedAuthGroupDetailsVo;


@RestController
@RequestMapping("/zremovedAuthGroup")
@Tag(name = "zremovedAuthGroup", description = "用户组")
@Slf4j
public class ZremovedAuthGroupController {

    @Autowired
    private ZremovedAuthGroupService zremovedAuthGroupService;

    @PostMapping("/getZremovedAuthGroupList")
    @Operation(summary ="列表查询")
    public PageResult<GetZremovedAuthGroupListVo> getZremovedAuthGroupList(@RequestBody @Valid GetZremovedAuthGroupListDto dto) throws Exception{
        return zremovedAuthGroupService.getZremovedAuthGroupList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addZremovedAuthGroup")
    public Result<String> addZremovedAuthGroup(@RequestBody @Valid AddZremovedAuthGroupDto dto) throws Exception{
		zremovedAuthGroupService.addZremovedAuthGroup(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editZremovedAuthGroup")
    public Result<String> editZremovedAuthGroup(@RequestBody @Valid EditZremovedAuthGroupDto dto) throws Exception{
		zremovedAuthGroupService.editZremovedAuthGroup(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getZremovedAuthGroupDetails")
    public Result<GetZremovedAuthGroupDetailsVo> getZremovedAuthGroupDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetZremovedAuthGroupDetailsVo details = zremovedAuthGroupService.getZremovedAuthGroupDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeZremovedAuthGroup")
    public Result<String> removeZremovedAuthGroup(@RequestBody @Valid CommonIdDto dto) throws Exception{
        zremovedAuthGroupService.removeZremovedAuthGroup(dto);
        return Result.success("操作成功");
    }

}
