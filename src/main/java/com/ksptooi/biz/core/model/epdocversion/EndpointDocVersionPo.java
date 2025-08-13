package com.ksptooi.biz.core.model.epdocversion;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Comment;
import com.ksptooi.biz.core.model.epdoc.EndpointDocPo;
import com.ksptooi.biz.core.model.epdocoperation.EndpointDocOperationPo;
import com.ksptooi.biz.core.model.relayserver.RelayServerPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ep_document_version")
@Getter@Setter
@Comment("端点文档版本")
public class EndpointDocVersionPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("端点文档版本ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relay_server_id", nullable = false)
    @Comment("中继通道ID")
    private RelayServerPo relayServer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ep_doc_id", nullable = false)
    @Comment("端点文档ID")
    private EndpointDocPo endpointDoc;

    @Column(name = "version", nullable = false)
    @Comment("版本号")
    private Long version;

    @Column(name = "hash", length = 64, nullable = false, unique = true)
    @Comment("HASH")
    private String hash;

    @Column(name = "api_total", nullable = false)
    @Comment("接口总数")
    private Integer apiTotal;

    @Column(name = "is_latest", length = 1, nullable = false)
    @Comment("是否最新版本 0:否 1:是")
    private Integer isLatest;

    @Lob
    @Column(name = "doc_json", columnDefinition = "LONGTEXT", nullable = false)
    @Comment("原始文档JSON")
    private String docJson;

    @Column(name = "is_persisted", length = 1, nullable = false)
    @Comment("是否已持久化 0:否 1:是")
    private Integer isPersisted;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;


    @OneToMany(mappedBy = "version", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("端点文档接口")
    private List<EndpointDocOperationPo> operations;

}
