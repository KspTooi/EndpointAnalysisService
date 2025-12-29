package com.ksptooi.biz.relay.model.request;

import com.ksptool.assembly.entity.web.PageQuery;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetRequestListDto extends PageQuery {

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

    //是否重放 0:否 1:是
    private Integer replay;

    //时间范围开始
    @NotNull(message = "此查询必须指定时间范围")
    private LocalDateTime startTime;

    //时间范围结束
    @NotNull(message = "此查询必须指定时间范围")
    private LocalDateTime endTime;

}
