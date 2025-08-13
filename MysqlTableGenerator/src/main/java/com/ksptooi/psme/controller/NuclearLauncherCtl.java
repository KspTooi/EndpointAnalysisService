package com.ksptooi.psme.controller;

import com.ksptooi.Application;
import com.ksptooi.association.nclient.NuclearClient;
import com.ksptooi.psm.configset.ConfigSet;
import com.ksptooi.psm.mapper.ConfigSetMapper;
import com.ksptooi.psm.processor.OnActivated;
import com.ksptooi.psm.processor.RequestHandler;
import com.ksptooi.psm.processor.ServiceUnit;
import com.ksptooi.psm.processor.ShellRequest;
import com.ksptooi.psm.utils.aio.color.GreenDye;
import com.ksptooi.psm.utils.aio.color.OrangeDye;
import com.ksptooi.psm.utils.aio.color.RedDye;
import com.ksptooi.psme.service.NetworkService;
import com.ksptooi.psme.service.WorkspaceService;
import com.ksptooi.uac.core.annatatiotion.Param;
import jakarta.inject.Inject;

import java.io.IOException;

@ServiceUnit("nc::proc::launcher")
public class NuclearLauncherCtl {

    private final ConfigSetMapper mapper = Application.getInjector().getInstance(ConfigSetMapper.class);
    private final ConfigSet configSet = Application.getInjector().getInstance(ConfigSet.class);


    @Inject
    private NetworkService networkService;

    @OnActivated
    public void activated(){
        System.out.println("NuclearLauncher控制器加载成功");
    }

    @RequestHandler("nc launcher list")
    public void listLauncher(ShellRequest request){

        networkService.connect(request);

        var client = networkService.getClient();

        try {
            networkService.connect(request);
            var launchers = networkService.getClient().getLaunchers();
            request.getCable().dye(GreenDye.pickUp).w("全部可用的启动器").nextLine().flush();
            request.getCable().dye(OrangeDye.pickUp).w(launchers).nextLine().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @RequestHandler("nc launcher set")
    public void setLauncher(ShellRequest request, @Param("launcher")String launcher){
        var prefix = "nc.launcher.settings.";
        configSet.createOrUpdate(prefix + "nc.launcher",launcher);
        request.getCable().dye(GreenDye.pickUp).w("确定");
    }

    @RequestHandler("nc launcher get")
    public void getLauncher(ShellRequest request){

        var prefix = "nc.launcher.settings.nc.launcher";

        var val = configSet.valOf(prefix);

        if(val == null){
            request.getCable().dye(RedDye.pickUp).w("未配置");
            return;
        }

        request.getCable().dye(GreenDye.pickUp).w(val);
    }


}
