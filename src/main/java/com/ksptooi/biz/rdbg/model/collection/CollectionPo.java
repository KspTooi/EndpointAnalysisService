package com.ksptooi.biz.rdbg.model.collection;

import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "rdbg_collection")
@Comment("请求集合表")
@SQLDelete(sql = "UPDATE rdbg_collection SET delete_time = now() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class CollectionPo {

    @Id
    @Column(name = "id")
    @Comment("主键ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("父级节点 NULL顶级")
    private CollectionPo parent;

    @Column(name = "company_id", nullable = false)
    @Comment("公司ID")
    private Long companyId;

    @Column(name = "name", nullable = false, length = 128)
    @Comment("集合名称")
    private String name;

    @Column(name = "kind", nullable = false, columnDefinition = "tinyint")
    @Comment("集合类型 0:请求 1:组")
    private Integer kind;

    @Column(name = "req_url", length = 320)
    @Comment("请求URL")
    private String reqUrl;

    @Column(name = "req_url_params_json", columnDefinition = "json")
    @Comment("请求URL参数JSON")
    private String reqUrlParamsJson;

    @Column(name = "req_method", columnDefinition = "tinyint")
    @Comment("请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS")
    private Integer reqMethod;

    @Column(name = "req_header_json", columnDefinition = "json")
    @Comment("请求头JSON")
    private String reqHeaderJson;

    @Column(name = "req_body_kind", columnDefinition = "tinyint")
    @Comment("请求体类型 0:空 1:form-data 2:raw-text 3:raw-javascription 4:raw-json 5:raw-html 6:raw-xml 7:binary 8:x-www-form-urlencoded")
    private Integer reqBodyKind;

    @Column(name = "req_body_json", columnDefinition = "json")
    @Comment("请求体JSON")
    private String reqBodyJson;

    @Column(name = "seq", nullable = false)
    @Comment("排序")
    private Integer seq;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false)
    @Comment("创建人ID")
    private Long creatorId;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false)
    @Comment("更新人ID")
    private Long updaterId;

    @Column(name = "delete_time", nullable = true)
    @Comment("删除时间 为NULL未删除")
    private LocalDateTime deleteTime;


    @BatchSize(size = 100)
    @OrderBy("seq ASC")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    @Comment("子节点")
    private List<CollectionPo> children;

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
        if (this.companyId == null) {
            this.companyId = AuthService.getCurrentCompanyId();
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
