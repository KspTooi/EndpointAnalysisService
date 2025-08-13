package com.ksptooi.mybatis.autoconfig;

import com.ksptooi.mybatis.model.config.MtgGenOptions;
import com.ksptooi.mybatis.model.po.TableField;
import com.ksptooi.utils.TextConv;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;

public class EntityNameAutoConfigurator implements AutoConfigurator {

    private final Logger log = LoggerFactory.getLogger(EntityNameAutoConfigurator.class);

    @Override
    public void doAutomaticConfiguration(Connection conn, MtgGenOptions opt, List<TableField> fields) {

        assert fields != null && !fields.isEmpty();
        assert !StringUtils.isBlank(opt.getPacketName());
        assert !StringUtils.isBlank(opt.getTableName());

        //用于排除某些名称前缀
        String tableName = opt.getTableName();

        for(String exclude:opt.getExcludeNames()){
            tableName = tableName.replace(exclude,"");
        }

        final String clazzName = TextConv.toJavaClass(tableName);

        if(StringUtils.isBlank(opt.getControllerName())){
            final String ctlName = clazzName + "Controller";
            log.info("[自动配置]ControllerName为: {}",ctlName);
            opt.setControllerName(ctlName);
        }

        if(StringUtils.isBlank(opt.getServiceName())){

            String srvName = clazzName + "Service";

            //开启了Impl
            if(opt.isWithImpl()){
                srvName = "I" + clazzName + "Service";
            }

            log.info("[自动配置]ServiceName为: {}",srvName);
            opt.setServiceName(srvName);
        }

        if(StringUtils.isBlank(opt.getServiceImplName())){
            String srvName = clazzName + "Service";

            //开启了Impl
            if(opt.isWithImpl()){
                srvName = clazzName + "ServiceImpl";
            }

            log.info("[自动配置]ServiceImplName为: {}",srvName);
            opt.setServiceImplName(srvName);
        }

        if(StringUtils.isBlank(opt.getPoName())){
            final String poName = clazzName + "Po";
            log.info("[自动配置]PoName为: {}",poName);
            opt.setPoName(poName);
        }

        if(StringUtils.isBlank(opt.getVoName())){
            final String voName = clazzName + "Vo";
            log.info("[自动配置]VoName为: {}",voName);
            opt.setVoName(voName);
        }

        if(StringUtils.isBlank(opt.getDtoName())){
            final String dtoName = clazzName + "Dto";
            log.info("[自动配置]DtoName为: {}",dtoName);
            opt.setDtoName(dtoName);
        }


        if(StringUtils.isBlank(opt.getMapperName())){
            final String mapperName = clazzName + "Mapper";
            log.info("[自动配置]MapperName为: {}",mapperName);
            opt.setMapperName(mapperName);
        }

    }

    @Override
    public String getName() {
        return "类名称自动配置器";
    }

}
