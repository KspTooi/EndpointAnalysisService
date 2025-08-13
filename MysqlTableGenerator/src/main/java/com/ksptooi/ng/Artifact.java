package com.ksptooi.ng;

import com.ksptooi.utils.TextConv;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class Artifact extends HashMap<String,Object> {

    private final List<Option> outputs = new ArrayList<>();

    public Object require(String k){

        if(!containsKey(k)){
            throw new RuntimeException("工件中不包含:"+k);
        }

        return get(k);
    }

    public void importOptions(Pipeline p){
        List<Option> options = p.getOptions();
        for(Option item : options){
            put(item.getKey(),item.getVal());
        }
    }

    public void importOptionsFormat(Pipeline p){
        List<Option> options = p.getOptions();
        for(Option item : options){
            put(TextConv.pkgToCamel(item.getKey()),item.getVal());
        }
    }

    public void addTemplateOutput(String vm,String path){
        Option o = new Option();
        o.setKey(vm);
        o.setVal(path);
        outputs.add(o);
    }

    public void addTemplateOutput(String vm, File path){
        Option o = new Option();
        o.setKey(vm);
        o.setVal(path.getAbsolutePath());
        outputs.add(o);
    }

    public List<Option> getOutputs(){
        return outputs;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        for (Entry<String,Object> entry:this.entrySet()){
            builder.append(entry.getKey()).append("::").append(entry.getValue()).append("\r\n");
        }
        return builder.toString();
    }
}
