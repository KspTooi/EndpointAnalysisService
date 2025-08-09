package com.ksptooi.biz.user.model.permission;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetPermissionDetailsVo {
    /**
     * 权限ID
     */
    private Long id;
    
    /**
     * 权限代码
     */
    private String code;
    
    /**
     * 权限名称
     */
    private String name;
    
    /**
     * 权限描述
     */
    private String description;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 是否为系统权限（1-是，0-否）
     */
    private Integer isSystem;

    //创建时间
    private String createTime;

    //修改时间
    private String updateTime;

}
