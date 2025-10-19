package com.ksptooi.biz.core.model.routerule.po;

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
@Table(name = "route_rule")
public class RouteRulePo {

    @Column(name = "id")
    @Id
    @Comment("路由规则ID")
    private Long id;


    @Column(name = "name")
    private String name;

    @Column(name = "match_type")
    private Integer matchType;

    @Column(name = "match_key")
    private String matchKey;

    @Column(name = "match_operator")
    private Integer matchOperator;

    @Column(name = "match_value")
    @Comment("匹配来源IP 为null时匹配所有IP")
    private String matchValue;

    @Column(name = "route_server_id")
    @Comment("所属服务器ID")
    private Long routeServerId;

    @Column(name = "seq")
    private Integer seq;

    @Column(name = "remark")
    private String remark;

    @Column(name = "create_time")
    @Comment("创建时间")
    private Date createTime;

    @Column(name = "update_time")
    @Comment("更新时间")
    private Date updateTime;

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
