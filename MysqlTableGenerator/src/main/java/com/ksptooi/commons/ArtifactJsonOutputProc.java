package com.ksptooi.commons;


import com.google.gson.GsonBuilder;
import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import com.ksptooi.utils.DirectoryTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ArtifactJsonOutputProc extends OptionRegister {

    private static final Logger log = LoggerFactory.getLogger(ArtifactJsonOutputProc.class);

    @Override
    public String getName() {
        return "工件数据JSON输出处理器";
    }

    @Override
    public void process(DataSource ds, Artifact artifact) {

        ifNotExists("file.name").add("artifact.art");
        ifNotExists("file.path").add(DirectoryTools.getCurrentPath());

        var file = new File(val("file.path"),val("file.name"));

        if(file.exists()){
            file.delete();
        }

        var gson = new GsonBuilder().setPrettyPrinting().create();
        var json = gson.toJson(artifact);

        try {

            file.createNewFile();
            Files.writeString(file.toPath(), json);
            log.info("已写出工件数据至:{}",file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

}
