package com.ksptool.bio.biz.assembly.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import com.ksptool.bio.biz.assembly.model.scm.dto.AddScmDto;
import com.ksptool.bio.biz.assembly.model.scm.dto.EditScmDto;
import com.ksptool.bio.biz.assembly.model.scm.dto.GetScmListDto;
import com.ksptool.bio.biz.assembly.model.scm.vo.GetScmDetailsVo;
import com.ksptool.bio.biz.assembly.model.scm.vo.GetScmListVo;
import com.ksptool.bio.biz.assembly.service.ScmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/scm")
@Tag(name = "代码装配-SCM管理", description = "SCM管理")
@Slf4j
public class ScmController {

    @Autowired
    private ScmService scmService;

    @PostMapping("/getScmList")
    @Operation(summary = "查询SCM列表")
    public PageResult<GetScmListVo> getScmList(@RequestBody @Valid GetScmListDto dto) throws Exception {
        return scmService.getScmList(dto);
    }

    @Operation(summary = "新增SCM")
    @PostMapping("/addScm")
    public Result<String> addScm(@RequestBody @Valid AddScmDto dto) throws Exception {

        //标准化SCM URL
        if (StringUtils.isNotBlank(dto.getScmUrl())) {
            dto.setScmUrl(scmService.normalizeGitUrl(dto.getScmUrl()));
        }

        scmService.addScm(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "编辑SCM")
    @PostMapping("/editScm")
    public Result<String> editScm(@RequestBody @Valid EditScmDto dto) throws Exception {

        //标准化SCM URL
        if (StringUtils.isNotBlank(dto.getScmUrl())) {
            dto.setScmUrl(scmService.normalizeGitUrl(dto.getScmUrl()));
        }

        scmService.editScm(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "查询SCM详情")
    @PostMapping("/getScmDetails")
    public Result<GetScmDetailsVo> getScmDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetScmDetailsVo details = scmService.getScmDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除SCM")
    @PostMapping("/removeScm")
    public Result<String> removeScm(@RequestBody @Valid CommonIdDto dto) throws Exception {
        scmService.removeScm(dto);
        return Result.success("操作成功");
    }

    @Operation(summary = "测试SCM连接")
    @PostMapping("/testScmConnection")
    public Result<String> testScmConnection(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return scmService.testScmConnection(dto);
    }

}
