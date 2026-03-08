package com.ksptool.bio.biz.gen.common.assemblybp.collector;

import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.RawField;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.RawTable;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class MysqlCollector implements RawCollector {

    private static final Logger logger = LoggerFactory.getLogger(MysqlCollector.class);

    //数据库URL
    @Setter
    private String url;

    //数据库用户名
    @Setter
    private String username;

    //数据库密码
    @Setter
    private String password;

    //数据库名称
    @Setter
    private String database;

    //选中的表名列表
    private List<String> selectedTables;

    /**
     * 构造函数
     *
     * @param url      数据库URL
     * @param username 数据库用户名
     * @param password 数据库密码
     * @param database 数据库名称
     */
    public MysqlCollector(String url, String username, String password, String database) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public MysqlCollector() {
    }

    @Override
    public void select(String... tableNames) {
        if (tableNames == null || tableNames.length == 0) {
            throw new IllegalArgumentException("必须至少指定一个表名!");
        }
        this.selectedTables = new ArrayList<>(Arrays.asList(tableNames));
        logger.info("已选择要收集的表：{}", selectedTables);
    }

    @Override
    public List<RawTable> collect() {
        if (selectedTables == null || selectedTables.isEmpty()) {
            throw new IllegalStateException("必须先调用 select 方法指定要处理的表!");
        }
        logger.info("开始收集MySQL数据库表信息，数据库：{}", database);
        List<RawTable> tables = new ArrayList<>();
        Connection connection = null;
        try {
            logger.debug("连接MySQL数据库，URL：{}", url);
            connection = DriverManager.getConnection(url, username, password);
            List<String> tableNames = getTableNames(connection);
            logger.info("从数据库中获取到{}张表", tableNames.size());

            List<String> foundTables = new ArrayList<>();
            int seq = 0;
            for (String tableName : tableNames) {
                logger.debug("处理表：{}", tableName);
                RawTable table = buildTable(connection, tableName, seq++);
                if (table == null) {
                    logger.warn("表{}构建失败，跳过", tableName);
                    continue;
                }
                tables.add(table);
                foundTables.add(tableName);
            }

            for (String selectedTable : selectedTables) {
                if (!foundTables.contains(selectedTable)) {
                    logger.warn("警告：在数据库中未找到指定的表：{}", selectedTable);
                }
            }

            logger.info("收集完成，共收集{}张表", tables.size());
        } catch (SQLException e) {
            logger.error("收集MySQL表信息失败，数据库：{}", database, e);
            throw new RuntimeException("收集MySQL表信息失败", e);
        } finally {
            closeConnection(connection);
        }
        return tables;
    }

    private List<String> getTableNames(Connection connection) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ?");

        if (selectedTables != null && !selectedTables.isEmpty()) {
            sql.append(" AND TABLE_NAME IN (");
            for (int i = 0; i < selectedTables.size(); i++) {
                if (i > 0) {
                    sql.append(",");
                }
                sql.append("?");
            }
            sql.append(")");
        }

        sql.append(" ORDER BY TABLE_NAME");

        logger.debug("查询表名列表，SQL：{}，数据库：{}，指定表：{}", sql, database, selectedTables);
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setString(1, database);
            if (selectedTables != null && !selectedTables.isEmpty()) {
                for (int i = 0; i < selectedTables.size(); i++) {
                    ps.setString(i + 2, selectedTables.get(i));
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    if (StringUtils.isBlank(tableName)) {
                        logger.warn("获取到空表名，跳过");
                        continue;
                    }
                    tableNames.add(tableName);
                }
            }
        }
        return tableNames;
    }

    private RawTable buildTable(Connection connection, String tableName, int seq) throws SQLException {
        logger.debug("构建表信息，表名：{}，序号：{}", tableName, seq);
        RawTable table = new RawTable();
        table.setName(tableName);
        table.setSeq(seq);

        String comment = getTableComment(connection, tableName);
        table.setComment(comment);

        List<RawField> fields = getFields(connection, tableName);
        table.setFields(fields);
        logger.debug("表{}构建完成，字段数：{}", tableName, fields.size());

        return table;
    }

    private String getTableComment(Connection connection, String tableName) throws SQLException {
        String sql = "SELECT TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?";
        logger.debug("查询表注释，表名：{}", tableName);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, database);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String comment = rs.getString("TABLE_COMMENT");
                    logger.debug("表{}注释：{}", tableName, comment);
                    return comment;
                }
            }
        }
        logger.debug("表{}无注释", tableName);
        return null;
    }

    private List<RawField> getFields(Connection connection, String tableName) throws SQLException {
        List<RawField> fields = new ArrayList<>();
        String sql = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT, IS_NULLABLE, COLUMN_KEY, ORDINAL_POSITION " +
                "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION";
        logger.debug("查询表字段信息，表名：{}", tableName);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, database);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                int fieldSeq = 0;
                while (rs.next()) {
                    RawField field = new RawField();
                    String columnName = rs.getString("COLUMN_NAME");
                    field.setName(columnName);
                    field.setType(rs.getString("DATA_TYPE"));
                    field.setComment(rs.getString("COLUMN_COMMENT"));
                    field.setRequired("NO".equals(rs.getString("IS_NULLABLE")));
                    field.setPrimaryKey("PRI".equals(rs.getString("COLUMN_KEY")));
                    field.setSeq(fieldSeq++);
                    fields.add(field);
                    logger.debug("字段：{}，类型：{}，必填：{}，主键：{}", columnName, field.getType(), field.isRequired(), field.isPrimaryKey());
                }
            }
        }
        logger.debug("表{}共{}个字段", tableName, fields.size());
        return fields;
    }

    private void closeConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
            logger.debug("数据库连接已关闭");
        } catch (SQLException e) {
            logger.warn("关闭数据库连接失败", e);
        }
    }

}

