package com.ksptooi.biz.document.model.epdocoperation;


import com.ksptooi.biz.document.model.epdoc.EndpointDocPo;
import com.ksptooi.biz.document.model.epdocversion.EndpointDocVersionPo;
import com.ksptooi.biz.relay.model.relayserver.RelayServerPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "ep_document_operation")
@Getter
@Setter
@Comment("端点文档操作")
public class EndpointDocOperationPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("端点文档操作ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relay_server_id", nullable = false)
    @Comment("中继通道ID")
    private RelayServerPo relayServer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ep_doc_id", nullable = false)
    @Comment("端点文档ID")
    private EndpointDocPo epDoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ep_doc_version_id", nullable = false)
    @Comment("端点文档版本ID")
    private EndpointDocVersionPo version;

    @Column(name = "tag", length = 255)
    @Comment("接口标签")
    private String tag;

    @Column(name = "path", length = 320, nullable = false)
    @Comment("接口路径")
    private String path;

    @Column(name = "method", length = 10)
    @Comment("请求方法")
    private String method;

    @Column(name = "summary", length = 320)
    @Comment("接口摘要")
    private String summary;

    @Column(name = "description", columnDefinition = "text")
    @Comment("接口描述")
    private String description;

    @Column(name = "operation_id", length = 255, nullable = false)
    @Comment("唯一操作ID")
    private String operationId;

    @Column(name = "req_query_json", columnDefinition = "longtext")
    @Comment("请求参数JSON")
    private String reqQueryJson;

    @Column(name = "res_query_json", columnDefinition = "longtext")
    @Comment("响应列表JSON")
    private String resQueryJson;

    @Column(name = "req_body_json", columnDefinition = "longtext")
    @Comment("请求体JSON")
    private String reqBodyJson;

    @Column(name = "res_body_json", columnDefinition = "longtext")
    @Comment("响应体JSON")
    private String resBodyJson;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

}
