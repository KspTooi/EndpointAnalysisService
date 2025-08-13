package com.ksptooi.psme;

import com.ksptooi.psm.subsystem.OnInstalled;
import com.ksptooi.psm.subsystem.SubSystem;
import com.ksptooi.psm.subsystem.SubSystemEntry;

@SubSystemEntry(name = "NuclearEndpoint",version = "3.0A")
public class PsmSubSystem extends SubSystem {


    @OnInstalled
    public void onActivated() {
        renderBanner();
    }

    public void renderBanner(){
        System.out.println("欢迎使用NuclearEndpoint V3.0A");
    }

}
