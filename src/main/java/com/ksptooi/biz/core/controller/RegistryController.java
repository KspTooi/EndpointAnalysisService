package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.registry.dto.AddRegistryDto;
import com.ksptooi.biz.core.model.registry.dto.EditRegistryDto;
import com.ksptooi.biz.core.model.registry.dto.GetRegistryListDto;
import com.ksptooi.biz.core.model.registry.vo.GetRegistryDetailsVo;
import com.ksptooi.biz.core.model.registry.vo.GetRegistryEntryListVo;
import com.ksptooi.biz.core.model.registry.vo.GetRegistryNodeTreeVo;
import com.ksptooi.biz.core.service.RegistryService;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.Result;
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

import java.util.List;

@RestController
@RequestMapping("/registry")
@Tag(name = "registry", description = "注册表条目")
@Slf4j
public class RegistryController {

    @Autowired
    private RegistryService registryService;

    @PostMapping("/getRegistryNodeTree")
    @Operation(summary = "查询注册表节点树")
    public Result<List<GetRegistryNodeTreeVo>> getRegistryNodeTree() throws Exception {
        return Result.success(registryService.getRegistryNodeTree());
    }

    @PostMapping("/getRegistryEntryList")
    @Operation(summary = "查询注册表条目列表")
    public Result<List<GetRegistryEntryListVo>> getRegistryEntryList(@RequestBody @Valid GetRegistryListDto dto) throws Exception {
        return Result.success(registryService.getRegistryEntryList(dto));
    }

    @Operation(summary = "新增注册表条目")
    @PostMapping("/addRegistry")
    public Result<String> addRegistry(@RequestBody @Valid AddRegistryDto dto) throws Exception {

        //验证输入参数
        String validate = dto.validate();
        if (StringUtils.isNotBlank(validate)) {
            return Result.error(validate);
        }

        registryService.addRegistry(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑注册表条目")
    @PostMapping("/editRegistry")
    public Result<String> editRegistry(@RequestBody @Valid EditRegistryDto dto) throws Exception {

        //验证输入参数
        String validate = dto.validate();
        if (StringUtils.isNotBlank(validate)) {
            return Result.error(validate);
        }

        registryService.editRegistry(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询注册表条目详情")
    @PostMapping("/getRegistryDetails")
    public Result<GetRegistryDetailsVo> getRegistryDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetRegistryDetailsVo details = registryService.getRegistryDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除注册表条目")
    @PostMapping("/removeRegistry")
    public Result<String> removeRegistry(@RequestBody @Valid CommonIdDto dto) throws Exception {
        registryService.removeRegistry(dto);
        return Result.success("操作成功");
    }

}
