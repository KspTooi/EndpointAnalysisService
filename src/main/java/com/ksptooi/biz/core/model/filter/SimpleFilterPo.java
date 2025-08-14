package com.ksptooi.biz.core.model.filter;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import com.ksptooi.biz.user.model.user.UserPo;

@Entity
@Table(name = "simple_filter")
@Getter
@Setter
@Comment("过滤器表")
public class SimpleFilterPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Comment("所属用户 为null表示系统过滤器")
    private UserPo user;

    @Column(name = "name", length = 128, nullable = false)
    @Comment("过滤器名称")
    private String name;

    @Column(name = "direction", nullable = false, columnDefinition = "tinyint")
    @Comment("过滤器方向 0:请求过滤器 1:响应过滤器")
    private Integer direction;

    @Column(name = "trigger_condition", nullable = false, columnDefinition = "tinyint")
    @Comment("触发条件 0:标头包含 1:标头不包含 2:JSON载荷包含 3:JSON载荷不包含 10:总是触发")
    private Integer triggerCondition;

    @Column(name = "trigger_key")
    @Comment("触发键")
    private String triggerKey;

    @Column(name = "trigger_value")
    @Comment("触发值")
    private String triggerValue;

    @Column(name = "operation", nullable = false, columnDefinition = "tinyint")
    @Comment("过滤器操作 0:获取头 1:获取JSON 2:注入头 3:注入JSON 4:更改请求业务状态")
    private Integer operation;

    @Column(name = "get_key")
    @Comment("获取键")
    private String getKey;

    @Column(name = "get_value")
    @Comment("获取值")
    private String getValue;

    @Column(name = "inject_type", nullable = false, columnDefinition = "tinyint")
    @Comment("注入方式 0:注入字面量 1:从配置表获取")
    private Integer injectType;

    @Column(name = "inject_key")
    @Comment("注入键")
    private String injectKey;

    @Column(name = "inject_value")
    @Comment("注入值 当注入方式为0时 注入字面量 当注入方式为1时 注入配置表中的值")
    private String injectValue;

    @Column(name = "inject_status_code", nullable = false, columnDefinition = "tinyint")
    @Comment("注入状态码 0:正常 1:HTTP失败 2:业务失败 3:连接超时")
    private Integer injectStatusCode;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint")
    @Comment("状态 0:启用 1:禁用")
    private Integer status;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    

    @PrePersist
    public void prePersist(){
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.updateTime = LocalDateTime.now();
    }
}
