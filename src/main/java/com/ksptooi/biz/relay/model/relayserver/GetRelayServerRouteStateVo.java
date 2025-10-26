package com.ksptooi.biz.relay.model.relayserver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRelayServerRouteStateVo {

    //目标主机
    private String targetHost;

    //目标端口
    private Integer targetPort;

    //流过该规则的请求数量
    private Long hitCount;

    //是否已熔断 0:否 1:是
    private Integer isBreaked;
    
}
