package com.ksptooi.ng;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MultiTaskLauncher {

    private static final Logger log = LoggerFactory.getLogger(MultiTaskLauncher.class);

    private DataSource dataSource = null;
    private final List<Pipeline> pipelines = new ArrayList<>();

    private volatile Artifact lastArtifact;

    public void add(Pipeline p){

        if(p instanceof DataSource){

            if(dataSource != null){
                throw new RuntimeException("当前启动器错误的配置了多个数据源,请检查配置.");
            }

            dataSource = (DataSource) p;
            return;
        }

        pipelines.add(p);
    }


    public void launch(){

        if(pipelines.isEmpty()){
            log.info("没有执行任何操作.");
        }

        ensureDataSourceExists();

        //初始化数据源
        dataSource.init();

        Artifact artifact = new Artifact();

        StringBuilder b = new StringBuilder();

        List<String> keys = new ArrayList<>();

        //执行数据源
        dataSource.process(dataSource,artifact);

        //执行管线
        for (int i = 0; i < pipelines.size(); i++) {

            String nodeName = pipelines.get(i).getName();

            keys.clear();
            keys.addAll(artifact.keySet());

            int count = artifact.size();

            System.out.println("正在执行节点:"+nodeName+"\r\n");

            pipelines.get(i).process(dataSource,artifact);

            if(count != artifact.size()){
                System.out.println("节点"+nodeName+" 向工件中添加了"+ (artifact.size() - count) + "个值");
                b.append(nodeName).append("(").append((artifact.size() - count)).append(")").append("--->");
                continue;
            }

            b.append(pipelines.get(i).getName()).append("--->");
        }

        b.append("工件(").append(artifact.size()).append(")");
        lastArtifact = artifact;
        System.out.println("管线节点总览");
        System.out.println(b.toString());
    }

    private void ensureDataSourceExists(){
        if(dataSource == null){
            throw new RuntimeException("没有为启动器配置数据源");
        }
    }

    public int getNodeCount(){
        return pipelines.size();
    }

    public void setGlobalOptions(Map<String,String> m){
        for(var e : m.entrySet()){
            setGlobalOptions(e.getKey(),e.getValue());
        }
    }

    public void setGlobalOptions(String k,String v){
        dataSource.set(k,v);
        for(var proc : pipelines){
            proc.set(k,v);
        }
    }

    public Artifact getLastArtifact(){
        return lastArtifact;
    }

    public Map<String,List<OptionDeclare>> getPipelineDeclare(){
        LinkedHashMap<String,List<OptionDeclare>> ret = new LinkedHashMap<>();
        if(dataSource!=null){
            ret.put(dataSource.getName(),dataSource.getOptionDeclare());
        }
        for(var proc : pipelines){
            ret.put(proc.getName(),proc.getOptionDeclare());
        }
        return ret;
    }

    public void clear(){
        for(var p : pipelines){
            p.clear();
        }
    }

}
