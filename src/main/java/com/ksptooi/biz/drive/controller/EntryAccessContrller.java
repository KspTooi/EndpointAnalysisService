package com.ksptooi.biz.drive.controller;

import com.ksptooi.commons.annotation.PrintLog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
import com.ksptooi.biz.drive.service.EntryService;
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

@PrintLog
@RestController
@RequestMapping("/drive/object/access")
@Tag(name = "团队云盘-对象访问接口", description = "团队云盘对象访问接口")
@Slf4j
public class EntryAccessContrller {
    
    @Autowired
    private EntryService entryService;

    @Autowired
    private EntryAccessService entryAccessService;


    @Operation(summary = "获取条目对象签名")
    @PostMapping("/getEntrySign")
    public Result<GetEntrySignVo> getEntrySign(@RequestBody @Valid CommonIdDto dto) throws Exception {

        if(!dto.isValid()){
            return Result.error("条目ID不能为空");
        }

        var ret = new GetEntrySignVo();
        var ids = dto.toIds();

        if(ids.size() > 1){
            ret.setIsBatch(1);
            ret.setParams(entryAccessService.getEntrySign(ids));
            return Result.success(ret);
        }

        ret.setIsBatch(0);
        ret.setParams(entryAccessService.getEntrySign(ids.getFirst()));
        return Result.success(ret);
    }

    @Operation(summary = "下载条目")
    @GetMapping("/downloadEntry")
    @PostMapping("/downloadEntry")
    public ResponseEntity<Resource> downloadEntry(@RequestParam("sign") String sign) throws Exception {

        if(StringUtils.isBlank(sign)){
            throw new BizException("签名参数不能为空");
        }

        //验证签名
        var signVo = entryAccessService.verify(sign);

        if(signVo == null){
            throw new BizException("签名验证失败");
        }

        var resource = entryAccessService.downloadEntry(signVo);
        var filename = URLEncoder.encode(signVo.getEName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }




}
