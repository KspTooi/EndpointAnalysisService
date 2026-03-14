package com.ksptool.bio.biz.auth.model.group.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
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
    
    // 排序号
    private Integer seq;

    // 创建时间
    private LocalDateTime createTime;

}
