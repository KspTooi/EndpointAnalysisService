package com.ksptooi.commons;

import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import org.apache.commons.lang3.StringUtils;

public class GenericToolsProc extends OptionRegister {
    @Override
    public String getName() {
        return "通用工具处理器";
    }

    @Override
    public void process(DataSource ds, Artifact artifact) {
        artifact.put("strings", new StringsWrapper());
    }

}
