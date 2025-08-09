package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.request.GetRequestListDto;
import com.ksptooi.biz.core.model.request.GetRequestListVo;
import com.ksptooi.biz.core.model.request.GetRequestDetailsVo;
import com.ksptooi.biz.core.service.RequestService;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageableResult;
import com.ksptooi.commons.utils.web.Result;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/request")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @PostMapping("/getRequestList")
    public PageableResult<GetRequestListVo> getRequestList(@RequestBody @Valid GetRequestListDto dto) {
        return requestService.getRequestList(dto);
    }

    @PostMapping("/getRequestDetails")
    public Result<GetRequestDetailsVo> getRequestDetails(@RequestBody @Valid CommonIdDto dto) {
        return Result.success(requestService.getRequestDetails(dto.getId()));
    }
    

}
