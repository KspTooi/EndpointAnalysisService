package com.ksptooi.biz.core.controller;


import com.ksptooi.biz.core.model.epdoc.AddEpDocDto;
import com.ksptooi.biz.core.model.epdoc.EditEpDocDto;
import com.ksptooi.biz.core.model.epdoc.GetEpDocDetailsVo;
import com.ksptooi.biz.core.model.epdoc.GetEpDocListDto;
import com.ksptooi.biz.core.model.epdoc.GetEpDocListVo;
import com.ksptooi.biz.core.service.EpDocService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import com.ksptooi.commons.utils.web.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@PrintLog
@Controller
@RestController
@RequestMapping("/epdoc")
public class EpDocController {

    @Autowired
    private EpDocService epDocService;

    @PostMapping("/getEpDocList")
    public PageResult<GetEpDocListVo> getEpDocList(@RequestBody @Valid GetEpDocListDto dto) {
        return epDocService.getEpDocList(dto);
    }

    @PostMapping("/addEpDoc")
    public Result<String> addEpDoc(@RequestBody @Valid AddEpDocDto dto) throws BizException {
        epDocService.addEpDoc(dto);
        return Result.success("添加成功");
    }

    @PostMapping("/getEpDocDetails")
    public Result<GetEpDocDetailsVo> getEpDocDetails(@RequestBody @Valid CommonIdDto dto) throws BizException {
        return Result.success(epDocService.getEpDocDetails(dto.getId()));
    }

    @PostMapping("/editEpDoc")
    public Result<String> editEpDoc(@RequestBody @Valid EditEpDocDto dto) throws BizException {
        epDocService.editEpDoc(dto);
        return Result.success("编辑成功");
    }

    @PostMapping("/removeEpDoc")
    public Result<String> removeEpDoc(@RequestBody @Valid CommonIdDto dto) throws BizException {
        epDocService.deleteEpDoc(dto.getId());
        return Result.success("删除成功");
    }

    @PostMapping("/pullEpDoc")
    public Result<String> pullEpDoc(@RequestBody @Valid CommonIdDto dto) throws Exception {
        epDocService.pullEpDoc(dto.getId());
        return Result.success("拉取文档成功");
    }



}
