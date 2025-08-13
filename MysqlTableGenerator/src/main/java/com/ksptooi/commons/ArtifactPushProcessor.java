package com.ksptooi.commons;

import com.ksptooi.association.ForkClient;
import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import com.ksptooi.utils.DirectoryTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * 用于将构建产物推送到Fork端点
 */
public class ArtifactPushProcessor extends OptionRegister {

    private static final Logger log = LoggerFactory.getLogger(ArtifactPushProcessor.class);

    @Override
    public String getName() {
        return "端点推送处理器";
    }

    @Override
    public void process(DataSource ds, Artifact artifact) {

        var host = require("endpoint.host");
        var port = require("endpoint.port");

        ForkClient client = new ForkClient(host,Integer.parseInt(port));
        try {

            client.connect();

            for(var item : artifact.getOutputs()){
                var artFile = new File(item.getVal());
                var curDir = DirectoryTools.getCurrentDirectory();
                var rmtPath = artFile.getAbsolutePath().replace(curDir.getAbsolutePath(),"").replace(artFile.getName(),"");
                //log.info("推送产物:{} 远程路径:{}",artFile.getName(),rmtPath);
                client.pushFile(rmtPath,artFile);
            }

            artifact.getOutputs();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
