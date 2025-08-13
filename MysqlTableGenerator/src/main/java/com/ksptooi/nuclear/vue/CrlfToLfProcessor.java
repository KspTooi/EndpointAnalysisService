package com.ksptooi.nuclear.vue;

import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class CrlfToLfProcessor extends OptionRegister {

    @Override
    public String getName() {
        return "CRLF-LF转换器";
    }

    @Override
    public void process(DataSource ds, Artifact a) {

        var outputs = a.getOutputs();

        for(var item : outputs){

            File f = new File(item.getVal());
            String s = null;
            try {
                s = Files.readString(f.toPath());
                s = s.replace("\r\n", "\n").replace("\r", "\n");
                Files.write(f.toPath(), s.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

}
