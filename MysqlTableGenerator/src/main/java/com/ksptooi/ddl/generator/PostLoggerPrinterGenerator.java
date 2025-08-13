package com.ksptooi.ddl.generator;

import com.ksptooi.commons.JavaToMysqlTypeMapper;
import com.ksptooi.commons.TypeMapper;
import com.ksptooi.ddl.model.DDLGenOptions;
import com.ksptooi.ddl.model.TableDescription;
import com.ksptooi.ddl.model.TableDescriptionField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class PostLoggerPrinterGenerator implements DDLGenerator{


    private static final Logger log = LoggerFactory.getLogger(PostLoggerPrinterGenerator.class);

    private final TypeMapper typeMapper = new JavaToMysqlTypeMapper();

    @Override
    public void generate(DDLGenOptions options) {


        for(TableDescription table : options.getTableDescriptions()){

            String tableName = table.getTableName();

            System.out.println("\r\n表:"+tableName+"\r\n--------------------------------------------------------");

            for(TableDescriptionField field : table.getFields()){
                System.out.println(field.getName()+"\t"+field.getType()+"("+field.getLength()+","+field.getDecimals()+")"+"\t"+field.getComment());
            }

            System.out.println("\r\n");

        }

    }

    @Override
    public String getName() {
        return "确认日志打印生成器";
    }

}
