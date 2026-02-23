package com.ksptool.bio.biz.rdbg.model.userrequesttree;

import com.ksptool.bio.biz.core.model.user.UserPo;
import com.ksptool.bio.biz.rdbg.model.userrequest.UserRequestPo;
import com.ksptool.bio.biz.rdbg.model.userrequestgroup.UserRequestGroupPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rdbg_request_tree", comment = "用户请求树")
@Getter
@Setter
public class UserRequestTreePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "主键ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "用户ID")
    private UserPo user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "父级ID 为null表示根节点")
    private UserRequestTreePo parent;

    @Column(name = "name", length = 64, nullable = false, comment = "节点名称")
    private String name;

    @Column(name = "kind", nullable = false, comment = "节点类型 0:请求组 1:用户请求")
    private Integer kind;

    @Column(name = "seq", nullable = false, comment = "排序")
    private Integer seq;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    private List<UserRequestTreePo> children;

    @Column(name = "request_id", nullable = true, comment = "请求ID")
    private Long requestId;

    @Column(name = "group_id", nullable = true, comment = "请求组ID")
    private Long groupId;

    //挂载的请求组
    @OneToOne(mappedBy = "tree", cascade = CascadeType.ALL, orphanRemoval = true)
    //@Comment("挂载的请求组")
    private UserRequestGroupPo group;

    //挂载的请求
    @OneToOne(mappedBy = "tree", cascade = CascadeType.ALL, orphanRemoval = true)
    //@Comment("挂载的请求")
    private UserRequestPo request;


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
