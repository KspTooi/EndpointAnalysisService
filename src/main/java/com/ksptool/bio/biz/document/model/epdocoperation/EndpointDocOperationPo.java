package com.ksptool.bio.biz.document.model.epdocoperation;


import com.ksptooi.biz.relay.model.relayserver.RelayServerPo;
import com.ksptool.bio.biz.document.model.epdoc.EndpointDocPo;
import com.ksptool.bio.biz.document.model.epdocversion.EndpointDocVersionPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ep_document_operation", comment = "端点文档操作")
@Getter
@Setter
public class EndpointDocOperationPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "端点文档操作ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relay_server_id", nullable = false, comment = "中继通道ID")
    private RelayServerPo relayServer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ep_doc_id", nullable = false, comment = "端点文档ID")
    private EndpointDocPo epDoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ep_doc_version_id", nullable = false, comment = "端点文档版本ID")
    private EndpointDocVersionPo version;

    @Column(name = "tag", length = 255, comment = "接口标签")
    private String tag;

    @Column(name = "path", length = 320, nullable = false, comment = "接口路径")
    private String path;

    @Column(name = "method", length = 10, comment = "请求方法")
    private String method;

    @Column(name = "summary", length = 320, comment = "接口摘要")
    private String summary;

    @Column(name = "description", columnDefinition = "text", comment = "接口描述")
    private String description;

    @Column(name = "operation_id", length = 255, nullable = false, comment = "唯一操作ID")
    private String operationId;

    @Column(name = "req_query_json", columnDefinition = "longtext", comment = "请求参数JSON")
    private String reqQueryJson;

    @Column(name = "res_query_json", columnDefinition = "longtext", comment = "响应列表JSON")
    private String resQueryJson;

    @Column(name = "req_body_json", columnDefinition = "longtext", comment = "请求体JSON")
    private String reqBodyJson;

    @Column(name = "res_body_json", columnDefinition = "longtext", comment = "响应体JSON")
    private String resBodyJson;

    @Column(name = "create_time", comment = "创建时间")
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

}
