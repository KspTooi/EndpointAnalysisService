package com.ksptooi.biz.auth.model.permission;

import com.ksptool.assembly.entity.web.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetPermissionListDto extends PageQuery {
    /**
     * 权限代码
     */
    private String code;

    /**
     * 权限名称
     */
    private String name;

}
