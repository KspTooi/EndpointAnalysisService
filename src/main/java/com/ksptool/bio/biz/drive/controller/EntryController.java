package com.ksptool.bio.biz.drive.controller;


import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.drive.common.aop.DriveSpace;
import com.ksptool.bio.biz.drive.model.driveentry.dto.*;
import com.ksptool.bio.biz.drive.model.driveentry.vo.CheckEntryMoveVo;
import com.ksptool.bio.biz.drive.model.driveentry.vo.GetDriveInfo;
import com.ksptool.bio.biz.drive.model.driveentry.vo.GetEntryDetailsVo;
import com.ksptool.bio.biz.drive.model.driveentry.vo.GetEntryListVo;
import com.ksptool.bio.biz.drive.service.EntryService;
import com.ksptool.bio.commons.annotation.PrintLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @DriveSpace
    @PreAuthorize("@auth.hasCode('drive:entry:view')")
    @PostMapping("/getDriveInfo")
    @Operation(summary = "获取云盘信息")
    public Result<GetDriveInfo> getDriveInfo() throws Exception {
        return Result.success(entryService.getDriveInfo());
    }

    @DriveSpace
    @PreAuthorize("@auth.hasCode('drive:entry:view')")
    @PostMapping("/getEntryList")
    @Operation(summary = "查询条目列表")
    public Result<GetEntryListVo> getEntryList(@RequestBody @Valid GetEntryListDto dto) throws Exception {
        var ret = entryService.getEntryList(dto);
        return Result.success(ret);
    }

    @DriveSpace
    @PreAuthorize("@auth.hasCode('drive:entry:add')")
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

    @DriveSpace
    @PreAuthorize("@auth.hasCode('drive:entry:copy')")
    @Operation(summary = "复制条目")
    @PostMapping("/copyEntry")
    public Result<String> copyEntry(@RequestBody @Valid CopyEntryDto dto) throws Exception {
        entryService.copyEntry(dto);
        return Result.success("复制成功");
    }

    @DriveSpace
    @PreAuthorize("@auth.hasCode('drive:entry:rename')")
    @Operation(summary = "重命名条目")
    @PostMapping("/renameEntry")
    public Result<String> renameEntry(@RequestBody @Valid RenameEntry dto) throws Exception {
        entryService.renameEntry(dto);
        return Result.success("重命名成功");
    }

    @DriveSpace
    @PreAuthorize("@auth.hasCode('drive:entry:move')")
    @Operation(summary = "移动检测", description = "用于移动条目前的检测")
    @PostMapping("/checkEntryMove")
    public Result<CheckEntryMoveVo> checkEntryMove(@RequestBody @Valid MoveEntryDto dto) throws Exception {
        var ret = entryService.checkEntryMove(dto);
        return Result.success(ret);
    }

    @DriveSpace
    @PreAuthorize("@auth.hasCode('drive:entry:move')")
    @Operation(summary = "移动条目", description = "允许用户在云盘内移动条目")
    @PostMapping("/moveEntry")
    public Result<String> moveEntry(@RequestBody @Valid MoveEntryDto dto) throws Exception {
        entryService.moveEntry(dto);
        return Result.success("移动成功");
    }

    @PreAuthorize("@auth.hasCode('drive:entry:view')")
    @Operation(summary = "查询条目详情")
    @PostMapping("/getEntryDetails")
    public Result<GetEntryDetailsVo> getEntryDetails(@RequestBody @Valid DriveCommonIdDto dto) throws Exception {

        GetEntryDetailsVo details = entryService.getEntryDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }

        return Result.success(details);
    }

    @DriveSpace
    @PreAuthorize("@auth.hasCode('drive:entry:remove')")
    @Operation(summary = "删除条目")
    @PostMapping("/removeEntry")
    public Result<String> removeEntry(@RequestBody @Valid DriveCommonIdDto dto) throws Exception {
        entryService.removeEntry(dto);
        return Result.success("操作成功");
    }
}
