package com.ksptooi.biz.document.model.epdocversion;

import com.ksptooi.biz.document.model.epdoc.EndpointDocPo;
import com.ksptooi.biz.document.model.epdocoperation.EndpointDocOperationPo;
import com.ksptooi.biz.relay.model.relayserver.RelayServerPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ep_document_version", comment = "端点文档版本")
@Getter
@Setter
public class EndpointDocVersionPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "端点文档版本ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relay_server_id", nullable = false, comment = "中继通道ID")
    private RelayServerPo relayServer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ep_doc_id", nullable = false, comment = "端点文档ID")
    private EndpointDocPo endpointDoc;

    @Column(name = "version", nullable = false, comment = "版本号")
    private Long version;

    @Column(name = "hash", length = 64, nullable = false, unique = true, comment = "HASH")
    private String hash;

    @Column(name = "api_total", nullable = false, comment = "接口总数")
    private Integer apiTotal;

    @Column(name = "is_latest", length = 1, nullable = false, comment = "是否最新版本 0:否 1:是")
    private Integer isLatest;

    @Lob
    @Column(name = "doc_json", columnDefinition = "LONGTEXT", nullable = false, comment = "原始文档JSON")
    private String docJson;

    @Column(name = "is_persisted", length = 1, nullable = false, comment = "是否已持久化 0:否 1:是")
    private Integer isPersisted;

    @Column(name = "create_time", comment = "创建时间")
    private LocalDateTime createTime;


    @OneToMany(mappedBy = "version", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EndpointDocOperationPo> operations;

}
