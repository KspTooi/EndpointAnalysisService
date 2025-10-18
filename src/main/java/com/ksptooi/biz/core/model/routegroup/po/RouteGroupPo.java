package com.ksptooi.biz.core.model.routegroup.po;

import com.ksptooi.biz.core.model.routerule.po.RouteRulePo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "route_group")
@Comment("路由组")
public class RouteGroupPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("路由组ID")
    private Long id;

    @Column(name = "name", nullable = false, length = 32)
    @Comment("组名称")
    private String name;

    @Column(name = "remark", nullable = true, length = 5000)
    @Comment("备注")
    private String remark;

    @Column(name = "load_balance", nullable = false, columnDefinition = "tinyint")
    @Comment("负载均衡策略 0:轮询 1:随机 2:权重")
    private Integer loadBalance;

    @Column(name = "auto_degradation", nullable = false, columnDefinition = "tinyint")
    @Comment("自动降级 0:开启 1:关闭")
    private Integer autoDegradation;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("路由规则")
    private List<RouteRulePo> rules;


    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = this.createTime;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }


}
