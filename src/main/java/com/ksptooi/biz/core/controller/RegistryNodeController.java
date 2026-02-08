package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.registrynode.dto.AddRegistryNodeDto;
import com.ksptooi.biz.core.model.registrynode.dto.EditRegistryNodeDto;
import com.ksptooi.biz.core.model.registrynode.dto.GetRegistryNodeListDto;
import com.ksptooi.biz.core.model.registrynode.vo.GetRegistryNodeDetailsVo;
import com.ksptooi.biz.core.model.registrynode.vo.GetRegistryNodeListVo;
import com.ksptooi.biz.core.service.RegistryNodeService;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
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


@RestController
@RequestMapping("/registry")
@Tag(name = "registry", description = "注册表")
@Slf4j
public class RegistryNodeController {

    @Autowired
    private RegistryNodeService registryNodeService;

    @PostMapping("/getRegistryNodeList")
    @Operation(summary = "列表查询")
    public PageResult<GetRegistryNodeListVo> getRegistryNodeList(@RequestBody @Valid GetRegistryNodeListDto dto) throws Exception {
        return registryNodeService.getRegistryNodeList(dto);
    }

    @Operation(summary = "新增注册表")
    @PostMapping("/addRegistryNode")
    public Result<String> addRegistryNode(@RequestBody @Valid AddRegistryNodeDto dto) throws Exception {

        //验证输入参数
        String validate = dto.validate();

        if (StringUtils.isNotBlank(validate)) {
            return Result.error(validate);
        }


        registryNodeService.addRegistryNode(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑注册表")
    @PostMapping("/editRegistryNode")
    public Result<String> editRegistryNode(@RequestBody @Valid EditRegistryNodeDto dto) throws Exception {

        //验证输入参数
        String validate = dto.validate();
        if (StringUtils.isNotBlank(validate)) {
            return Result.error(validate);
        }

        registryNodeService.editRegistryNode(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询注册表详情")
    @PostMapping("/getRegistryNodeDetails")
    public Result<GetRegistryNodeDetailsVo> getRegistryDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetRegistryNodeDetailsVo details = registryNodeService.getRegistryNodeDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除注册表")
    @PostMapping("/removeRegistryNode")
    public Result<String> removeRegistryNode(@RequestBody @Valid CommonIdDto dto) throws Exception {
        registryNodeService.removeRegistryNode(dto);
        return Result.success("操作成功");
    }

}
