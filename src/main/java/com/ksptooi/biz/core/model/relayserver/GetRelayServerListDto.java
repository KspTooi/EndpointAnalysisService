package com.ksptooi.biz.core.model.relayserver;

import com.ksptooi.commons.utils.page.PageQuery;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetRelayServerListDto extends PageQuery{

    //中继服务器名称
    private String name;

    //桥接目标URL
    private String forwardUrl;

}
