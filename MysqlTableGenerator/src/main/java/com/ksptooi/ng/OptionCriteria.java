package com.ksptooi.ng;

import com.ksptooi.ng.criteria.ResetCriteria;

import java.util.List;

public class OptionCriteria {

    private final Option o;

    private String key;
    private final List<Option> l;

    public OptionCriteria(Option o){
        this.o = o;
        this.l = null;
    }

    public OptionCriteria(String key,List<Option> l){
        this.l = l;
        this.o = null;
        this.key = key;
    }

    public void set(String v){

        if(o == null){
            return;
        }

        o.setVal(v);
    }

    public String add(String v){

        if(key == null || l == null){
            return null;
        }

        Option o = new Option();
        o.setKey(key);
        o.setVal(v);
        o.setDefaultVal(v);
        l.add(o);
        return o.getVal();
    }

    public void set(ResetCriteria criteria){
        if(o == null){
            return;
        }
        o.setVal(criteria.reset(o.getVal()));
    }


}
