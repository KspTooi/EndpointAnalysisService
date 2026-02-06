package com.ksptooi.biz.rdbg.model.filter;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "rdbg_simple_filter_operation", comment = "过滤器操作")
@Getter
@Setter
public class SimpleFilterOperationPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "操作ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "filter_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "过滤器ID")
    private SimpleFilterPo filter;

    @Column(name = "name", length = 128, nullable = false, comment = "操作名称")
    private String name;

    @Column(name = "kind", nullable = false, columnDefinition = "tinyint", comment = "类型 0:持久化 1:缓存 2:注入缓存 3.注入持久化 4:覆写URL 50:标记请求状态 60:获取请求ID")
    private Integer kind;

    @Column(name = "target", nullable = false, columnDefinition = "tinyint", comment = "目标 0:标头 1:JSON载荷 2:URL(仅限kind=4) [50:正常 51:HTTP失败 52:业务失败 53:连接超时(仅限kind=50)]")
    private Integer target;

    @Column(name = "f", length = 128, comment = "原始键")
    private String f;

    @Column(name = "t", nullable = false, length = 128, comment = "目标键")
    private String t;

    @Column(name = "seq", nullable = false, comment = "排序")
    private Integer seq;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
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


