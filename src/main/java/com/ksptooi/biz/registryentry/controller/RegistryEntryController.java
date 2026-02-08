package com.ksptooi.biz.registryentry.controller;

import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptooi.biz.registryentry.service.RegistryEntryService;
import com.ksptooi.biz.registryentry.model.dto.AddRegistryEntryDto;
import com.ksptooi.biz.registryentry.model.dto.EditRegistryEntryDto;
import com.ksptooi.biz.registryentry.model.dto.GetRegistryEntryListDto;
import com.ksptooi.biz.registryentry.model.vo.GetRegistryEntryListVo;
import com.ksptooi.biz.registryentry.model.vo.GetRegistryEntryDetailsVo;


@RestController
@RequestMapping("/registryEntry")
@Tag(name = "registryEntry", description = "注册表条目")
@Slf4j
public class RegistryEntryController {

    @Autowired
    private RegistryEntryService registryEntryService;

    @PostMapping("/getRegistryEntryList")
    @Operation(summary ="列表查询")
    public PageResult<GetRegistryEntryListVo> getRegistryEntryList(@RequestBody @Valid GetRegistryEntryListDto dto) throws Exception{
        return registryEntryService.getRegistryEntryList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addRegistryEntry")
    public Result<String> addRegistryEntry(@RequestBody @Valid AddRegistryEntryDto dto) throws Exception{
		registryEntryService.addRegistryEntry(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editRegistryEntry")
    public Result<String> editRegistryEntry(@RequestBody @Valid EditRegistryEntryDto dto) throws Exception{
		registryEntryService.editRegistryEntry(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getRegistryEntryDetails")
    public Result<GetRegistryEntryDetailsVo> getRegistryEntryDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetRegistryEntryDetailsVo details = registryEntryService.getRegistryEntryDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeRegistryEntry")
    public Result<String> removeRegistryEntry(@RequestBody @Valid CommonIdDto dto) throws Exception{
        registryEntryService.removeRegistryEntry(dto);
        return Result.success("操作成功");
    }

}
