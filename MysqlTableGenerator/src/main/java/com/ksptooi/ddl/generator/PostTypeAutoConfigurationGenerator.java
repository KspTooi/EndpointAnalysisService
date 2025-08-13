package com.ksptooi.ddl.generator;

import com.ksptooi.commons.JavaToMysqlTypeMapper;
import com.ksptooi.commons.TypeFieldLength;
import com.ksptooi.commons.TypeMapper;
import com.ksptooi.ddl.model.DDLGenOptions;
import com.ksptooi.ddl.model.TableDescription;
import com.ksptooi.ddl.model.TableDescriptionField;
import com.ksptooi.utils.TextConv;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 将状态机解析后的对象进行后处理
 */
public class PostTypeAutoConfigurationGenerator implements DDLGenerator{


    private static final Logger log = LoggerFactory.getLogger(PostTypeAutoConfigurationGenerator.class);

    @Override
    public void generate(DDLGenOptions options) {

        var typeMapper = options.getTypeMapper();

        for(TableDescription table : options.getTableDescriptions()){

            String tableName = TextConv.toSnake(table.getTableName());

            log.info("后处理数据类型:{}",tableName);

            //根据排除规则移除
            for(String exclude : options.getExcludeTableName()){
                tableName = tableName.replace(exclude,"");
            }

            //添加表统一前缀
            if(StringUtils.isNotBlank(options.getPrefix())){
                tableName = options.getPrefix() + tableName;
            }
            //添加表统一后缀
            if(StringUtils.isNotBlank(options.getSuffix())){
                tableName = tableName + options.getSuffix();
            }

            for(TableDescriptionField field : table.getFields()){

                String fieldName = TextConv.toSnake(field.getName());

                String fieldType = typeMapper.convert(field.getType());

                var fieldLength = typeMapper.getFieldLength(fieldType);

                field.setName(fieldName);
                field.setType(fieldType);

                //如果TypeMapping结果大于0 则允许自定义字段长度
                if(fieldLength.getLength() > 0){

                    //如果field没有自定义长度 则应用TypeMapping中的长度
                    if(field.getLength() < 1){
                        field.setLength(fieldLength.getLength());
                    }

                }

                field.setDecimals(fieldLength.getDecimals());
                field.setAllowNull(false); //默认全都不允许为空

                //指定主键
                for(String primary : options.getPrimaryFields()){
                    if(field.getName().equals(primary)){
                        field.setPrimary(true);
                        table.getPrimary().add(fieldName);
                    }
                }

            }

            table.setTableName(tableName);
        }

    }

    @Override
    public String getName() {
        return "实体类后处理解析器";
    }

}
