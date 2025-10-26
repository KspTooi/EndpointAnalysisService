package com.ksptooi.biz.relay.model.replayrequest;

import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.relay.model.relayserver.RelayServerPo;
import com.ksptooi.biz.relay.model.request.RequestPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "relay_replay_request")
@Getter
@Setter
@Comment("重放请求记录")
public class ReplayRequestPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relay_server_id", nullable = false)
    @Comment("中继服务器ID")
    private RelayServerPo relayServer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_request_id", nullable = false)
    @Comment("原始请求")
    private RequestPo originalRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("用户ID")
    private UserPo user;

    @Column(name = "request_id", length = 64, nullable = false, unique = true)
    @Comment("请求ID")
    private String requestId;

    @Column(name = "method", length = 32, nullable = false)
    @Comment("请求方法")
    private String method;

    @Column(name = "url", length = 255, nullable = false)
    @Comment("请求URL")
    private String url;

    @Column(name = "source", length = 64, nullable = false)
    @Comment("来源")
    private String source;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_headers", columnDefinition = "json")
    @Comment("请求头JSON")
    private String requestHeaders;

    @Column(name = "request_body_length", nullable = false)
    @Comment("请求体长度")
    private Integer requestBodyLength;

    @Column(name = "request_body_type", length = 64, nullable = false)
    @Comment("请求体类型 JSON、表单数据、二进制")
    private String requestBodyType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_body", columnDefinition = "json")
    @Comment("请求体JSON")
    private String requestBody;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_headers", columnDefinition = "json")
    @Comment("响应头JSON")
    private String responseHeaders;

    @Column(name = "response_body_length", nullable = false)
    @Comment("响应体长度")
    private Integer responseBodyLength;

    @Column(name = "response_body_type", length = 64, nullable = false)
    @Comment("响应体类型 JSON、表单数据、二进制")
    private String responseBodyType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_body", columnDefinition = "json")
    @Comment("响应体JSON")
    private String responseBody;

    @Column(name = "status_code", nullable = false)
    @Comment("HTTP响应状态码 -1为请求失败")
    private Integer statusCode;

    @Column(name = "redirect_url", length = 255)
    @Comment("重定向URL 301、302、303、307、308")
    private String redirectUrl;

    @Column(name = "status", length = 10, nullable = false)
    @Comment("状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时")
    private Integer status;

    @Column(name = "create_time", nullable = false)
    @Comment("发起请求时间")
    private LocalDateTime requestTime;

    @Column(name = "response_time")
    @Comment("响应时间")
    private LocalDateTime responseTime;

    @PrePersist
    public void prePersist() {
        if (StringUtils.isBlank(this.requestBody)) {
            this.requestBody = null;
        }
        if (StringUtils.isBlank(this.responseBody)) {
            this.responseBody = null;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (StringUtils.isBlank(this.requestBody)) {
            this.requestBody = null;
        }
        if (StringUtils.isBlank(this.responseBody)) {
            this.responseBody = null;
        }
    }


}
