package com.ksptooi.ddl.generator;

import com.ksptooi.ddl.model.DDLGenOptions;
import com.ksptooi.ddl.model.TableDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * 使用状态机将实体类解析为可用的"数据描述对象"
 */
public class EntityResolverGenerator implements DDLGenerator{


    private static final Logger log = LoggerFactory.getLogger(EntityResolverGenerator.class);

    @Override
    public void generate(DDLGenOptions options) {

        File entitiesPath = new File(options.getAbsoluteEntitiesDir());
        File[] entities = entitiesPath.listFiles((dir, name) -> name.endsWith(".java"));

        if(!options.getSpecifyEntities().isEmpty()){
            entities = entitiesPath.listFiles((dir,name) -> {
                String n = name.replace(".java","");
                return options.getSpecifyEntities().contains(n);
            });
        }


        if(entities == null){
            throw new RuntimeException("无法从路径获取到实体类:"+entitiesPath.getAbsolutePath());
        }

        int c = 0;

        for (File entity : entities){
            try {
                c++;
                log.info("解析 {} of {} - {}",c,entities.length,entity.getName());
                String read = Files.readString(entity.toPath());
                EntityResolveFSM fsm = new EntityResolveFSM(read);
                fsm.setCommentPolicy(options.getEntityCommentResolverPolicy());
                options.getTableDescriptions().add(fsm.parse());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public String getName() {
        return "实体类解析生成器";
    }

}
