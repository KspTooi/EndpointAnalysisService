package com.ksptooi.biz.drive.controller;

import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.config.DriveConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptooi.biz.drive.model.vo.GetEntrySignVo;
import com.ksptooi.biz.drive.service.EntryAccessService;
import com.ksptooi.biz.drive.utils.DriveEntrySignUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@PrintLog
@RestController
@RequestMapping("/drive/object/access")
@Tag(name = "团队云盘-对象访问接口", description = "团队云盘对象访问接口")
@Slf4j
public class EntryAccessContrller {
    
    @Autowired
    private EntryAccessService entryAccessService;

    @Autowired
    private DriveConfig driveConfig;

    @Operation(summary = "获取条目对象签名")
    @PostMapping("/getEntrySign")
    public Result<GetEntrySignVo> getEntrySign(@RequestBody @Valid CommonIdDto dto) throws Exception {

        if(!dto.isValid()){
            return Result.error("条目ID不能为空");
        }

        var ret = new GetEntrySignVo();
        ret.setParams(entryAccessService.getEntrySign(dto.toIds()));
        ret.setIsBatch(dto.toIds().size() > 1 ? 1 : 0);
        return Result.success(ret);
    }

    @Operation(summary = "下载条目")
    @GetMapping("/downloadEntry")
    @PostMapping("/downloadEntry")
    public ResponseEntity<Resource> downloadEntry(@RequestParam("sign") String sign, HttpServletResponse hsrp) throws Exception {

        if(StringUtils.isBlank(sign)){
            throw new BizException("签名参数不能为空");
        }

        //验证签名
        var signVo = DriveEntrySignUtils.parserSignWithParams(sign, driveConfig.getSignSecretKey());

        //判断签名是否过期
        if(System.currentTimeMillis() > (signVo.getT() + driveConfig.getTtl() * 1000)){
            throw new BizException("签名已过期! 请重新生成签名并尝试下载.");
        }

        //单签直接下载单个文件
        if(signVo.getIsBatch() == 0){
            var resource = entryAccessService.downloadEntry(signVo);
            var filename = URLEncoder.encode(signVo.getEName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }

        var name = "多个文件("+Arrays.stream(signVo.getEids().split(",")).count()+").zip";
        var encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        hsrp.setContentType("application/zip");
        hsrp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName);
        hsrp.setCharacterEncoding("utf-8");
        //多签打包下载
        entryAccessService.downloadBatchEntry(signVo, hsrp.getOutputStream());
        return null;
    }





}
