package com.ksptooi.psme.controller;

import com.ksptooi.Application;
import com.ksptooi.association.ForkEndpoint;
import com.ksptooi.psm.configset.ConfigSet;
import com.ksptooi.psm.mapper.ConfigSetMapper;
import com.ksptooi.psm.processor.*;
import com.ksptooi.psm.utils.aio.color.BlueDye;
import com.ksptooi.psm.utils.aio.color.GreenDye;
import com.ksptooi.psm.utils.aio.color.OrangeDye;
import com.ksptooi.psm.utils.aio.color.RedDye;
import com.ksptooi.psme.service.NetworkService;
import com.ksptooi.psme.service.WorkspaceService;
import com.ksptooi.uac.core.annatatiotion.Param;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

@ServiceUnit("nc::proc")
public class NuclearCtl {

    private final ConfigSetMapper mapper = Application.getInjector().getInstance(ConfigSetMapper.class);
    private final ConfigSet configSet = Application.getInjector().getInstance(ConfigSet.class);

    @Inject
    private WorkspaceService workspaceService;

    @Inject
    private NetworkService networkService;

    @OnActivated
    public void activated(){
        System.out.println("NuclearProc加载成功");
    }

    @RequestHandler("nc set host")
    public void setHost(ShellRequest request, @Param("host")String host){

        if(StringUtils.isBlank(host)){
            request.getCable().dye(RedDye.pickUp).w("参数不合法");
            return;
        }
        if(!host.contains(":")){
            request.getCable().dye(RedDye.pickUp).w("参数不合法");
            return;
        }

        try{
            var ip = host.split(":")[0];
            var port = Integer.parseInt(host.split(":")[1]);

            configSet.createOrUpdate("nc.remote.host",ip);
            configSet.createOrUpdate("nc.remote.port",port+"");
            request.getCable().dye(GreenDye.pickUp).w("确定");

        }catch (Exception e){
            e.printStackTrace();
            request.getCable().dye(RedDye.pickUp).w("参数不合法");
        }

    }

    @RequestHandler("nc set")
    public void setLaunch(ShellRequest request,@Param("key")String key,@Param("val")String val){
        var prefix = "nc.launcher.settings.";
        configSet.createOrUpdate(prefix + key,val);
    }

    @RequestHandler("nc set rm")
    public void setRemove(ShellRequest request,@Param("key")String key){
        var prefix = "nc.launcher.settings."+key;
        var byPrefix = configSet.getByPrefix(prefix);
        if(byPrefix == null || byPrefix.isEmpty()){
            request.getCable().dye(GreenDye.pickUp).w("确定");
            return;
        }
        configSet.remove(byPrefix.get(0).getId()+"");
        request.getCable().dye(GreenDye.pickUp).w("确定");
    }

    @RequestHandler("nc set all")
    public void getAllSettings(ShellRequest request){
        var byPrefix = configSet.getByPrefix("nc.launcher.settings.");
        request.getCable().nextLine().dye(GreenDye.pickUp);
        for(var item : byPrefix){
            request.getCable().w(item.getKey().replace("nc.launcher.settings.","")).w("\t").w(item.getVal()).nextLine();
        }
    }

