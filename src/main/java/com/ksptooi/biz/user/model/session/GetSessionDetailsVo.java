package com.ksptooi.biz.user.model.session;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class GetSessionDetailsVo {

    //会话ID
    private Long id;

    //用户名
    private String username;

    //登入时间
    private Date createTime;

    //过期时间
    private Date expiresAt;

    //权限节点
    private Set<String> permissions;

}
