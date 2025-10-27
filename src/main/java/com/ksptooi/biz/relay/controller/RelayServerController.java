package com.ksptooi.biz.relay.controller;

import com.ksptooi.biz.relay.model.relayserver.*;
import com.ksptooi.biz.relay.service.RelayServerService;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageableResult;
import com.ksptooi.commons.utils.web.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/relayServer")
public class RelayServerController {


    @Autowired
    private RelayServerService relayServerService;

    /**
     * 获取中继服务器列表
     *
     * @param dto 查询条件
     * @return 中继服务器列表
     */
    @PostMapping("/getRelayServerList")
    public PageableResult<GetRelayServerListVo> getRelayServerList(@RequestBody @Valid GetRelayServerListDto dto) {
        return relayServerService.getRelayServerList(dto);
    }

    /**
     * 添加中继服务器
     *
     * @param dto 中继服务器信息
     * @throws BizException
     */
    @PostMapping("/addRelayServer")
    public Result<String> addRelayServer(@RequestBody @Valid AddRelayServerDto dto) throws BizException {

        String validate = dto.validate();

        if (validate != null) {
            throw new BizException(validate);
        }

        relayServerService.addRelayServer(dto);
        return Result.success("添加中继服务器成功");
    }

    /**
     * 编辑中继服务器
     *
     * @param dto 中继服务器信息
     * @throws BizException
     */
    @PostMapping("/editRelayServer")
    public Result<String> editRelayServer(@RequestBody @Valid EditRelayServerDto dto) throws BizException {

        String validate = dto.validate();
        if (validate != null) {
            throw new BizException(validate);
        }

        relayServerService.editRelayServer(dto);
        return Result.success("编辑中继服务器成功");
    }


    /**
     * 获取中继服务器详情
     *
     * @param dto 中继服务器ID
     * @return 中继服务器详情
     */
    @PostMapping("/getRelayServerDetails")
    public Result<GetRelayServerDetailsVo> getRelayServerDetails(@RequestBody @Valid CommonIdDto dto) throws BizException {
        return Result.success(relayServerService.getRelayServerDetails(dto.getId()));
    }

    /**
     * 删除中继服务器
     *
     * @param dto 中继服务器ID
     * @throws BizException
     */
    @PostMapping("/removeRelayServer")
    public Result<String> removeRelayServer(@RequestBody @Valid CommonIdDto dto) throws BizException {
        relayServerService.removeRelayServer(dto.getId());
        return Result.success("删除中继服务器成功");
    }

    /**
     * 启动中继服务器
     *
     * @param dto 中继服务器ID
     * @throws BizException
     */
    @PostMapping("/startRelayServer")
    public Result<String> startRelayServer(@RequestBody @Valid CommonIdDto dto) throws BizException {
        return relayServerService.startRelayServer(dto.getId());
    }

    /**
     * 停止中继服务器
     *
     * @param dto 中继服务器ID
     * @throws BizException
     */
    @PostMapping("/stopRelayServer")
    public Result<String> stopRelayServer(@RequestBody @Valid CommonIdDto dto) throws BizException {
        return relayServerService.stopRelayServer(dto.getId());
    }

    /**
     * 获取中继服务器路由状态
     *
     * @param dto 中继服务器ID
     * @throws BizException
     */
    @PostMapping("/getRelayServerRouteState")
    public Result<List<GetRelayServerRouteStateVo>> getRelayServerRouteState(@RequestBody @Valid CommonIdDto dto) throws BizException {
        return Result.success(relayServerService.getRelayServerRouteState(dto.getId()));
    }

    /**
     * 重置熔断状态
     *
     * @param dto 中继服务器ID
     * @throws BizException
     */
    @PostMapping("/resetRelayServerBreaker")
    public Result<String> resetRelayServerBreaker(@RequestBody @Valid ResetRelayServerBreakerDto dto) throws BizException {
        relayServerService.resetRelayServerBreaker(dto);
        return Result.success("重置中继服务器熔断器成功");
    }

}
