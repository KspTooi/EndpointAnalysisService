package com.ksptool.bio.biz.auth.service;

import com.ksptool.bio.biz.auth.model.auth.AuthUserDetails;
import com.ksptool.bio.biz.auth.repository.GroupRepository;
import com.ksptool.bio.biz.core.repository.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;

import static com.ksptool.entities.Entities.assign;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @NullMarked
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //根据用户名查询用户
        var user = userRepository.getUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("用户 [" + username + "] 不存在");
        }

        //获取用户拥有的全部权限码
        var permissionCodes = userRepository.getUserPermissionCodes(user.getId());

        //获取用户拥有的全部用户组
        var groups = groupRepository.getGroupsByUserId(user.getId());

        //组装用户详情
        var authUserDetails = new AuthUserDetails();
        assign(user, authUserDetails);

        //兼容旧式Company
        authUserDetails.setCompanyId(user.getActiveCompanyId());

        //GrantedAuthority包括角色码和权限码 SpringSecurity通过ROLE_前缀区分角色和权限
        var grantedAuthorities = new HashSet<GrantedAuthority>();

        //将角色码和角色码转换成Granted
        for (var group : groups) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + group.getCode()));
        }

        for (var permission : permissionCodes) {
            grantedAuthorities.add(new SimpleGrantedAuthority(permission));
        }

        authUserDetails.setAuthorities(grantedAuthorities);
        return authUserDetails;
    }

}
