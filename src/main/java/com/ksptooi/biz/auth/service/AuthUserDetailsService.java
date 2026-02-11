package com.ksptooi.biz.auth.service;

import com.ksptooi.biz.auth.model.auth.AuthUserDetails;
import com.ksptooi.biz.core.repository.UserRepository;

import static com.ksptool.entities.Entities.assign;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        //根据用户名查询用户
        var user = userRepository.getUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("用户 [" + username + "] 不存在");
        }

        //获取用户拥有的全部权限码
        var permissionCodes = userRepository.getUserPermissionCodes(user.getId());
        

        

        //组装用户详情
        var authUserDetails = new AuthUserDetails();
        assign(user, authUserDetails);

        //处理用户角色码


        authUserDetails.setPermissionCodes(permissionCodes);
        return null;
    }

}
