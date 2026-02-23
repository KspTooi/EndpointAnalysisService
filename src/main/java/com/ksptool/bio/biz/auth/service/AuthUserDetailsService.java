package com.ksptool.bio.biz.auth.service;

import com.ksptool.bio.biz.auth.model.auth.AuthUserDetails;
import com.ksptool.bio.biz.auth.repository.GroupDeptRepository;
import com.ksptool.bio.biz.auth.repository.GroupRepository;
import com.ksptool.bio.biz.core.repository.OrgRepository;
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

@Service
public class AuthUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupDeptRepository gdRepository;

    @Autowired
    private OrgRepository orgRepository;

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

        /*
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
        if (!allowDeptGroupIds.isEmpty()) {
            var deptIds = gdRepository.getDeptIdsByGroupIds(new ArrayList<>(allowDeptGroupIds));
            rsAllowDepts.addAll(deptIds);
        }

        //如果用户没有任何组,则RS等级为4,允许访问的部门IDS为空
        if (groups.isEmpty()) {
            rsMax = 4;
            rsAllowDepts.clear();
        }

        /*
         * 关于RS为3和2的处理
         * 这里有两种方案
         * 方案A: 用户登录/刷新会话时预计算部门以及下级部门IDS,存储到AUD中。（此方案需要处理部门树或用户角色/部门变动时的会话更新）
         * 方案B: 在查询时按数据范围动态拼接/注入SQL(基于 core_org.org_path_ids 做“包含当前 deptId 的后代”匹配再 IN)
         *
         * ⭕#0 初版:使用方案A时需要在此处理预计算 我们采纳方案B所以不进行预计算,RS处理延后到Hibernate过滤器或者Specification中
         * #0原因:
         * 主流做法是方案B：查询时按数据范围动态拼接/注入 SQL（AOP @DataScope），例如“本部门及以下”用 dept_id IN (SELECT dept_id FROM sys_dept WHERE dept_id=? OR FIND_IN_SET(?, ancestors)) 这类子查询，不在登录时预计算。
         * 你们系统建议优先采纳方案B（基于 core_org.org_path_ids 做“包含当前 deptId 的后代”匹配再 IN），一致性最好、权限/组织变更即时生效；方案1 只适合作为优化（加缓存/会话字段）且必须处理变更失效与会话膨胀问题。
         *
         * ✅#1 修订:现在采用方案A,即预计算RS允许访问的部门IDS并存储到AUD中
         * #1原因:
         * 你们用的是 JPA/Hibernate，不是 MyBatis,因为 MyBatis 的 ${params.dataScope} 可以直接拼接任意 SQL 片段。你们用 JPA，要在 Hibernate @Filter 或 Specification 里动态注入包含 LIKE 的子查询，写起来痛苦且脆弱。方案A 下 Filter/Spec 只需要 dept_id IN (:ids)，这在 JPA 里是最自然的写法。
         * 你们的数据模型已经为方案A而设计 UserSessionPo.rs_allow_depts 是 JSON 列，AuthUserDetails.rsAllowDepts 是 List<Long> — 这就是为"存一组预计算好的部门ID"而建的容器。如果走方案B，这两个字段在 RS=2/3 时永远为空，完全浪费。
         * 组织树变更频率极低 部门调整是低频管理操作（天/周级别），而列表查询是高频操作（秒级别）。把低频计算放在登录/刷新时做一次，让高频查询保持简单，是正确的权衡。至于一致性，组织树变更时清一下受影响用户的 Session 就够了，不需要复杂的版本号机制。
         *
         */

        //处理预计算RS
        //如果RS为3(本部门) 将用户所属部门添加到ADGS中
        if (rsMax == 3) {
            rsAllowDepts.add(user.getDeptId());
        }

        //如果RS为2(本部门及以下) 将用户所属部门以及其子部门添加到ADGS中
        if (rsMax == 2) {
            var depts = orgRepository.getChildDeptsByDeptId(user.getDeptId());
            for (var dept : depts) {
                rsAllowDepts.add(dept.getId());
            }
        }

        //设置RS数据权限到AUD中
        authUserDetails.setRsMax(rsMax);
        authUserDetails.setRsAllowDepts(new ArrayList<>(rsAllowDepts));
        return authUserDetails;
    }

}
