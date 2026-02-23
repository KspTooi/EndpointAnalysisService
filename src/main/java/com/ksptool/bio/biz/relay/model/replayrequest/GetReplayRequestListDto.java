package com.ksptool.bio.biz.relay.model.replayrequest;

import com.ksptool.assembly.entity.web.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetReplayRequestListDto extends PageQuery {

    //原始请求ID
    private String originRequestId;

    //中继通道ID
    private Long relayServerId;

    //请求ID
    private String requestId;

    //请求方法
    private String method;

    //请求URL
    private String url;

    //来源
    private String source;

    //状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时
    private Integer status;

}
