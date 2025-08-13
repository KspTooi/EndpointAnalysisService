package com.ksptooi.archives;

import com.ksptooi.commons.ConfigFactory;
import com.ksptooi.commons.GeneratorDataSource;
import com.ksptooi.commons.GeneratorLauncher;
import com.ksptooi.dict.DictGenOptions;
import com.ksptooi.dict.DictGeneratorLauncher;
import com.ksptooi.utils.DirectoryTools;

import java.io.File;

public class ApplicationDict {

    public static void main(String[] args) {

        String currentDir = System.getProperty("user.dir");
        File template = DirectoryTools.findTemplate(currentDir, "velocity_dict");
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

        DictGenOptions opt = new DictGenOptions();
        opt.setInputFilePath("D://input.txt");
        opt.setOutputPath("D:/");
        opt.setDictType("SYS3904");

        GeneratorLauncher launcher = new DictGeneratorLauncher(ds,opt);
        launcher.generate();
    }

}
