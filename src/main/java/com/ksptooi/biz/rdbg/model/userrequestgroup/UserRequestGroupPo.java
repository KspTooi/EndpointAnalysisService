package com.ksptooi.biz.rdbg.model.userrequestgroup;

import com.ksptool.bio.biz.core.model.user.UserPo;
import com.ksptooi.biz.rdbg.model.filter.SimpleFilterPo;
import com.ksptooi.biz.rdbg.model.userrequest.UserRequestPo;
import com.ksptooi.biz.rdbg.model.userrequesttree.UserRequestTreePo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "rdbg_request_group", comment = "用户请求组")
@Getter
@Setter
public class UserRequestGroupPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "请求组ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tree_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), unique = true, comment = "用户请求树ID")
    private UserRequestTreePo tree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "用户ID")
    private UserPo user;

    @Column(name = "name", length = 64, nullable = false, comment = "请求组名称")
    private String name;

    @Column(name = "description", length = 255, comment = "请求组描述")
    private String description;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    //请求组中的请求
    @BatchSize(size = 20)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRequestPo> requests;

    //请求组中的过滤器
    @BatchSize(size = 20)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rdbg_simple_filter_request_group",
            joinColumns = @JoinColumn(name = "group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
            inverseJoinColumns = @JoinColumn(name = "simple_filter_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    )
    private List<SimpleFilterPo> filters;


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
