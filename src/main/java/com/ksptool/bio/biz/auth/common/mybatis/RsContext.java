package com.ksptool.bio.biz.auth.common.mybatis;

import lombok.Getter;
import lombok.Setter;


import java.util.List;

/**
 * Mybatis数据权限上下文
 * 用于在Mybatis查询时根据用户权限过滤数据
 *
 * @author KspTool
 */
@Getter
@Setter
public class RsContext {

    private Integer rsMax;
    private Long userId;
    private Long rootId;
    private List<Long> deptIds;

    /**
     * 构造函数
     *
     * @param rsMax   数据权限最大等级
     * @param userId  用户ID
     * @param rootId  所属企业ID
     * @param deptIds 允许访问的部门IDS
     */
    public RsContext(Integer rsMax, Long userId, Long rootId, List<Long> deptIds) {
        this.rsMax = rsMax;
        this.userId = userId;
        this.rootId = rootId;
        this.deptIds = deptIds;
    }

}