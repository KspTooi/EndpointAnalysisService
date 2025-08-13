package com.ksptooi.psme.service;

import com.ksptooi.Application;
import com.ksptooi.association.nclient.NuclearClient;
import com.ksptooi.psm.configset.ConfigSet;
import com.ksptooi.psm.mapper.ConfigSetMapper;
import com.ksptooi.psm.processor.ShellRequest;
import com.ksptooi.psm.utils.aio.color.BlueDye;
import com.ksptooi.psm.utils.aio.color.RedDye;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class NetworkService {

    private final ConfigSetMapper mapper = Application.getInjector().getInstance(ConfigSetMapper.class);
    private final ConfigSet configSet = Application.getInjector().getInstance(ConfigSet.class);

    private NuclearClient client = null;

    public boolean connect(ShellRequest request){

        var host = configSet.valOf("nc.remote.host");
        var port = configSet.valOf("nc.remote.port");

        if(StringUtils.isBlank(host) || StringUtils.isBlank(port)){
            request.getCable().dye(RedDye.pickUp).w("没有指定远程地址").nextLine();
            request.getCable().dye(RedDye.pickUp).w(":nc.remote.host").nextLine();
            request.getCable().dye(RedDye.pickUp).w(":nc.remote.port").nextLine();
            throw new RuntimeException("没有指定远程地址");
        }

        try {

            if(client == null || !client.isAlive()) {
                request.getCable().dye(BlueDye.pickUp).w("正在连接:"+host+":"+port).nextLine();
                client = new NuclearClient(host,Integer.parseInt(port));
            }else {
                return true;
                //request.getCable().dye(BlueDye.pickUp).w("获取现有连接").nextLine();
            }

            //request.getCable().dye(BlueDye.pickUp).w("成功").nextLine();

        } catch (IOException e) {
            request.getCable().dye(RedDye.pickUp).w("连接错误");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return true;
    }

    public NuclearClient getClient(){
        return client;
    }


}
