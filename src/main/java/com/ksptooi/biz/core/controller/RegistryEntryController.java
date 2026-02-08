package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.registryentry.dto.AddRegistryEntryDto;
import com.ksptooi.biz.core.model.registryentry.dto.EditRegistryEntryDto;
import com.ksptooi.biz.core.model.registryentry.dto.GetRegistryEntryListDto;
import com.ksptooi.biz.core.model.registryentry.vo.GetRegistryEntryDetailsVo;
import com.ksptooi.biz.core.model.registryentry.vo.GetRegistryEntryListVo;
import com.ksptooi.biz.core.service.RegistryEntryService;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/registryEntry")
@Tag(name = "registryEntry", description = "注册表条目")
@Slf4j
public class RegistryEntryController {

    @Autowired
    private RegistryEntryService registryEntryService;

    @PostMapping("/getRegistryEntryList")
    @Operation(summary = "列表查询")
    public PageResult<GetRegistryEntryListVo> getRegistryEntryList(@RequestBody @Valid GetRegistryEntryListDto dto) throws Exception {
        return registryEntryService.getRegistryEntryList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addRegistryEntry")
    public Result<String> addRegistryEntry(@RequestBody @Valid AddRegistryEntryDto dto) throws Exception {
        registryEntryService.addRegistryEntry(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editRegistryEntry")
    public Result<String> editRegistryEntry(@RequestBody @Valid EditRegistryEntryDto dto) throws Exception {
        registryEntryService.editRegistryEntry(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getRegistryEntryDetails")
    public Result<GetRegistryEntryDetailsVo> getRegistryEntryDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetRegistryEntryDetailsVo details = registryEntryService.getRegistryEntryDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeRegistryEntry")
    public Result<String> removeRegistryEntry(@RequestBody @Valid CommonIdDto dto) throws Exception {
        registryEntryService.removeRegistryEntry(dto);
        return Result.success("操作成功");
    }

}
