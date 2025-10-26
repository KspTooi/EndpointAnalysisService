package com.ksptooi.biz.requestdebug.model.filter;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "rdbg_simple_filter_operation")
@Getter
@Setter
@Comment("过滤器操作")
public class SimpleFilterOperationPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("操作ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "filter_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("过滤器ID")
    private SimpleFilterPo filter;

    @Comment("操作名称")
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Comment("类型 0:持久化 1:缓存 2:注入缓存 3.注入持久化 4:覆写URL 50:标记请求状态 60:获取请求ID")
    @Column(name = "kind", nullable = false, columnDefinition = "tinyint")
    private Integer kind;

    @Comment("目标 0:标头 1:JSON载荷 2:URL(仅限kind=4) [50:正常 51:HTTP失败 52:业务失败 53:连接超时(仅限kind=50)]")
    @Column(name = "target", nullable = false, columnDefinition = "tinyint")
    private Integer target;

    @Comment("原始键")
    @Column(name = "f", length = 128)
    private String f;

    @Comment("目标键")
    @Column(name = "t", length = 128)
    private String t;

    @Comment("排序")
    @Column(name = "seq", nullable = false)
    private Integer seq;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;


    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        generateName();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
        generateName();
    }

    public void generateName() {

        StringBuilder sb = new StringBuilder();
        String targetStr = null;
        if (target == 0) {
            targetStr = "标头";
        }
        if (target == 1) {
            targetStr = "JSON载荷";
        }
        if (target == 2) {
            targetStr = "URL";
        }
        if (target == 50) {
            targetStr = "正常";
        }
        if (target == 51) {
            targetStr = "HTTP失败";
        }
        if (target == 52) {
            targetStr = "业务失败";
        }
        if (target == 53) {
            targetStr = "连接超时";
        }

        String kindStr = null;
        if (kind == 0) {
            kindStr = "持久化";
        }
        if (kind == 1) {
            kindStr = "缓存";
        }
        if (kind == 2) {
            kindStr = "注入缓存";
        }
        if (kind == 3) {
            kindStr = "注入持久化";
        }
        if (kind == 4) {
            kindStr = "覆写URL";
        }
        if (kind == 50) {
            kindStr = "标记请求状态";
        }
        if (kind == 60) {
            kindStr = "获取请求ID";
        }

        if (targetStr == null || kindStr == null) {
            return;
        }

        sb.append(targetStr).append("_").append(kindStr);
        this.name = sb.toString();
    }


}


