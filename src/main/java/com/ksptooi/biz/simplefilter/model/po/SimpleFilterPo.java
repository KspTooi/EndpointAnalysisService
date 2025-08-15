package com.ksptooi.biz.simplefilter.model.po;


import jakarta.persistence.*;
import lombok.*;
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
@Table(name = "simple_filter")
public class SimpleFilterPo {

    @Column(name = "id")
    @Id
    private Long id;


    @Column(name = "user_id")
    @Comment("所属用户 为null表示系统过滤器")
    private Long userId;

    @Column(name = "name")
    @Comment("过滤器名称")
    private String name;

    @Column(name = "direction")
    @Comment("过滤器方向 0:请求过滤器 1:响应过滤器")
    private Integer direction;

    @Column(name = "trigger_condition")
    @Comment("触发条件 0:标头包含 1:标头不包含 2:JSON载荷包含 3:JSON载荷不包含 10:总是触发")
    private Integer triggerCondition;

    @Column(name = "trigger_key")
    @Comment("触发键")
    private String triggerKey;

    @Column(name = "trigger_value")
    @Comment("触发值")
    private String triggerValue;

    @Column(name = "operation")
    @Comment("过滤器操作 0:获取头 1:获取JSON 2:注入头 3:注入JSON 4:更改请求业务状态")
    private Integer operation;

    @Column(name = "get_key")
    @Comment("获取键")
    private String getKey;

    @Column(name = "get_value")
    @Comment("获取值")
    private String getValue;

    @Column(name = "inject_type")
    @Comment("注入方式 0:注入字面量 1:从配置表获取")
    private Integer injectType;

    @Column(name = "inject_key")
    @Comment("注入键")
    private String injectKey;

    @Column(name = "inject_value")
    @Comment("注入值 当注入方式为0时 注入字面量 当注入方式为1时 注入配置表中的值")
    private String injectValue;

    @Column(name = "inject_status_code")
    @Comment("注入状态码 0:正常 1:HTTP失败 2:业务失败 3:连接超时")
    private Integer injectStatusCode;

    @Column(name = "status")
    @Comment("状态 0:启用 1:禁用")
    private Integer status;

    @Column(name = "create_time")
    @Comment("创建时间")
    private Date createTime;

    @Column(name = "update_time")
    @Comment("更新时间")
    private Date updateTime;

    @PrePersist
    private void onCreate() {

        Date now = new Date();
        if (this.createTime == null) {
            this.createTime = now;
        }
        if (this.updateTime == null) {
            this.updateTime = this.createTime;
        }

    }

    @PreUpdate
    private void onUpdate() {
        this.updateTime = new Date();
    }
}
