package com.ksptooi.biz.rdbg.model.collectionhistory;

import com.odisp.biz.auth.service.AuthService;
import com.odisp.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.Date;

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
public class CollectionHistoryPo {

    @Column(name = "id")
    @Id
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
    private Date reqTime;

    @Column(name = "ret_time")
    @Comment("响应时间")
    private Date retTime;

    @Column(name = "create_time")
    @Comment("创建时间")
    private Date createTime;

    @Column(name = "creator_id")
    @Comment("创建人ID")
    private Long creatorId;

    @Column(name = "update_time")
    @Comment("更新时间")
    private Date updateTime;

    @Column(name = "updater_id")
    @Comment("更新人ID")
    private Long updaterId;

    @Column(name = "delete_time")
    @Comment("删除时间 NULL未删除")
    private Date deleteTime;

    @PrePersist
    private void onCreate() {
        if (this.id == null) {
            this.id = IdWorker.nextId();
        }

        Date now = new Date();
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
        this.updateTime = new Date();
        if (this.updaterId == null) {
            this.updaterId = AuthService.getCurrentUserId();
        }
    }
}
