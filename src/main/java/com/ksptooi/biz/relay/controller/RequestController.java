package com.ksptooi.biz.relay.controller;

import com.ksptooi.biz.relay.model.request.GetRequestDetailsVo;
import com.ksptooi.biz.relay.model.request.GetRequestListDto;
import com.ksptooi.biz.relay.model.request.GetRequestListVo;
import com.ksptooi.biz.relay.service.RequestService;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageableResult;
import com.ksptooi.commons.utils.web.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
