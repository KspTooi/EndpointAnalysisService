package com.ksptooi.ddl.generator;

import com.ksptooi.ddl.model.DDLGenOptions;
import com.ksptooi.ddl.model.TableDescription;
import com.ksptooi.utils.DDLVelocityWrapper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.File;

/**
 * 该生成器根据数据模型构建模板
 */
public class TemplateOutputGenerator implements DDLGenerator{


    @Override
    public void generate(DDLGenOptions options) {

        Template template = DDLVelocityWrapper.getTemplate("table.vm");

        for(TableDescription table : options.getTableDescriptions()){
            VelocityContext ctx = DDLVelocityWrapper.injectContext(table);
            File dir = new File(options.getAbsoluteEntitiesDir(),"/dbtd");
            File target = new File(dir,table.getTableName()+".DBTD");
            DDLVelocityWrapper.mergeAndOutput(template,ctx,target);
        }

    }

    @Override
    public String getName() {
        return "模板写出生成器";
    }

}
