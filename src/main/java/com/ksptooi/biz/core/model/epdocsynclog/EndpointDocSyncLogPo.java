package com.ksptooi.biz.core.model.epdocsynclog;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Comment;

import com.ksptooi.biz.core.model.epdoc.EndpointDocPo;

@Entity
@Table(name = "ep_document_sync_log")
@Getter
@Setter
public class EndpointDocSyncLogPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("端点文档拉取记录ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ep_doc_id", nullable = false)
    @Comment("端点文档ID")
    private EndpointDocPo endpointDoc;

    @Column(name = "hash", length = 64)
    @Comment("文档MD5值")
    private String hash;

    @Column(name = "pull_url", length = 320, nullable = false)
    @Comment("拉取地址")
    private String pullUrl;

    @Column(name = "status", length = 1, nullable = false)
    @Comment("状态 0:成功 1:失败")
    private Integer status;

    @Column(name = "create_time")
    @Comment("拉取时间")
    private LocalDateTime createTime;

}
