package com.ksptooi.biz.userrequest.model.userrequest;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Comment;
import com.ksptooi.biz.core.model.request.RequestPo;
import com.ksptooi.biz.userrequest.model.userrequestlog.UserRequestLogPo;
import com.ksptooi.biz.userrequest.model.userrequestgroup.UserRequestGroupPo;
import com.ksptooi.biz.user.model.user.UserPo;

import java.util.List;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_request")
@Getter @Setter
@Comment("用户保存的请求")
public class UserRequestPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("用户请求ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true,foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("请求组ID")
    private UserRequestGroupPo group;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = true,foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("原始请求ID")
    private RequestPo originalRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("用户ID")
    private UserPo user;

    @Column(name = "name",length = 64)
    @Comment("用户自定义请求名称")
    private String name;

    @Column(name = "seq",nullable = false)
    @Comment("排序")
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
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "userRequest")
    @Comment("用户请求记录")
    private List<UserRequestLogPo> requestLogs;

}
