package com.ksptooi.commons.event;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户登录事件
 */
@Getter
@Setter
public class UserLoginEvent {

    //用户ID
    private String userId;
    
    //用户名
    private String username;

    //登录方式 0:用户名密码
    private Integer loginKind;

    //登录IP
    private String ipAddr;

    //IP归属地
    private String location;

    //浏览器/客户端指纹
    private String browser;

    //操作系统
    private String os;

    //用户代理(User-Agent)字符串
    private String uaString;

    //状态 0:成功 1:失败
    private Integer status;

    //提示消息
    private String message;

}
