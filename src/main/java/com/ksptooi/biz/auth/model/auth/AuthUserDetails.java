package com.ksptooi.biz.auth.model.auth;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 认证用户详情
 * 这是Spring Security的UserDetails实现类，用于存储已认证的用户信息，这包括用户基础信息和权限码
 */
@Getter
@Setter
public class AuthUserDetails implements UserDetails {

    //用户ID
    private Long id;

    //用户名
    private String username;

    //密码
    private String password;

    //用户昵称
    private String nickname;

    //用户性别
    private Integer gender;

    //用户手机号
    private String phone;

    //用户邮箱
    private String email;

    //用户状态
    private Integer status;

    //最后登录时间
    private LocalDateTime lastLoginTime;

    //所属企业ID
    private Long rootId;

    //所属部门ID
    private Long deptId;

    //所属企业名称
    private String rootName;

    //所属部门名称
    private String deptName;

    //是否为系统内置用户
    private Integer isSystem;

    //创建时间
    private LocalDateTime createTime;

    //用户角色码
    private List<String> roles;

    //用户权限码和角色码(角色码通过ROLE_前缀区分)
    private Set<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == 0; // 0:正常 1:封禁
    }
}
