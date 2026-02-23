package com.ksptool.bio.biz.auth.service;

import com.ksptool.bio.biz.auth.model.auth.AuthUserDetails;
import com.ksptool.bio.biz.auth.model.group.GroupPo;
import com.ksptool.bio.biz.auth.repository.GroupDeptRepository;
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

import java.util.ArrayList;
import java.util.HashSet;

import static com.ksptool.entities.Entities.assign;
import static com.ksptool.entities.Entities.fromJsonArray;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupDeptRepository gdRepository;

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

        /**
         * 处理RS数据权限
         * 
         * A:权限优先级
         * 当用户存在多个组时,提取所有组的RowScope等级,取最小值作为用户的RowScope等级
         * RS等级: 0:全部 1:本公司/租户及以下 2:本部门及以下 3:本部门 4:仅本人 5:指定部门
         * 
         * B:允许访问的部门IDS
         * 当用户存在多个组时,提取所有组的允许访问的部门IDS,取并集作为用户的允许访问的部门IDS
         * 
         * !如果用户没有任何组,则RS等级为4,允许访问的部门IDS为空
         */
        var rsMax = 5;
        var rsAllowDepts = new HashSet<Long>();

        //拥有指定部门RS的组IDS
        var allowDeptGroupIds = new HashSet<Long>();

        for (var group : groups) {

            //如果组的RS是指定部门,需要特殊处理
            if (group.getRowScope() == 5) {
                allowDeptGroupIds.add(group.getId());
                continue;
            }

            //如果组的RS等级小于当前最小值,则更新最小值
            if (group.getRowScope() < rsMax) {
                rsMax = group.getRowScope();
            }

        }

        //如果拥有指定部门RS的组IDS不为空,则需要获取与之关联的部门IDS
        if(!allowDeptGroupIds.isEmpty()){
            var deptIds = gdRepository.getDeptIdsByGroupIds(new ArrayList<>(allowDeptGroupIds));
            rsAllowDepts.addAll(deptIds);
        }

        //如果用户没有任何组,则RS等级为4,允许访问的部门IDS为空
        if(groups.isEmpty()){
            rsMax = 4;
            rsAllowDepts.clear();
        }

        //设置RS数据权限到AUD中
        authUserDetails.setRsMax(rsMax);
        authUserDetails.setRsAllowDepts(new ArrayList<>(rsAllowDepts));
        return authUserDetails;
    }

}
