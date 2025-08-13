package com.ksptooi.psme.controller;

import com.ksptooi.Application;
import com.ksptooi.psm.configset.ConfigSet;
import com.ksptooi.psm.mapper.ConfigSetMapper;
import com.ksptooi.psm.processor.OnActivated;
import com.ksptooi.psm.processor.RequestHandler;
import com.ksptooi.psm.processor.ServiceUnit;
import com.ksptooi.psm.processor.ShellRequest;
import com.ksptooi.psm.utils.aio.color.GreenDye;
import com.ksptooi.psm.utils.aio.color.RedDye;
import com.ksptooi.psme.service.WorkspaceService;
import com.ksptooi.uac.core.annatatiotion.Param;
import jakarta.inject.Inject;

@ServiceUnit("nc::proc::workspace")
public class NuclearWorkspaceCtl {

    private final ConfigSetMapper mapper = Application.getInjector().getInstance(ConfigSetMapper.class);
    private final ConfigSet configSet = Application.getInjector().getInstance(ConfigSet.class);

    private String prefix = "nc.proc.workspace.";

    @Inject
    private WorkspaceService service;

    @OnActivated
    public void activated(){
        System.out.println("NuclearWorkspace控制器加载成功");
    }

    @RequestHandler("nc workspace set")
    public void setWorkspace(ShellRequest request, @Param("name")String name,@Param("path")String path){
        service.setWorkspace(request,name,path);
    }

    @RequestHandler("nc workspace rm")
    public void removeWorkspace(ShellRequest request, @Param("name")String name){
        request.getCable()
                .dye(GreenDye.pickUp)
                .w(service.removeWorkspace(name));
    }

    @RequestHandler("nc workspace list")
    public void listWorkspace(ShellRequest request){

        var workspaceList = service.getWorkspaceList();

        for(var item : workspaceList.entrySet()){

            var cable = request.getCable();

            cable.dye(GreenDye.pickUp)
                    .w(item.getKey().replace(prefix,""))
                    .w("\t")
                    .w(item.getValue())
                    .w("\t");

            if(service.isValid(item.getValue())){
                cable.w("有效");
            }else {
                cable.dye(RedDye.pickUp)
                        .w("无效")
                        .dye(GreenDye.pickUp);
            }

            cable.nextLine().flush();
        }

    }




}
