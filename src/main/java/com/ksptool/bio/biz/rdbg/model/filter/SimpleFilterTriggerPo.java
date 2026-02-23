package com.ksptool.bio.biz.rdbg.model.filter;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "rdbg_simple_filter_trigger", comment = "过滤器触发器")
@Getter
@Setter
public class SimpleFilterTriggerPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "触发器ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "filter_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "过滤器ID")
    private SimpleFilterPo filter;

    @Column(name = "name", length = 128, nullable = false, comment = "触发器名称")
    private String name;

    @Column(name = "target", nullable = false, columnDefinition = "tinyint", comment = "目标 0:标头 1:JSON载荷 2:URL 3:HTTP方法 4:总是触发")
    private Integer target;

    @Column(name = "kind", nullable = false, columnDefinition = "tinyint", comment = "条件 0:包含 1:不包含 2:等于 3:不等于 4:总是触发")
    private Integer kind;

    @Column(name = "tk", length = 128, comment = "目标键")
    private String tk;

    @Column(name = "tv", length = 128, comment = "比较值")
    private String tv;

    @Column(name = "seq", nullable = false, columnDefinition = "int", comment = "排序")
    private Integer seq;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;


    //自动生成触发器名称
    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        generateName();
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
        generateName();
    }

    public void generateName() {

        if (target == null || kind == null) {
            return;
        }

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
        if (target == 3) {
            targetStr = "HTTP方法";
        }
        if (target == 4) {
            targetStr = "@";
        }

        String kindStr = null;
        if (kind == 0) {
            kindStr = "包含";
        }
        if (kind == 1) {
            kindStr = "不包含";
        }
        if (kind == 2) {
            kindStr = "等于";
        }
        if (kind == 3) {
            kindStr = "不等于";
        }
        if (target == 4) {
            kindStr = "总是触发";
        }

        if (targetStr == null || kindStr == null) {
            return;
        }

        sb.append(targetStr).append("_").append(kindStr);

        this.name = sb.toString();
    }

}
