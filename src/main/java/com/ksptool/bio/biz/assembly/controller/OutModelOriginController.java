package com.ksptool.bio.biz.assembly.controller;

import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.assembly.model.rawmodel.dto.GetRawModelDto;
import com.ksptool.bio.biz.assembly.model.rawmodel.vo.GetRawModelListVo;
import com.ksptool.bio.biz.assembly.service.OutModelOriginService;
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
@RequestMapping("/outModelOrigin")
@Tag(name = "代码装配-输出方案原始模型管理", description = "输出方案原始模型管理")
@Slf4j
public class OutModelOriginController {

    @Autowired
    private OutModelOriginService outModelOriginService;

    @PostMapping("/getOutModelOriginList")
    @Operation(summary = "查询原始模型列表")
    public PageResult<GetRawModelListVo> getOutModelOriginList(@RequestBody @Valid GetRawModelDto dto) throws Exception {
        return outModelOriginService.getOutModelOriginList(dto);
    }

}
