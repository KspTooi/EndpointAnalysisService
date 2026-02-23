package com.ksptool.bio.biz.auth.model.permission.vo;

import lombok.Data;

@Data
public class GetPermissionListVo {
    /**
     * 权限ID
     */
    private Long id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限代码
     */
    private String code;

    /**
     * 权限描述
     */
    private String remark;

    /**
     * 排序号
     */
    private Integer seq;

    /**
     * 是否为系统权限（1-是，0-否）
     */
    private Integer isSystem;

}
