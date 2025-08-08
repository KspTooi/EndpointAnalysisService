package com.ksptooi.biz.model.request;

import com.ksptooi.commons.utils.page.PageQuery;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter@Setter
public class GetRequestListVo{

    //数据ID
    private Long id;

    //请求ID
    private String requestId;

    //请求方法
    private String method;

    //请求URL
    private String url;

    //来源
    private String source;

    //请求体JSON
    private Object requestBody;

    //响应体JSON
    private Object responseBody;

    //状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时
    private Integer status;

    //HTTP响应状态码
    private Integer statusCode;

    //发起请求时间
    private LocalDateTime requestTime;

    //响应时间
    private LocalDateTime responseTime;

}
