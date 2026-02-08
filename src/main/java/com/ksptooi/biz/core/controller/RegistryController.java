package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.registry.dto.AddRegistryDto;
import com.ksptooi.biz.core.model.registry.dto.EditRegistryDto;
import com.ksptooi.biz.core.model.registry.dto.GetRegistryListDto;
import com.ksptooi.biz.core.model.registry.vo.GetRegistryDetailsVo;
import com.ksptooi.biz.core.model.registry.vo.GetRegistryListVo;
import com.ksptooi.biz.core.service.RegistryService;
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
@RequestMapping("/registry")
@Tag(name = "registry", description = "注册表节点")
@Slf4j
public class RegistryController {

    @Autowired
    private RegistryService registryService;

    @PostMapping("/getRegistryNodeList")
    @Operation(summary = "列表查询注册表节点列表")
    public PageResult<GetRegistryListVo> getRegistryNodeList(@RequestBody @Valid GetRegistryListDto dto)
            throws Exception {
        return registryService.getRegistryNodeList(dto);
    }

    @Operation(summary = "新增注册表节点")
    @PostMapping("/addRegistryNode")
    public Result<String> addRegistryNode(@RequestBody @Valid AddRegistryDto dto) throws Exception {

        registryService.addRegistryNode(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑注册表节点")
    @PostMapping("/editRegistryNode")
    public Result<String> editRegistryNode(@RequestBody @Valid EditRegistryDto dto) throws Exception {

        registryService.editRegistryNode(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询注册表节点详情")
    @PostMapping("/getRegistryNodeDetails")
    public Result<GetRegistryDetailsVo> getRegistryDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetRegistryDetailsVo details = registryService.getRegistryNodeDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除注册表节点")
    @PostMapping("/removeRegistryNode")
    public Result<String> removeRegistryNode(@RequestBody @Valid CommonIdDto dto) throws Exception {
        registryService.removeRegistryNode(dto);
        return Result.success("操作成功");
    }

}
