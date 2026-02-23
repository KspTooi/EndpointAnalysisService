package com.ksptool.bio.biz.document.model.epdoc;

import com.ksptooi.biz.relay.model.relayserver.RelayServerPo;
import com.ksptool.bio.biz.document.model.epdocsynclog.EndpointDocSyncLogPo;
import com.ksptool.bio.biz.document.model.epdocversion.EndpointDocVersionPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ep_document", comment = "端点文档定义")
@Getter
@Setter
public class EndpointDocPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "端点文档ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relay_server_id", comment = "中继通道ID")
    private RelayServerPo relayServer;

    @Column(name = "doc_pull_url", comment = "文档拉取URL")
    private String docPullUrl;

    @Column(name = "pull_time", comment = "拉取时间")
    private LocalDateTime pullTime;

    @Column(name = "create_time", comment = "创建时间")
    private LocalDateTime createTime;

    @OneToMany(mappedBy = "endpointDoc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EndpointDocVersionPo> versions;

    @OneToMany(mappedBy = "endpointDoc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EndpointDocSyncLogPo> syncLogs;


    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

}