    @RequestHandler("nc require all")
    public void getLauncherDeclare(ShellRequest request){

        var byPrefix = configSet.getByPrefix("nc.launcher");

        if(byPrefix == null || byPrefix.isEmpty()){
            request.getCable().dye(RedDye.pickUp).w("没有配置 nc.launcher").nextLine().flush();
            return;
        }

        networkService.connect(request);
        request.getCable().dye(BlueDye.pickUp).w("等待远程主机响应...").nextLine().flush();

        try {

            var launcherDeclare = networkService.getClient().getLauncherDeclare(byPrefix.get(0).getVal());

            if(launcherDeclare.isEmpty()){
                request.getCable().dye(BlueDye.pickUp).w("无数据").nextLine().flush();
                return;
            }

            var cable = request.getCable().nextLine();

            for(var entry : launcherDeclare.entrySet()){

                if(entry.getValue().isEmpty()){
                    continue;
                }

                cable.dye(BlueDye.pickUp).w("来自管线:").w(entry.getKey()).w("的参数(").w(entry.getValue().size()).w(")").nextLine();

                if(entry.getValue().isEmpty()){
                    cable.w("无").nextLine();
                    continue;
                }

                cable.dye(OrangeDye.pickUp).w("值\t\t注释\t例子\t是否必填").nextLine().flush();
                for(var p : entry.getValue()){
                    cable.w(p.getKey()).w("\t\t").w(p.getTitle()).w("\t").w(p.getExample()).w("\t").w(p.getRequired()).nextLine();
                }

                cable.nextLine().flush();
            }

        } catch (IOException e) {
            request.getCable().dye(RedDye.pickUp).w(e.getMessage()).nextLine().flush();
            e.printStackTrace();
        }

    }


    @RequestHandler("nc launch")
    public void ncLaunch(ShellRequest request){

        networkService.connect(request);
        request.getCable().dye(BlueDye.pickUp).w("正在准备启动器参数...").nextLine().flush();
        //准备参数
        Map<String,String> set = new HashMap<>();
        var byPrefix = configSet.getByPrefix("nc.launcher.settings.");

        for(var item : byPrefix){
            set.put(item.getKey().replace("nc.launcher.settings.",""),item.getVal());
        }

        //准备本地工作空间
        var workspaceList = workspaceService.getWorkspaceList();

        if(workspaceList.isEmpty()){
            request.getCable().dye(RedDye.pickUp).w("没有设定工作空间").nextLine();
            request.getCable().dye(RedDye.pickUp).w(":nc.workspace").nextLine();
            return;
        }

        var allInvalid = true;

        for(var entry : workspaceList.entrySet()){
            if(workspaceService.isValid(entry.getValue())) {
                allInvalid = false;
                break;
            }
        }

        if(allInvalid){
            request.getCable().dye(RedDye.pickUp).w("缺少有效的工作空间!").nextLine();
            return;
        }

        try {

            //创建工作空间
            var rmtWorkspace = networkService.getClient().requestCreateTempDir();
            request.getCable().dye(BlueDye.pickUp).w("配置远程工作空间:"+rmtWorkspace).nextLine().flush();

            //文件列表
            var fileList = new ArrayList<File>();

            for(var entry : workspaceList.entrySet()){
                var dir = entry.getValue();
                if(workspaceService.isValid(dir)){
                    var dirFiles = dir.listFiles();
                    if(dirFiles != null){
                        fileList.addAll(List.of(dirFiles));
                    }
                }
            }

            //过滤文件夹
            Iterator<File> iterator = fileList.iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().isFile()) {
                    iterator.remove();
                }
            }

            int i = 0;

            //传输文件到远程工作空间
            for(var lf : fileList){
                networkService.getClient().pushFile(rmtWorkspace,lf);
                i++;
                request.getCable().dye(BlueDye.pickUp).w(i + " of " + fileList.size()).nextLine().flush();
            }

            request.getCable().dye(BlueDye.pickUp).w("后处理启动器参数:"+rmtWorkspace).nextLine().flush();
            request.getCable().dye(BlueDye.pickUp).w("------------------------").nextLine().flush();
            networkService.getClient().pushSettings(set);

            for(var item : byPrefix){
                request.getCable().dye(GreenDye.pickUp).w(item.getKey().replace("nc.launcher.settings.","")).w("\t").w(item.getVal()).nextLine().flush();
            }
            request.getCable().nextLine().flush();

            request.getCable().dye(BlueDye.pickUp).w("等待远程主机响应...").nextLine().flush();
            String result = networkService.getClient().requestLaunch();
            request.getCable().dye(GreenDye.pickUp).w(result).nextLine().flush();

        } catch (Exception e) {
            request.getCable().dye(RedDye.pickUp).w(e.getMessage()).nextLine().flush();
            e.printStackTrace();
        }

    }




}
