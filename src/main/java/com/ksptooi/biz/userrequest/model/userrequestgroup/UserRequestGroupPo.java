package com.ksptooi.biz.userrequest.model.userrequestgroup;

import jakarta.persistence.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import com.ksptooi.biz.userrequest.model.userrequest.UserRequestPo;
import com.ksptooi.biz.user.model.user.UserPo;
import java.util.List;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "user_request_group")
@Getter @Setter
@Comment("用户请求组")
public class UserRequestGroupPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("请求组ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("用户ID")
    private UserPo user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true,foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("父级ID 为空表示根节点")
    private UserRequestGroupPo parent;

    @Column(name = "name",length = 64,nullable = false)
    @Comment("请求组名称")
    private String name;

    @Column(name = "description",length = 255)
    @Comment("请求组描述")
    private String description;

    @Column(name = "seq",nullable = false)
    @Comment("排序")
    private Integer seq;

    //请求组中的请求
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "group")
    @Comment("请求组中的请求")
    private List<UserRequestPo> requests;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
    }

}
