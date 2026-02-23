package com.ksptool.bio.biz.auth.model.group.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetGroupListVo {

    // 组ID
    private Long id;

    // 组标识
    private String code;

    // 组名称
    private String name;

    // 成员数量
    private Integer memberCount;

    // 权限节点数量
    private Integer permissionCount;

    // 系统内置组 0:否 1:是
    private Integer isSystem;

    // 组状态：0-禁用，1-启用
    private Integer status;

    // 创建时间
    private LocalDateTime createTime;


    public GetGroupListVo(Long id, String code, String name, Integer memberCount, Integer permissionCount,
                          Integer isSystem, Integer status, LocalDateTime createTime) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.memberCount = memberCount;
        this.permissionCount = permissionCount;
        this.isSystem = isSystem;
        this.status = status;
        this.createTime = createTime;
    }
}
