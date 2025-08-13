package com.ksptooi.ddl;

import com.ksptooi.commons.AbstractGeneratorLauncher;
import com.ksptooi.commons.GeneratorDataSource;
import com.ksptooi.ddl.generator.*;
import com.ksptooi.ddl.model.DDLGenOptions;
import com.ksptooi.mybatis.generator.Generator;
import com.ksptooi.utils.DDLVelocityWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DDLGeneratorLauncher extends AbstractGeneratorLauncher {

    private final static Logger log = LoggerFactory.getLogger(DDLGeneratorLauncher.class);

    private final DDLGenOptions genOptions;
    private final List<DDLGenerator> generators = new ArrayList<>();

    public DDLGeneratorLauncher(GeneratorDataSource ds, DDLGenOptions opt){
        super(ds);
        initDataSource();
        initDefaultGenerators();
        this.genOptions = opt;
        genOptions.setTemplatePath(dataSource.getTemplatePath());
    }


    @Override
    public void initDefaultGenerators() {
        generators.add(new AutoConfigurationGenerator());
        generators.add(new EntityResolverGenerator());
        generators.add(new PostTypeAutoConfigurationGenerator());
        generators.add(new PostLoggerPrinterGenerator());
        generators.add(new TemplateOutputGenerator());
        generators.add(new AutoExecuteGenerator());
    }

    @Override
    public void generate() {


        DDLVelocityWrapper.init(genOptions.getTemplatePath(),genOptions);
        DDLVelocityWrapper.setNamespace(genOptions.getTemplateNameSpace());

        genOptions.setConnection(connection);
        genOptions.setDatabaseTools(dbTool);

        int c = 0;

        for(DDLGenerator item : generators){
            item.generate(genOptions);
            c++;
        }

        log.info("已运行 {} of {} 个生成器",c,generators.size());

    }
}
