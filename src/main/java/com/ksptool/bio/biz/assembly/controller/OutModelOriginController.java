package com.ksptool.bio.biz.assembly.controller;

import com.ksptool.assembly.entity.web.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.ksptool.bio.biz.assembly.service.OutModelOriginService;
import com.ksptool.bio.biz.assembly.model.outmodelorigin.dto.GetOutModelOriginListDto;
import com.ksptool.bio.biz.assembly.model.outmodelorigin.vo.GetOutModelOriginListVo;


@RestController
@RequestMapping("/outModelOrigin")
@Tag(name = "代码装配-输出方案原始模型管理", description = "输出方案原始模型管理")
@Slf4j
public class OutModelOriginController {

    @Autowired
    private OutModelOriginService outModelOriginService;

    @PostMapping("/getOutModelOriginList")
    @Operation(summary = "查询原始模型列表")
    public PageResult<GetOutModelOriginListVo> getOutModelOriginList(@RequestBody @Valid GetOutModelOriginListDto dto) throws Exception {
        return outModelOriginService.getOutModelOriginList(dto);
    }

}
