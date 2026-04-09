package com.ksptool.bio.biz.auth.common.mybatis;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mybatis数据权限构建器
 * 用于构建Mybatis查询时的数据权限SQL
 * <p>
 * 使用方法
 * 1.在Service函数上面加入@RowScope注解
 * 2.在Mapper上加一个参数 rsSql 用于接收数据权限SQL，或者直接加在Dto中
 * 3.在Mapper.xml中使用 ${rsSql} 来使用数据权限SQL 加在Where后面
 *
 * @author KspTool
 */
public class RsBuilder {

    /**
     * 构建Mybatis查询时的数据权限SQL
     *
     * @param alias 主表的别名 例如有SQL如下 SELECT * FROM 表名 AS T 这里就传T
     * @return 数据权限SQL
     */
    public static String build(String alias) {

        //上下文为空，说明没有加@RowScope注解或者当前用户吃了超级权限，直接放行
        RsContext context = RsContextHolder.get();
        if (context == null) {
            return "";
        }

        Integer rsMax = context.getRsMax();

        // rsMax 为 null 表示权限配置异常，拒绝所有数据
        if (rsMax == null) {
            return " AND 1 = 0 ";
        }

        // rsMax = 0 表示全部权限，不追加任何过滤条件
        if (rsMax == 0) {
            return "";
        }

        // alias 为空时不加前缀，否则加 "alias." 前缀
        String prefix = "";
        if (alias != null && !alias.isBlank()) {
            prefix = alias + ".";
        }

        // rsMax = 1 本公司/租户及以下，按 root_id 过滤
        if (rsMax == 1) {
            return " AND " + prefix + "root_id = " + context.getRootId() + " ";
        }

        // rsMax = 4 仅本人，按 creator_id 过滤
        if (rsMax == 4) {
            return " AND " + prefix + "creator_id = " + context.getUserId() + " ";
        }

        // rsMax = 2 本部门及以下 / rsMax = 3 本部门 / rsMax = 5 指定部门，均按 dept_id IN(...) 过滤
        if (rsMax == 2 || rsMax == 3 || rsMax == 5) {
            List<Long> deptIds = context.getDeptIds();

            // 允许的部门列表为空，拒绝所有数据
            if (deptIds == null || deptIds.isEmpty()) {
                return " AND 1 = 0 ";
            }

            String ids = deptIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            return " AND " + prefix + "dept_id IN (" + ids + ") ";
        }

        // 未知的 rsMax 值，拒绝所有数据
        return " AND 1 = 0 ";
    }

    /**
     * 构建Mybatis查询时的数据权限SQL（不带表别名）
     *
     * @return 数据权限SQL
     */
    public static String build() {
        return build(null);
    }
}
