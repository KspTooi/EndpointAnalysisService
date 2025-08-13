package com.ksptooi.dict;

import com.ksptooi.commons.AbstractGeneratorLauncher;
import com.ksptooi.commons.GeneratorDataSource;
import com.ksptooi.utils.DDLVelocityWrapper;
import com.ksptooi.utils.DictVelocityWrapper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import xyz.downgoon.snowflake.Snowflake;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class DictGeneratorLauncher extends AbstractGeneratorLauncher {

    private DictGenOptions options;

    public DictGeneratorLauncher(GeneratorDataSource dataSource,DictGenOptions options) {
        super(dataSource);
        initDefaultGenerators();
        this.options = options;
    }

    @Override
    public void initDefaultGenerators() {

    }

    @Override
    public void generate() {

        var templatePath = dataSource.getTemplatePath();

        File source = new File(options.getAbsoluteFilePath());

        if(!source.exists()){
            log.info("源文件不存在");
            return;
        }

        DictVelocityWrapper.setNamespace("ns_default");
        DictVelocityWrapper.init(templatePath,options);

        try {

            TextResolverFSM fsm = new TextResolverFSM(Files.readString(source.toPath()));
            List<DictElement> parse = fsm.parse();

            var id = new Snowflake(0L,1L);

            for(DictElement el : parse){
                el.setPk(id.nextId()+"");
            }

            options.getOpt().put("dict",parse);
            options.getOpt().put("dictType",options.getDictType());

            Template template = DictVelocityWrapper.getTemplate("element.vm");
            VelocityContext ctx = DictVelocityWrapper.injectContext();
            File dir = new File(options.getOutputPath(),"/dict");
            File target = new File(dir,"dict"+".sql");
            DictVelocityWrapper.mergeAndOutput(template,ctx,target);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
