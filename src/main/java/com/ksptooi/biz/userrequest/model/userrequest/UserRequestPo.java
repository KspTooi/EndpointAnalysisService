package com.ksptooi.biz.userrequest.model.userrequest;


import com.ksptooi.biz.core.model.request.RequestPo;
import com.ksptooi.biz.user.model.user.UserPo;
import com.ksptooi.biz.userrequest.model.userrequestgroup.UserRequestGroupPo;
import com.ksptooi.biz.userrequest.model.userrequestlog.UserRequestLogPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_request")
@Getter
@Setter
@Comment("用户保存的请求")
public class UserRequestPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("用户请求ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("请求组ID")
    private UserRequestGroupPo group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("原始请求ID")
    private RequestPo originalRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("用户ID")
    private UserPo user;

    @Column(name = "name", length = 64)
    @Comment("用户自定义请求名称")
    private String name;

    @Column(name = "method", length = 32, nullable = false)
    @Comment("请求方法")
    private String method;

    @Column(name = "url", length = 320, nullable = false)
    @Comment("请求URL")
    private String url;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_headers", columnDefinition = "json")
    @Comment("请求头JSON")
    private String requestHeaders;

    @Column(name = "request_body_type", length = 320, nullable = false)
    @Comment("请求体类型 JSON、表单数据、二进制")
    private String requestBodyType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_body", columnDefinition = "json")
    @Comment("请求体JSON")
    private String requestBody;

    @Column(name = "seq", nullable = false)
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userRequest")
    @Comment("用户请求记录")
    private List<UserRequestLogPo> requestLogs;

}
