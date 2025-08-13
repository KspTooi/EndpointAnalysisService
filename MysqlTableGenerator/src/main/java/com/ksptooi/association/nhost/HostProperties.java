package com.ksptooi.association.nhost;

import com.ksptooi.ng.MultiTaskLauncher;
import com.ksptooi.utils.DirectoryTools;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HostProperties {

    private File workspace = DirectoryTools.getCurrentDirectory();

    private final Map<String, MultiTaskLauncher> launchers = new ConcurrentHashMap<>();

    private Integer port = 58000;


    public File getWorkspace() {
        return workspace;
    }

    public void setWorkspace(File workspace) {
        this.workspace = workspace;
    }

    public Map<String, MultiTaskLauncher> getLaunchers() {
        return launchers;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
