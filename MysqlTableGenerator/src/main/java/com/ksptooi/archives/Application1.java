package com.ksptooi.archives;

import com.ksptooi.commons.ConfigFactory;
import com.ksptooi.mybatis.generator.GeneratorExtraEntities;
import com.ksptooi.mybatis.model.config.MtgGenOptions;
import com.ksptooi.commons.GeneratorDataSource;
import com.ksptooi.mybatis.MtGenerator;
import com.ksptooi.utils.DirectoryTools;
import java.io.File;

public class Application1 {

    public static void main(String[] args) {

        generateCode("tb_excel_templates");

    }

    private static void generateCode(String tb){
        String currentDir = System.getProperty("user.dir");
        File template = DirectoryTools.findTemplate(currentDir, "velocity");
        assert template!=null;

        GeneratorDataSource ds = ConfigFactory.datasource()
                .driver("com.mysql.cj.jdbc.Driver")
                .host("192.168.10.200:3306")
                .username("root")
                .password("root")
                .params("?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8")
                .dbName("industry_security_platform")
                .templatePath(template.getAbsolutePath())
                .build();

        String ns = "ns_confidential"; //模板命名空间 - 普通
        //String ns = "ns_private_no_logic_delete"; //模板命名空间 - 不包含逻辑删除

        MtgGenOptions opt = ConfigFactory.config()
                .tableName(tb)
                .genController(true)
                .genService(true)
                .genPo(true)
                .genVo(true)
                .genDto(true)
                .genMapper(true)
                .addNameExclude("tb_","sys_","QRTZ_","qrtz_") //排除表前缀
                .packetName("com.baodian.back")
                .packetNameController(".controller")
                .packetNameService(".service")
                .packetNameServiceImpl(".service")
                .packetNameMapper(".mapper")
                .packetNamePo(".models.mv")
                .packetNameVo(".models.mv")
                .packetNameDto(".models.mv")
                .projectName("baodian-back")
                .silence(true)//为true不输出详细生成日志
                .namespace(ns) //模板命名空间
                .put("logicRemoveField","del_status") //逻辑删除字段
                .put("logicRemoved","1") //逻辑删除值
                .put("logicExists","0") //逻辑存在值
                .addGenerator(new GeneratorExtraEntities())
                .build();

        new MtGenerator(ds,opt).generate();
    }

}
