package com.ksptooi.biz.core.model.epdoc;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import com.ksptooi.biz.core.model.relayserver.RelayServerPo;
import java.time.LocalDateTime;

@Entity
@Table(name = "ep_document")
@Getter
@Setter
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

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

}
