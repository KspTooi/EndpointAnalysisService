package com.ksptooi.biz.drive.controller;

import com.ksptooi.biz.drive.model.dto.*;
import com.ksptooi.biz.drive.model.vo.CheckEntryMoveVo;
import com.ksptooi.biz.drive.model.vo.GetDriveInfo;
import com.ksptooi.biz.drive.model.vo.GetEntryDetailsVo;
import com.ksptooi.biz.drive.model.vo.GetEntryListVo;
import com.ksptooi.biz.drive.service.EntryService;
import com.ksptooi.commons.annotation.PrintLog;
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

@PrintLog
@RestController
@RequestMapping("/drive/entry")
@Tag(name = "Entry", description = "团队云盘接口")
@Slf4j
public class EntryController {

    @Autowired
    private EntryService entryService;

    @PostMapping("/getDriveInfo")
    @Operation(summary = "获取云盘信息")
    public Result<GetDriveInfo> getDriveInfo() throws Exception {
        return Result.success(entryService.getDriveInfo());
    }

    @PostMapping("/getEntryList")
    @Operation(summary = "查询条目列表")
    public Result<GetEntryListVo> getEntryList(@RequestBody @Valid GetEntryListDto dto) throws Exception {
        var ret = entryService.getEntryList(dto);
        return Result.success(ret);
    }

    @Operation(summary = "新增条目")
    @PostMapping("/addEntry")
    public Result<String> addEntry(@RequestBody @Valid AddEntryDto dto) throws Exception {

        //验证输入参数
        if (dto.validate() != null) {
            return Result.error(dto.validate());
        }

        //新增条目
        entryService.addEntry(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "复制条目")
    @PostMapping("/copyEntry")
    public Result<String> copyEntry(@RequestBody @Valid CopyEntryDto dto) throws Exception {
        entryService.copyEntry(dto);
        return Result.success("复制成功");
    }

    @Operation(summary = "重命名条目")
    @PostMapping("/renameEntry")
    public Result<String> renameEntry(@RequestBody @Valid RenameEntry dto) throws Exception {
        entryService.renameEntry(dto);
        return Result.success("重命名成功");
    }

    @Operation(summary = "移动检测")
    @PostMapping("/checkEntryMove")
    public Result<CheckEntryMoveVo> checkEntryMove(@RequestBody @Valid MoveEntryDto dto) throws Exception {
        var ret = entryService.checkEntryMove(dto);
        return Result.success(ret);
    }


    @Operation(summary = "移动条目")
    @PostMapping("/moveEntry")
    public Result<String> moveEntry(@RequestBody @Valid MoveEntryDto dto) throws Exception {
        entryService.moveEntry(dto);
        return Result.success("移动成功");
    }

    @Operation(summary = "查询条目详情")
    @PostMapping("/getEntryDetails")
    public Result<GetEntryDetailsVo> getEntryDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetEntryDetailsVo details = entryService.getEntryDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除条目")
    @PostMapping("/removeEntry")
    public Result<String> removeEntry(@RequestBody @Valid CommonIdDto dto) throws Exception {
        entryService.removeEntry(dto);
        return Result.success("操作成功");
    }
}
