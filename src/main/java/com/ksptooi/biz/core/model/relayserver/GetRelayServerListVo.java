package com.ksptooi.biz.core.model.relayserver;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetRelayServerListVo {

    //中继服务器ID
    private Long id;

    //中继服务器名称
    private String name;

    //中继服务器主机(与端口)
    private String host;
    
    //桥接目标URL
    private String forwardUrl;

    //自动运行 0:否 1:是
    private Integer autoStart;

    //中继服务器状态 0:已禁用 1:未启动 2:运行中 3:启动失败
    private Integer status;

    //创建时间
    private LocalDateTime createTime;
    
}
