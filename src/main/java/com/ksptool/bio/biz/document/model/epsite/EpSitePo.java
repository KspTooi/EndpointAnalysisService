package com.ksptool.bio.biz.document.model.epsite;

import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.ksptool.bio.biz.auth.service.SessionService.session;

/**
 * 站点
 *
 * @author: generator
 */
@Getter
@Setter
@Entity
@Table(name = "ep_site", comment = "站点", uniqueConstraints = {
        @UniqueConstraint(name = "uk_ep_site_name", columnNames = {"name"})
})
public class EpSitePo {

    @Id
    @Column(name = "id", nullable = false, comment = "站点ID")
    private Long id;

    @Column(name = "name", nullable = false, length = 32, comment = "站点名称")
    private String name;

    @Column(name = "address", length = 255, comment = "地址")
    private String address;

    @Column(name = "username", length = 500, comment = "账户")
    private String username;

    @Column(name = "password", length = 500, comment = "密码")
    private String password;

    @Column(name = "remark", length = 1000, comment = "备注")
    private String remark;

    @Column(name = "seq", nullable = false, comment = "排序")
    private Integer seq;

    @Column(name = "name_py_idx", length = 320, comment = "站点名称-用于查询的拼音首字母索引")
    private String namePyIdx;

    @Column(name = "username_py_idx", length = 320, comment = "账户-用于查询的拼音首字母索引")
    private String usernamePyIdx;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false, comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false, comment = "更新人ID")
    private Long updaterId;

    @PrePersist
    private void onCreate() throws AuthException {
        if (this.id == null) {
            this.id = IdWorker.nextId();
        }

        LocalDateTime now = LocalDateTime.now();
        if (this.createTime == null) {
            this.createTime = now;
        }
        if (this.updateTime == null) {
            this.updateTime = this.createTime;
        }

        if (this.creatorId == null) {
            this.creatorId = session().getUserId();
        }
        if (this.updaterId == null) {
            this.updaterId = session().getUserId();
        }
    }

    @PreUpdate
    private void onUpdate() throws AuthException {
        this.updateTime = LocalDateTime.now();
        if (this.updaterId == null) {
            this.updaterId = session().getUserId();
        }
    }
}
