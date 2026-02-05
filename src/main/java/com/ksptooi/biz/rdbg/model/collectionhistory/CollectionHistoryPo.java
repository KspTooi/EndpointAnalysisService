package com.ksptooi.biz.rdbg.model.collectionhistory;

import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

import static com.ksptooi.biz.core.service.SessionService.session;


@Getter
@Setter
@Entity
@Table(name = "rdbg_collection_history", comment = "请求集合历史记录表")
@SQLDelete(sql = "UPDATE rdbg_collection_history SET delete_time = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class CollectionHistoryPo {

    @Id
    @Column(name = "id", comment = "记录ID")
    private Long id;

    @Column(name = "company_id",nullable = false, comment = "公司ID")
    private Long companyId;

    @Column(name = "collection_id",nullable = false, comment = "集合ID")
    private Long collectionId;

    @Column(name = "req_url",nullable = false,length = 320, comment = "请求URL")
    private String reqUrl;

    @Column(name = "req_url_params_json", columnDefinition = "json",nullable = false, comment = "请求URL查询参数 类型:RelayParam")
    private String reqUrlParamsJson;

    @Column(name = "req_method", columnDefinition = "tinyint",nullable = false, comment = "请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS")
    private Integer reqMethod;

    @Column(name = "req_header_json", columnDefinition = "json",nullable = false, comment = "请求头JSON 类型:RelayHeader")
    private String reqHeaderJson;

    @Column(name = "req_body_json", columnDefinition = "json",nullable = false, comment = "请求体JSON 类型:RelayBody")
    private String reqBodyJson;

    @Column(name = "ret_header_json", columnDefinition = "json",nullable = false, comment = "响应头JSON 类型:RelayHeader")
    private String retHeaderJson;

    @Column(name = "ret_body_text", columnDefinition = "longtext",nullable = false, comment = "响应体文本")
    private String retBodyText;

    @Column(name = "ret_http_status", comment = "HTTP状态码 NULL请求尚未完成")
    private Integer retHttpStatus;

    @Column(name = "biz_status", columnDefinition = "tinyint",nullable = false, comment = "业务状态 0:正常 1:HTTP失败 2:业务失败 3:正在处理 4:EAS内部错误")
    private Integer bizStatus;

    @Column(name = "error_message",columnDefinition = "longtext", comment = "EAS内部错误消息 NULL未发生错误")
    private String errorMessage;

    @Column(name = "req_time",nullable = false, comment = "请求发起时间")
    private LocalDateTime reqTime;

    @Column(name = "ret_time", comment = "响应时间")
    private LocalDateTime retTime;

    @Column(name = "create_time",nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id",nullable = false, comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time",nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id",nullable = false, comment = "更新人ID")
    private Long updaterId;

    @Column(name = "delete_time",nullable = true, comment = "删除时间 NULL未删除")
    private LocalDateTime deleteTime;

    @PrePersist
    private void onCreate() throws AuthException {
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
            this.creatorId = session().getUserId();
        }
        if (this.updaterId == null) {
            this.updaterId = session().getUserId();
        }
    }

    @PreUpdate
    private void onUpdate() throws AuthException {
            this.updateTime = LocalDateTime.now();
        if (this.updaterId == null) {
            this.updaterId = session().getUserId();
        }
    }
}
