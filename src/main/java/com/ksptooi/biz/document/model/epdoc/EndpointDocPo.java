package com.ksptooi.biz.document.model.epdoc;

import com.ksptooi.biz.document.model.epdocsynclog.EndpointDocSyncLogPo;
import com.ksptooi.biz.document.model.epdocversion.EndpointDocVersionPo;
import com.ksptooi.biz.relay.model.relayserver.RelayServerPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ep_document")
@Getter
@Setter
@Comment("端点文档定义")
public class EndpointDocPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("端点文档ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relay_server_id")
    @Comment("中继通道ID")
    private RelayServerPo relayServer;

    @Column(name = "doc_pull_url")
    @Comment("文档拉取URL")
    private String docPullUrl;

    @Column(name = "pull_time")
    @Comment("拉取时间")
    private LocalDateTime pullTime;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "endpointDoc", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("端点文档版本")
    private List<EndpointDocVersionPo> versions;

    @OneToMany(mappedBy = "endpointDoc", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("端点文档拉取记录")
    private List<EndpointDocSyncLogPo> syncLogs;


    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

}
