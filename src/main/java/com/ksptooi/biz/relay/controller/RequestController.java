package com.ksptooi.biz.relay.controller;

import com.ksptooi.biz.relay.model.request.GetRequestDetailsVo;
import com.ksptooi.biz.relay.model.request.GetRequestListDto;
import com.ksptooi.biz.relay.model.request.GetRequestListVo;
import com.ksptooi.biz.relay.service.RequestService;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    public PageResult<GetRequestListVo> getRequestList(@RequestBody @Valid GetRequestListDto dto) {

        //开始、结束时间区间范围不可以超过15天
        long days = ChronoUnit.DAYS.between(dto.getStartTime(), dto.getEndTime());
        if (days > 15) {
            return PageResult.error("时间范围不可以超过15天");
        }

        return requestService.getRequestList(dto);
    }

    @PostMapping("/getRequestDetails")
    @Operation(summary = "获取中继器请求详情")
    public Result<GetRequestDetailsVo> getRequestDetails(@RequestBody @Valid CommonIdDto dto) {
        return Result.success(requestService.getRequestDetails(dto.getId()));
    }


}
