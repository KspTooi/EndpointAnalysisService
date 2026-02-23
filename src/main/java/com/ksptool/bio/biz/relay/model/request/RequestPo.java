package com.ksptool.bio.biz.relay.model.request;

import com.ksptool.bio.biz.relay.model.relayserver.RelayServerPo;
import com.ksptool.bio.biz.relay.model.replayrequest.ReplayRequestPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CREATE TABLE `relay_request` (
 * `id` bigint NOT NULL AUTO_INCREMENT,
 * `method` varchar(32) NOT NULL COMMENT '请求方法',
 * `redirect_url` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '重定向URL 301、302、303、307、308',
 * `request_body` json DEFAULT NULL COMMENT '请求体JSON',
 * `request_body_length` int NOT NULL COMMENT '请求体长度',
 * `request_body_type` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求体类型 JSON、表单数据、二进制',
 * `request_headers` json DEFAULT NULL COMMENT '请求头JSON',
 * `request_id` varchar(64) NOT NULL COMMENT '请求ID',
 * `create_time` datetime(6) NOT NULL COMMENT '发起请求时间',
 * `response_body` json DEFAULT NULL COMMENT '响应体JSON',
 * `response_body_length` int NOT NULL COMMENT '响应体长度',
 * `response_body_type` varchar(64) NOT NULL COMMENT '响应体类型 JSON、表单数据、二进制',
 * `response_headers` json DEFAULT NULL COMMENT '响应头JSON',
 * `response_time` datetime(6) DEFAULT NULL COMMENT '响应时间',
 * `source` varchar(64) NOT NULL COMMENT '来源',
 * `status` int NOT NULL COMMENT '状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时',
 * `status_code` int NOT NULL COMMENT 'HTTP响应状态码 -1为请求失败',
 * `url` varchar(255) NOT NULL COMMENT '请求URL',
 * `relay_server_id` bigint NOT NULL COMMENT '中继服务器ID',
 * PRIMARY KEY (`id`),
 * UNIQUE KEY `UKqfbp1xsjy7lwq8ich6s0o4x16` (`request_id`),
 * KEY `FKfgjbfvxueqqqcg01mitu358h0` (`relay_server_id`),
 * CONSTRAINT `FKfgjbfvxueqqqcg01mitu358h0` FOREIGN KEY (`relay_server_id`) REFERENCES `relay_server` (`id`)
 * ) ENGINE=InnoDB AUTO_INCREMENT=4936 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='重放请求';
 */
@Entity
@Table(name = "relay_request", comment = "请求表", indexes = {
        @Index(name = "idx_create_time", columnList = "create_time"),
        @Index(name = "idx_relay_server_id", columnList = "relay_server_id")
})
@Getter
@Setter
public class RequestPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "主键ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relay_server_id", nullable = false, comment = "中继服务器ID")
    private RelayServerPo relayServer;

    @Column(name = "request_id", length = 64, nullable = false, unique = true, comment = "请求ID")
    private String requestId;

    @Column(name = "method", length = 32, nullable = false, comment = "请求方法")
    private String method;

    @Column(name = "url", length = 255, nullable = false, comment = "请求URL")
    private String url;

    @Column(name = "source", length = 64, nullable = false, comment = "来源")
    private String source;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_headers", columnDefinition = "json", comment = "请求头JSON")
    private String requestHeaders;

    @Column(name = "request_body_length", nullable = false, comment = "请求体长度")
    private Integer requestBodyLength;

    @Column(name = "request_body_type", length = 256, nullable = false, comment = "请求体类型 JSON、表单数据、二进制")
    private String requestBodyType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_body", columnDefinition = "json", comment = "请求体JSON")
    private String requestBody;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_headers", columnDefinition = "json", comment = "响应头JSON")
    private String responseHeaders;

    @Column(name = "response_body_length", nullable = false, comment = "响应体长度")
    private Integer responseBodyLength;

    @Column(name = "response_body_type", length = 256, nullable = false, comment = "响应体类型 JSON、表单数据、二进制")
    private String responseBodyType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_body", columnDefinition = "json", comment = "响应体JSON")
    private String responseBody;

    @Column(name = "status_code", nullable = false, comment = "HTTP响应状态码 -1为请求失败")
    private Integer statusCode;

    @Column(name = "redirect_url", columnDefinition = "longtext", comment = "重定向URL 301、302、303、307、308")
    private String redirectUrl;

    @Column(name = "status", length = 10, nullable = false, comment = "状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时")
    private Integer status;

    @Column(name = "create_time", nullable = false, comment = "发起请求时间")
    private LocalDateTime requestTime;

    @Column(name = "response_time", comment = "响应时间")
    private LocalDateTime responseTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "originalRequest")
    private List<ReplayRequestPo> replayRequests;

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
