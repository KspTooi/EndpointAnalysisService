package com.ksptooi.nuclear.mybatis;

import com.ksptooi.mybatis.model.po.TableField;
import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import com.ksptooi.utils.DatabaseToolsH2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 管线用途: 将数据源DDL解析为PO模型供后续处理
 */
public class H2DdlProc extends OptionRegister {


    private static final Logger log = LoggerFactory.getLogger(H2DdlProc.class);

    @Override
    public String getName() {
        return "H2DB-DDL解析器";
    }

    private List<TableField> fields;

    @Override
    public void process(DataSource ds, Artifact a) {

        String tableName = require("tableName");

        DatabaseToolsH2 dbt = new DatabaseToolsH2((Connection) ds.getRaw());

        try {

            if (!dbt.tableExist(tableName)) {
                throw new RuntimeException("表:" + tableName + "不存在");
            }
            // 获取表注释
            String tableComment = null;
            try {
                DatabaseMetaData metaData = ((Connection) ds.getRaw()).getMetaData();
                ResultSet tables = metaData.getTables(null, (String) a.require("dbName"), tableName, new String[]{"TABLE"});
                if (tables.next()) {
                    tableComment = tables.getString("REMARKS");  // 获取表注释
                }
                tables.close();
                a.put("comments", tableComment);
            } catch (Exception ex) {
                log.error("获取表注释失败", ex);
            }
            // 获取到表中所有数据
            List<TableField> table = dbt.getFieldsByTable((String) a.require("dbName"), tableName);
            this.fields = table;
            a.put("fields", table);
            a.put("fieldHelper", this);
            a.put("table.name", tableName);
            a.put("tableName", tableName);

            // 自动侦测表中的主键
            DatabaseMetaData metaData = ((Connection) ds.getRaw()).getMetaData();
            ResultSet rs = metaData.getPrimaryKeys(null, null, tableName);
            List<String> pks = new ArrayList<>();
            while (rs.next()) {
                pks.add(rs.getString("COLUMN_NAME"));
            }

            if (pks.isEmpty()) {
                log.info("自动配置失败 当前表 {} 不含有主键 ", tableName);
                return;
            }

            if (!pks.isEmpty()) {
                log.info("[自动配置]当前表 {} 共有 {} 个主键", tableName, pks.size());
                String pk = pks.get(0);
                log.info("[自动配置]选择 {} 作为生成主键", pk);
                setPkFlag(pk, table);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }


    public void setPkFlag(String field, List<TableField> fields) {

        for (TableField item : fields) {
            if (item.getName().equals(field)) {
                item.setPrimary(true);
                log.info("[自动配置]已成功设置主键 位于:{}", item.getName());
                return;
            }
        }

        log.error("自动配置失败 无法在TableField中配置主键 位于:{}", field);
        throw new RuntimeException("自动配置失败 无法在TableField中配置主键");
    }


    public boolean exists(String fieldName) {

        boolean exists = false;

        for (TableField item : fields) {
            if (item.getName().equals(fieldName)) {
                exists = true;
            }
            if (item.getJavaFieldName().equals(fieldName)) {
                exists = true;
            }
            if (item.getJavaGetterName().equals(fieldName)) {
                exists = true;
            }
        }

        return exists;
    }

}
