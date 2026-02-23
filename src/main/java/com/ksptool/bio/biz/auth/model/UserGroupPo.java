package com.ksptool.bio.biz.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "auth_user_group", comment = "用户组关联表")
@Getter
@Setter
@IdClass(UserGroupPo.Pk.class)
public class UserGroupPo {

    @Id
    @Column(name = "user_id", nullable = false, comment = "用户ID")
    private Long userId;

    @Id
    @Column(name = "group_id", nullable = false, comment = "用户组ID")
    private Long groupId;

    /**
     * 用于复合主键的类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Pk implements Serializable {
        private Long userId;
        private Long groupId;
    }
}
