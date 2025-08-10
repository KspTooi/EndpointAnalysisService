package com.ksptooi.biz.core.model.request;

import com.ksptooi.biz.core.model.replayrequest.ReplayRequestPo;
import com.ksptooi.biz.core.model.relayserver.RelayServerPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "relay_request")
@Getter@Setter
public class RequestPo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relay_server_id",nullable = false)
    @Comment("中继服务器ID")
    private RelayServerPo relayServer;

    @Column(name = "request_id",length = 64,nullable = false,unique = true)
    @Comment("请求ID")
    private String requestId;
    
    @Column(name = "method",length = 32,nullable = false)
    @Comment("请求方法")
    private String method;
    
    @Column(name = "url",length = 255,nullable = false)
    @Comment("请求URL")
    private String url;

    @Column(name = "source",length = 64,nullable = false)
    @Comment("来源")
    private String source;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_headers",columnDefinition = "json")
    @Comment("请求头JSON")
    private String requestHeaders;
    
    @Column(name = "request_body_length",nullable = false)
    @Comment("请求体长度")
    private Integer requestBodyLength;

    @Column(name = "request_body_type",length = 64,nullable = false)
    @Comment("请求体类型 JSON、表单数据、二进制")
    private String requestBodyType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_body",columnDefinition = "json")
    @Comment("请求体JSON")
    private String requestBody;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_headers",columnDefinition = "json")
    @Comment("响应头JSON")
    private String responseHeaders;

    @Column(name = "response_body_length",nullable = false)
    @Comment("响应体长度")
    private Integer responseBodyLength;

    @Column(name = "response_body_type",length = 64,nullable = false)
    @Comment("响应体类型 JSON、表单数据、二进制")
    private String responseBodyType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_body",columnDefinition = "json")
    @Comment("响应体JSON")
    private String responseBody;

    @Column(name = "status_code",nullable = false)
    @Comment("HTTP响应状态码 -1为请求失败")
    private Integer statusCode;

    @Column(name = "redirect_url",length = 255)
    @Comment("重定向URL 301、302、303、307、308")
    private String redirectUrl;

    @Column(name = "status",length = 10,nullable = false)
    @Comment("状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时")
    private Integer status;

    @Column(name = "create_time",nullable = false)
    @Comment("发起请求时间")
    private LocalDateTime requestTime;

    @Column(name = "response_time")
    @Comment("响应时间")
    private LocalDateTime responseTime;

    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "originalRequest")
    @Comment("重放请求")
    private List<ReplayRequestPo> replayRequests;

    @PrePersist
    public void prePersist(){
        if(StringUtils.isBlank(this.requestBody)){
            this.requestBody = null;
        }
        if(StringUtils.isBlank(this.responseBody)){
            this.responseBody = null;
        }
    }

    @PreUpdate
    public void preUpdate(){
        if(StringUtils.isBlank(this.requestBody)){
            this.requestBody = null;
        }
        if(StringUtils.isBlank(this.responseBody)){
            this.responseBody = null;
        }
    }


}
