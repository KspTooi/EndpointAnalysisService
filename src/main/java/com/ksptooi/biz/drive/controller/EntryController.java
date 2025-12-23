package com.ksptooi.biz.drive.controller;

import com.ksptooi.biz.drive.model.dto.AddEntryDto;
import com.ksptooi.biz.drive.model.dto.CopyEntryDto;
import com.ksptooi.biz.drive.model.dto.GetEntryListDto;
import com.ksptooi.biz.drive.model.dto.RenameEntry;
import com.ksptooi.biz.drive.model.vo.DriveSignVo;
import com.ksptooi.biz.drive.model.vo.GetDriveInfo;
import com.ksptooi.biz.drive.model.vo.GetEntryDetailsVo;
import com.ksptooi.biz.drive.model.vo.GetEntryListVo;
import com.ksptooi.biz.drive.service.EntryService;
import com.ksptooi.biz.drive.service.EntrySignService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PrintLog
@RestController
@RequestMapping("/drive/entry")
@Tag(name = "Entry", description = "驱动器条目管理")
@Slf4j
public class EntryController {

    @Autowired
    private EntryService entryService;

    @Autowired
    private EntrySignService entrySignService;

    @PostMapping("/getDriveInfo")
    @Operation(summary = "获取云盘信息")
    public Result<GetDriveInfo> getDriveInfo() throws Exception {
        return Result.success(entryService.getDriveInfo());
    }


    @PostMapping("/getEntryList")
    @Operation(summary = "查询条目列表")
    public PageResult<GetEntryListVo> getEntryList(@RequestBody @Valid GetEntryListDto dto) throws Exception {
        return entryService.getEntryList(dto);
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


    @Operation(summary = "生成条目签名")
    @PostMapping("/generateEntrySign")
    public Result<DriveSignVo> generateEntrySign(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return Result.success(entrySignService.sign(dto.getId()));
    }

    @Operation(summary = "验证条目签名")
    @PostMapping("/verifyEntrySign")
    public Result<DriveSignVo> verifyEntrySign(@RequestBody @Valid Map<String, String> params) throws Exception {
        return Result.success(entrySignService.verify(params.get("sign")));
    }

}
