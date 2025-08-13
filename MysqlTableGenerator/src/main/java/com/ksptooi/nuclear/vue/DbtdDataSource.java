package com.ksptooi.nuclear.vue;

import com.ksptooi.ddl.generator.EntityResolveFSM;
import com.ksptooi.ng.*;
import com.ksptooi.utils.DirectoryTools;
import com.ksptooi.utils.TextConv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class DbtdDataSource extends OptionRegister implements DataSource {

    @Override
    public String getName() {
        return "DBTD文件数据源";
    }

    public DbtdDataSource(){
        declareRequire("","");
    }


    @Override
    public void init() {
    }

    @Override
    public Object getRaw() {

        var relativePath = require("dbtd.path");

        var f = new File(DirectoryTools.getCurrentDirectory(), TextConv.pkgToPath(relativePath,".java"));

        if(!f.exists()){
            throw new RuntimeException("文件不存在 " + f.getAbsolutePath());
        }

        try {

            var commentPolicy = require("fsm.comment.policy");

            var content = Files.readString(f.toPath());

            var fsm = new EntityResolveFSM(content);
            fsm.setCommentPolicy(Integer.parseInt(commentPolicy));
            return fsm;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(DataSource ds, Artifact artifact) {

    }

}
