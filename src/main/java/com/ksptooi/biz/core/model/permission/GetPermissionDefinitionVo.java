package com.ksptooi.biz.core.model.permission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetPermissionDefinitionVo {

    //权限节点ID
    private Long id;

    //权限节点标识
    private String code;

    //权限节点名称
    private String name;

}
