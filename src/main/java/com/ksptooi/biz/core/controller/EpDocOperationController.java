package com.ksptooi.biz.core.controller;

import com.ksptooi.commons.exception.BizException;
import com.ksptooi.biz.core.model.epdocoperation.GetEpDocOperationDetailsVo;
import com.ksptooi.biz.core.model.epdocoperation.GetEpDocOperationTagListDto;
import com.ksptooi.biz.core.model.epdocoperation.GetEpDocOperationTagListVo;
import com.ksptooi.biz.core.service.EpDocOperationService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.Result;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PrintLog
@Controller
@RestController
@RequestMapping("/epdoc")
public class EpDocOperationController {


    @Autowired
    private EpDocOperationService epDocOperationService;


    /**
     * 获取端点文档接口标签列表
     * @param dto 请求参数
     * @return 端点文档接口标签列表
     * @throws BizException 业务异常
     */
    @PostMapping("/getEpDocOperationTagList")
    public Result<List<GetEpDocOperationTagListVo>> getEpDocOperationTagList(@RequestBody @Valid GetEpDocOperationTagListDto dto) throws BizException {
        return Result.success(epDocOperationService.getEpDocOperationTagList(dto));
    }


    /**
     * 获取端点文档接口详情
     * @param dto 请求参数
     * @return 端点文档接口详情
     * @throws BizException 业务异常
     */
    @PostMapping("/getEpDocOperationDetails")
    public Result<GetEpDocOperationDetailsVo> getEpDocOperationDetails(@RequestBody @Valid CommonIdDto dto) throws BizException {
        return Result.success(epDocOperationService.getEpDocOperationDetails(dto.getId()));
    }




}
