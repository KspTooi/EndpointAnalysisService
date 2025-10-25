package com.ksptooi.biz.requestdebug.model.userrequesttree;

import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.requestdebug.model.userrequest.UserRequestPo;
import com.ksptooi.biz.requestdebug.model.userrequestgroup.UserRequestGroupPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_request_tree")
@Getter
@Setter
@Comment("用户请求树")
public class UserRequestTreePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("用户ID")
    private UserPo user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("父级ID 为null表示根节点")
    private UserRequestTreePo parent;

    @Column(name = "name", length = 64, nullable = false)
    @Comment("节点名称")
    private String name;

    @Column(name = "kind", nullable = false)
    @Comment("节点类型 0:请求组 1:用户请求")
    private Integer kind;

    @Column(name = "seq", nullable = false)
    @Comment("排序")
    private Integer seq;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    @Comment("子节点")
    private List<UserRequestTreePo> children;

    @Column(name = "request_id", nullable = true)
    @Comment("请求ID")
    private Long requestId;

    @Column(name = "group_id", nullable = true)
    @Comment("请求组ID")
    private Long groupId;

    //挂载的请求组
    @OneToOne(mappedBy = "tree", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("挂载的请求组")
    private UserRequestGroupPo group;

    //挂载的请求
    @OneToOne(mappedBy = "tree", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("挂载的请求")
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
