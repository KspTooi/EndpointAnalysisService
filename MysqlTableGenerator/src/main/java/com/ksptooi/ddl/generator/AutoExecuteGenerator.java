package com.ksptooi.ddl.generator;

import com.ksptooi.ddl.model.DDLGenOptions;
import com.ksptooi.ddl.model.TableDescription;
import com.ksptooi.utils.DDLVelocityWrapper;
import com.ksptooi.utils.DatabaseTools;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 该生成器会自动执行生成的模板代码
 */
public class AutoExecuteGenerator implements DDLGenerator{


    private static final Logger log = LoggerFactory.getLogger(AutoExecuteGenerator.class);

    @Override
    public void generate(DDLGenOptions options) {

        if(!options.getAutoExecute()){
            return;
        }

        try{

            //自动替换策略 0:不论如何都会删除重建 1:当表有数据时不进行动作
            DatabaseTools dbTools = options.getDatabaseTools();
            Connection connection = options.getConnection();

            for(TableDescription table : options.getTableDescriptions()){

                //读取DBTD文件
                File dbtd = new File(new File(options.getAbsoluteEntitiesDir(),"/dbtd"),table.getTableName()+".dbtd");

                if(!dbtd.exists()){
                    log.error("无法自动执行,因为DBTD文件不存在. {}",dbtd.getName());
                }

                //表不存在 直接创建
                if(!dbTools.tableExist(table.getTableName())){
                    String sql = Files.readString(dbtd.toPath());
                    Statement stm = connection.createStatement();
                    stm.executeUpdate(sql);
                    stm.close();
                    log.info("已创建表:{}",table.getTableName());
                    continue;
                }

                //表存在 策略为0 删除重建
                if(options.getAutoUpdatePolicy() == 0){
                    dbTools.dropTable(table.getTableName());
                    String sql = Files.readString(dbtd.toPath());
                    Statement stm = connection.createStatement();
                    stm.executeUpdate(sql);
                    stm.close();
                    log.info("已更新表:{}",table.getTableName());
                    continue;
                }

                //表存在 策略为1 无数据时删除重建
                if(options.getAutoUpdatePolicy() == 1){
                    if(dbTools.isTableEmpty(table.getTableName())){
                        dbTools.dropTable(table.getTableName());
                        String sql = Files.readString(dbtd.toPath());
                        Statement stm = connection.createStatement();
                        stm.executeUpdate(sql);
                        stm.close();
                        log.info("已更新表:{}",table.getTableName());
                        continue;
                    }
                    log.info("当前策略不允许操作已有数据的表:{}",table.getTableName());
                }

            }

        }catch (Exception ex){
            ex.printStackTrace();
        }


    }

    @Override
    public String getName() {
        return "自动执行生成器";
    }

}
