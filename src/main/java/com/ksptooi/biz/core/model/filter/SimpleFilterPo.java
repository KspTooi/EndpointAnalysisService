package com.ksptooi.biz.core.model.filter;

import com.ksptooi.biz.user.model.user.UserPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "status", nullable = false, columnDefinition = "tinyint")
    @Comment("状态 0:启用 1:禁用")
    private Integer status;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    //过滤器下的触发器
    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL)
    private List<SimpleFilterTriggerPo> triggers;

    //过滤器下的操作
    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL)
    private List<SimpleFilterOperationPo> operations;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
