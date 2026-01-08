package com.ksptooi.biz.rdbg.model.collectionhistory;

import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import java.time.LocalDateTime;

/**
 * ${tableComment}
 *
 * @author: generator
 * @date: ${.now?string("yyyy年MM月dd日 HH:mm")}
 */
@Getter
@Setter
@Entity
@Table(name = "rdbg_collection_history")
@Comment("请求集合历史记录表")
public class CollectionHistoryPo {

    @Id
    @Column(name = "id")
    @Comment("记录ID")
    private Long id;

    @Column(name = "company_id")
    @Comment("公司ID")
    private Long companyId;

    @Column(name = "collection_id")
    @Comment("集合ID")
    private Long collectionId;

    @Column(name = "req_url")
    @Comment("请求URL")
    private String reqUrl;

    @Column(name = "req_url_params_json")
    @Comment("请求URL查询参数")
    private String reqUrlParamsJson;

    @Column(name = "req_method")
    @Comment("请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS")
    private Integer reqMethod;

    @Column(name = "req_header_json")
    @Comment("请求头JSON")
    private String reqHeaderJson;

    @Column(name = "req_body_json")
    @Comment("请求体JSON")
    private String reqBodyJson;

    @Column(name = "ret_header_json")
    @Comment("响应头JSON")
    private String retHeaderJson;

    @Column(name = "ret_body_json")
    @Comment("响应体JSON")
    private String retBodyJson;

    @Column(name = "ret_http_status")
    @Comment("HTTP状态码")
    private Integer retHttpStatus;

    @Column(name = "ret_biz_status")
    @Comment("状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时")
    private Integer retBizStatus;

    @Column(name = "req_time")
    @Comment("请求发起时间")
    private LocalDateTime reqTime;

    @Column(name = "ret_time")
    @Comment("响应时间")
    private LocalDateTime retTime;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id")
    @Comment("创建人ID")
    private Long creatorId;

    @Column(name = "update_time")
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id")
    @Comment("更新人ID")
    private Long updaterId;

    @Column(name = "delete_time")
    @Comment("删除时间 NULL未删除")
    private LocalDateTime deleteTime;

    @PrePersist
    private void onCreate() {
        if (this.id == null) {
            this.id = IdWorker.nextId();
        }

        LocalDateTime now = LocalDateTime.now();
        if (this.createTime == null) {
            this.createTime = now;
        }
        if (this.updateTime == null) {
            this.updateTime = this.createTime;
        }

        if (this.creatorId == null) {
            this.creatorId = AuthService.getCurrentUserId();
        }
        if (this.updaterId == null) {
            this.updaterId = AuthService.getCurrentUserId();
        }
    }

    @PreUpdate
    private void onUpdate() {
            this.updateTime = LocalDateTime.now();
        if (this.updaterId == null) {
            this.updaterId = AuthService.getCurrentUserId();
        }
    }
}
