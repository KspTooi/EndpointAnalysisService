package com.ksptooi.biz.requestdebug.model.collection;

import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "rdbg_collection")
public class CollectionPo {

    @Column(name = "id")
    @Id
    @Comment("主键ID")
    private Long id;

    @Column(name = "parent_id")
    @Comment("父级ID NULL顶级")
    private Long parentId;

    @Column(name = "company_id")
    @Comment("公司ID")
    private Long companyId;

    @Column(name = "name")
    @Comment("集合名称")
    private String name;

    @Column(name = "kind")
    @Comment("集合类型 0:请求 1:组")
    private Integer kind;

    @Column(name = "req_url")
    @Comment("请求URL")
    private String reqUrl;

    @Column(name = "req_url_params_json")
    @Comment("请求URL参数JSON")
    private String reqUrlParamsJson;

    @Column(name = "req_method")
    @Comment("请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS")
    private Integer reqMethod;

    @Column(name = "req_header_json")
    @Comment("请求头JSON")
    private String reqHeaderJson;

    @Column(name = "req_body_kind")
    @Comment("请求体类型 0:空 1:form-data 2:raw-text 3:raw-javascription 4:raw-json 5:raw-html 6:raw-xml 7:binary 8:x-www-form-urlencoded")
    private Integer reqBodyKind;

    @Column(name = "req_body_json")
    @Comment("请求体JSON")
    private String reqBodyJson;

    @Column(name = "seq")
    @Comment("排序")
    private Integer seq;

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
    @Comment("删除时间 为NULL未删除")
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
