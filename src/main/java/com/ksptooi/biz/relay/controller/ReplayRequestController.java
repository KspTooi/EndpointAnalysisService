package com.ksptooi.biz.relay.controller;

import com.ksptooi.biz.relay.model.replayrequest.*;
import com.ksptooi.biz.relay.service.ReplayRequestService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageableResult;
import com.ksptooi.commons.utils.web.Result;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@PrintLog
@Controller
@RestController
@RequestMapping("/replayRequest")
public class ReplayRequestController {

    @Autowired
    private ReplayRequestService replayRequestService;

    /**
     * 重放某原始请求
     *
     * @param dto 请求参数
     * @return 重放请求结果
     * @throws BizException 业务异常
     */
    @PostMapping("/replayRequest")
    public Result<String> replayRequest(@RequestBody @Valid Map<String, String> dto) throws Exception {

        String requestId = dto.get("requestId");

        if (StringUtils.isBlank(requestId)) {
            return Result.error("请求ID必传");
        }

        replayRequestService.replayRequest(dto.get("requestId"));
        return Result.success("success");
    }

    @PostMapping("/getOriginRequest")
    public Result<GetOriginRequestVo> getOriginRequest(@RequestBody @Valid GetOriginRequestDto dto) throws Exception {
        return Result.success(replayRequestService.getOriginRequest(dto.getRequestId()));
    }

    @PostMapping("/getReplayRequestList")
    public PageableResult<GetReplayRequestListVo> getReplayRequestList(@RequestBody @Valid GetReplayRequestListDto dto) throws AuthException {
        return replayRequestService.getReplayRequestList(dto);
    }

    @PostMapping("/getReplayRequestDetails")
    public Result<GetReplayRequestDetailsVo> getReplayRequestDetails(@RequestBody @Valid CommonIdDto dto) {
        return Result.success(replayRequestService.getReplayRequestDetails(dto.getId()));
    }


}
