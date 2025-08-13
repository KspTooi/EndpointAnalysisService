package com.ksptooi.psme.service;

import com.ksptooi.Application;
import com.ksptooi.guice.annotations.Unit;
import com.ksptooi.psm.configset.ConfigSet;
import com.ksptooi.psm.mapper.ConfigSetMapper;
import com.ksptooi.psm.processor.ShellRequest;
import com.ksptooi.psm.utils.aio.color.GreenDye;
import com.ksptooi.psm.utils.aio.color.RedDye;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Unit
public class WorkspaceService {

    private final ConfigSetMapper mapper = Application.getInjector().getInstance(ConfigSetMapper.class);
    private final ConfigSet configSet = Application.getInjector().getInstance(ConfigSet.class);
    private String prefix = "nc.proc.workspace.";


    public void setWorkspace(ShellRequest sr, String name, String path){

        try{
            ensureAvailable(new File(path));
        }catch (Exception e){
            sr.getCable().dye(RedDye.pickUp).w(e.getMessage());
            return;
        }

        var key = prefix + name;
        configSet.createOrUpdate(key,path);
        sr.getCable().dye(GreenDye.pickUp).w("确定");
        return;
    }


    public Map<String,File> getWorkspaceList() {
        var byPrefix = configSet.getByPrefix(prefix);
        var ret = new HashMap<String, File>();
        for(var item:byPrefix){
            ret.put(item.getKey(),new File(item.getVal()));
        }
        return ret;
    }

    public String removeWorkspace(String name){

        var byKey = mapper.getByKey(prefix + name);

        if(byKey == null){
            return "确定";
        }

        mapper.removeById(byKey.get(0).getId()+"");
        return "确定";
    }

    //确保工作空间可用
    public void ensureAvailable(File f) throws Exception{
        if(!f.exists()){
            throw new Exception("路径不存在");
        }
        if(!f.isDirectory()){
            throw new Exception("路径不是一个文件夹");
        }
        if(!f.canRead()){
            throw new Exception("路径不可读");
        }
        if(!f.canWrite()){
            throw new Exception("路径不可写");
        }
    }

    public boolean isValid(File f){
        if(!f.exists()){
            return false;
        }
        if(!f.isDirectory()){
            return false;
        }
        if(!f.canRead()){
            return false;
        }
        if(!f.canWrite()){
            return false;
        }
        return true;
    }


}
