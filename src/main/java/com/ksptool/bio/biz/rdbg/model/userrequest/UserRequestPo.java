package com.ksptool.bio.biz.rdbg.model.userrequest;


import com.ksptool.bio.biz.core.model.user.UserPo;
import com.ksptool.bio.biz.rdbg.model.userrequestgroup.UserRequestGroupPo;
import com.ksptool.bio.biz.rdbg.model.userrequestlog.UserRequestLogPo;
import com.ksptool.bio.biz.rdbg.model.userrequesttree.UserRequestTreePo;
import com.ksptool.bio.biz.relay.model.request.RequestPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rdbg_request", comment = "用户保存的请求")
@Getter
@Setter
public class UserRequestPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "用户请求ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tree_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), unique = true, comment = "用户请求树ID")
    private UserRequestTreePo tree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "请求组ID")
    private UserRequestGroupPo group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "原始请求ID")
    private RequestPo originalRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "用户ID")
    private UserPo user;

    @Column(name = "name", length = 64, comment = "用户自定义请求名称")
    private String name;

    @Column(name = "method", length = 32, nullable = false, comment = "请求方法")
    private String method;

    @Column(name = "url", length = 320, nullable = false, comment = "请求URL")
    private String url;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_headers", columnDefinition = "json", comment = "请求头JSON")
    private String requestHeaders;

    @Column(name = "request_body_type", length = 320, nullable = false, comment = "请求体类型 JSON、表单数据、二进制")
    private String requestBodyType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_body", columnDefinition = "json", comment = "请求体JSON")
    private String requestBody;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userRequest")
    private List<UserRequestLogPo> requestLogs;

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
