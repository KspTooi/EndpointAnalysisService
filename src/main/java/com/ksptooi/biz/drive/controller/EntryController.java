package com.ksptooi.biz.drive.controller;

import com.ksptooi.biz.drive.model.dto.AddEntryDto;
import com.ksptooi.biz.drive.model.dto.CopyEntryDto;
import com.ksptooi.biz.drive.model.dto.GetEntryListDto;
import com.ksptooi.biz.drive.model.dto.RenameEntry;
import com.ksptooi.biz.drive.model.vo.EntrySignVo;
import com.ksptooi.biz.drive.model.vo.GetDriveInfo;
import com.ksptooi.biz.drive.model.vo.GetEntryDetailsVo;
import com.ksptooi.biz.drive.model.vo.GetEntryListVo;
import com.ksptooi.biz.drive.service.EntryService;
import com.ksptooi.biz.drive.service.EntrySignService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "下载条目")
    @GetMapping("/downloadEntry")
    @PostMapping("/downloadEntry")
    public ResponseEntity<Resource> downloadEntry(@RequestParam("sign") String sign) throws Exception {

        if(StringUtils.isBlank(sign)){
            throw new BizException("签名参数不能为空");
        }

        //验证签名
        var signVo = entrySignService.verify(sign);

        if(signVo == null){
            throw new BizException("签名验证失败");
        }

        var resource = entryService.downloadEntry(signVo);
        var filename = URLEncoder.encode(signVo.getEName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    
    @Operation(summary = "生成条目签名")
    @PostMapping("/generateEntrySign")
    public Result<EntrySignVo> generateEntrySign(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return Result.success(entrySignService.sign(dto.getId()));
    }

    @Operation(summary = "验证条目签名")
    @PostMapping("/verifyEntrySign")
    public Result<EntrySignVo> verifyEntrySign(@RequestBody @Valid Map<String, String> params) throws Exception {
        return Result.success(entrySignService.verify(params.get("sign")));
    }

}
