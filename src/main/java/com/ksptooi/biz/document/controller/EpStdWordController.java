package com.ksptooi.biz.document.controller;

import com.ksptooi.biz.document.model.epstdword.dto.AddEpStdWordDto;
import com.ksptooi.biz.document.model.epstdword.dto.EditEpStdWordDto;
import com.ksptooi.biz.document.model.epstdword.dto.GetEpStdWordListDto;
import com.ksptooi.biz.document.model.epstdword.dto.ImportEpStdWordDto;
import com.ksptooi.biz.document.model.epstdword.vo.ExportEpStdWordVo;
import com.ksptooi.biz.document.model.epstdword.vo.GetEpStdWordDetailsVo;
import com.ksptooi.biz.document.model.epstdword.vo.GetEpStdWordListVo;
import com.ksptooi.biz.document.service.EpStdWordService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.dataprocess.ExportWizard;
import com.ksptooi.commons.dataprocess.ImportWizard;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@PrintLog
@RestController
@RequestMapping("/epStdWord")
@Tag(name = "EpStdWord", description = "标准词管理")
@Slf4j
public class EpStdWordController {

    @Autowired
    private EpStdWordService epStdWordService;

    @PostMapping("/getEpStdWordList")
    @Operation(summary = "查询标准词列表")
    public PageResult<GetEpStdWordListVo> getEpStdWordList(@RequestBody @Valid GetEpStdWordListDto dto) throws Exception {
        return epStdWordService.getEpStdWordList(dto);
    }

    @Operation(summary = "新增标准词")
    @PostMapping("/addEpStdWord")
    public Result<String> addEpStdWord(@RequestBody @Valid AddEpStdWordDto dto) throws Exception {
        epStdWordService.addEpStdWord(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑标准词")
    @PostMapping("/editEpStdWord")
    public Result<String> editEpStdWord(@RequestBody @Valid EditEpStdWordDto dto) throws Exception {
        epStdWordService.editEpStdWord(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询标准词详情")
    @PostMapping("/getEpStdWordDetails")
    public Result<GetEpStdWordDetailsVo> getEpStdWordDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetEpStdWordDetailsVo details = epStdWordService.getEpStdWordDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除标准词")
    @PostMapping("/removeEpStdWord")
    public Result<String> removeEpStdWord(@RequestBody @Valid CommonIdDto dto) throws Exception {
        epStdWordService.removeEpStdWord(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "导入标准词")
    @PostMapping("/importEpStdWord")
    public Result<String> importEpStdWord(@RequestParam("file") MultipartFile file) throws Exception {

        //准备向导
        var iw = new ImportWizard<>(file, ImportEpStdWordDto.class);

        //验证导入数据
        var errors = iw.validate();

        if (StringUtils.isNotBlank(errors)) {
            return Result.error(errors);
        }

        //获取导入数据
        var data = iw.getData();
        var count = epStdWordService.importEpStdWord(data);
        return Result.success("操作成功,已导入数据:" + count + "条");
    }

    @Operation(summary = "导出标准词")
    @RequestMapping("/exportEpStdWord")
    public void exportEpStdWord(@RequestBody @Valid GetEpStdWordListDto dto, HttpServletResponse hsrp) throws Exception {
        //准备导出向导
        var ew = new ExportWizard<ExportEpStdWordVo>(epStdWordService.exportEpStdWord(dto), hsrp);
        ew.transfer("标准词");
    }

}
