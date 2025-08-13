package com.ksptooi.nuclear.datasource;

import com.ksptooi.commons.parser.DtdParser;
import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import com.ksptooi.utils.DirectoryTools;
import com.ksptooi.utils.TextConv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DtdDataSource extends OptionRegister implements DataSource {

    @Override
    public String getName() {
        return "DTD文件数据源";
    }

    public DtdDataSource(){
        declareRequire("dtd.path","dtd文件名(无需添加后缀)","Example");
    }

    @Override
    public void init() {
    }

    @Override
    public Object getRaw() {

        var relativePath = require("dtd.path");

        var f = new File(getWorkspace(), TextConv.pkgToPath(relativePath,".java"));

        if(!f.exists()){
            throw new RuntimeException("文件不存在 " + f.getAbsolutePath());
        }

        try {

            ifNotExists("fsm.comment.policy").add("-1");
            var parser = new DtdParser(Files.readString(f.toPath()));
            parser.setCommentPolicy(Integer.parseInt(val("fsm.comment.policy")));
            return parser;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(DataSource ds, Artifact artifact) {

    }

}
