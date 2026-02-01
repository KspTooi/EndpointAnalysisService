package com.ksptooi.biz.document.model.epdocsynclog;


import com.ksptooi.biz.document.model.epdoc.EndpointDocPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ep_document_sync_log", comment = "端点文档拉取记录")
@Getter
@Setter
public class EndpointDocSyncLogPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "端点文档拉取记录ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ep_doc_id", nullable = false, comment = "端点文档ID")
    private EndpointDocPo endpointDoc;

    @Column(name = "hash", length = 64, comment = "文档MD5值")
    private String hash;

    @Column(name = "pull_url", length = 320, nullable = false, comment = "拉取地址")
    private String pullUrl;

    @Column(name = "new_version_created", length = 1, nullable = false, comment = "是否创建了新版本 0:否 1:是")
    private Integer newVersionCreated;

    @Column(name = "new_version_num", length = 10, comment = "新版本号，如果创建了新版本，则记录新版本号")
    private Long newVersionNum;

    @Column(name = "status", length = 1, nullable = false, comment = "状态 0:成功 1:失败")
    private Integer status;

    @Column(name = "create_time", comment = "拉取时间")
    private LocalDateTime createTime;

}
