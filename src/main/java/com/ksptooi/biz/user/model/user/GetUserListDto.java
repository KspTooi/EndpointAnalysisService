package com.ksptooi.biz.user.model.user;


import com.ksptooi.commons.utils.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetUserListDto extends PageQuery {
    /**
     * 用户名查询
     */
    private String username;

    //0:正常 1:封禁
    private Integer status;
}
