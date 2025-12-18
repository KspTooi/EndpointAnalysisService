package com.ksptooi.biz.drive.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.biz.drive.service.EntryService;
import com.ksptooi.biz.drive.model.vo.GetEntryListVo;
import com.ksptooi.biz.drive.model.dto.GetEntryListDto;
import com.ksptooi.biz.drive.model.vo.GetEntryDetailsVo;
import com.ksptooi.biz.drive.model.dto.EditEntryDto;
import com.ksptooi.biz.drive.model.dto.AddEntryDto;

@PrintLog
@RestController
@RequestMapping("/entry")
@Tag(name = "Entry", description = "")
@Slf4j
public class EntryController {

    @Autowired
    private EntryService entryService;

    @PostMapping("/getEntryList")
    @Operation(summary ="列表查询")
    public PageResult<GetEntryListVo> getEntryList(@RequestBody @Valid GetEntryListDto dto) throws Exception{
        return entryService.getEntryList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addEntry")
    public Result<String> addEntry(@RequestBody @Valid AddEntryDto dto) throws Exception{
        entryService.addEntry(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editEntry")
    public Result<String> editEntry(@RequestBody @Valid EditEntryDto dto) throws Exception{
        entryService.editEntry(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getEntryDetails")
    public Result<GetEntryDetailsVo> getEntryDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetEntryDetailsVo details = entryService.getEntryDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeEntry")
    public Result<String> removeEntry(@RequestBody @Valid CommonIdDto dto) throws Exception{
        entryService.removeEntry(dto);
        return Result.success("操作成功");
    }

}
