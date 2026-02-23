package com.ksptool.bio.biz.core.model.appstatus.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSystemInfoIFVo {

    @Schema(description = "网卡名称(系统层 eth0,ens33)")
    private String name;

    @Schema(description = "网卡显示名称(网络适配器 1)")
    private String displayName;

    @Schema(description = "网卡MAC地址")
    private String mac;

    @Schema(description = "网卡IP地址")
    private List<String> ipv4Addrs;
    
}
