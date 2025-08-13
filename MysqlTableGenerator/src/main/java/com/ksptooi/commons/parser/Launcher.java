package com.ksptooi.commons.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Launcher {

    public static void main(String[] args) throws IOException {

        File f = new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\java\\com\\ksptooi\\dbtd\\EmergencyPlan.java");
        String read = Files.readString(f.toPath());

        DtdParser fsm = new DtdParser(read);
        fsm.setCommentPolicy(1);

        var parser = new DtdParser(read);
        var parse = parser.parse();
        System.out.println(parse);
    }

}
