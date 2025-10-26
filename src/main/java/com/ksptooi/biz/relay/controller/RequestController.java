package com.ksptooi.biz.relay.controller;

import com.ksptooi.biz.relay.model.request.GetRequestDetailsVo;
import com.ksptooi.biz.relay.model.request.GetRequestListDto;
import com.ksptooi.biz.relay.model.request.GetRequestListVo;
import com.ksptooi.biz.relay.service.RequestService;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageableResult;
import com.ksptooi.commons.utils.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request")
@Tag(name = "中继器请求", description = "中继器请求")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @PostMapping("/getRequestList")
    @Operation(summary = "获取中继器请求列表")
    public PageableResult<GetRequestListVo> getRequestList(@RequestBody @Valid GetRequestListDto dto) {
        return requestService.getRequestList(dto);
    }

    @PostMapping("/getRequestDetails")
    @Operation(summary = "获取中继器请求详情")
    public Result<GetRequestDetailsVo> getRequestDetails(@RequestBody @Valid CommonIdDto dto) {
        return Result.success(requestService.getRequestDetails(dto.getId()));
    }


}
