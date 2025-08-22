package com.ksptooi.biz.userrequest.model.userrequesttree;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import com.ksptooi.biz.user.model.user.UserPo;
import com.ksptooi.biz.userrequest.model.userrequest.UserRequestPo;
import com.ksptooi.biz.userrequest.model.userrequestgroup.UserRequestGroupPo;

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

    //挂载的请求
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("挂载的请求")
    private UserRequestPo request;


    //挂载的请求组
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("挂载的请求组")
    private UserRequestGroupPo group;



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
