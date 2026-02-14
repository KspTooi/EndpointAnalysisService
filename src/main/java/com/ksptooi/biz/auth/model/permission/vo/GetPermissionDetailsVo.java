package com.ksptooi.biz.auth.model.permission.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetPermissionDetailsVo {
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

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 创建人
     */
    private Long creatorId;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 修改人
     */
    private Long updaterId;

}
