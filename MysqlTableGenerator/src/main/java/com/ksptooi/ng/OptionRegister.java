package com.ksptooi.ng;

import com.ksptooi.utils.DirectoryTools;
import com.ksptooi.utils.TextConv;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public abstract class OptionRegister extends TextConv implements Pipeline {

    public List<Option> options = new ArrayList<>();
    public final List<OptionDeclare> optionDeclare = new ArrayList<>();

    public OptionRegister(){
    }

    public void require(String... ks){
        for(String k : ks){
            require(k);
        }
    }

    public String require(String k){

        Option option = getOption(k);

        if(option == null){
            throw new RuntimeException("\r\n获取选项时出现错误 Option不存在\r\n详细值:"+k);
        }

        if(StringUtils.isBlank((String)option.getVal())){
            throw new RuntimeException("\r\n获取选项时出现错误 Option值为空\r\n详细值:"+k);
        }

        return option.getVal();
    }

    public void print(String k){

        var vals = vals(k);

        if(vals!=null){
            vals.forEach(System.out::println);
        }

    }


    public void add(String k,String v){
        Option o = new Option();
        o.setKey(k);
        o.setVal(v);
        o.setDefaultVal(v);
        options.add(o);
    }

    public boolean addIfNotPresent(String k,String v){
        if(!isExists(k)){
            Option o = new Option();
            o.setKey(k);
            o.setVal(v);
            o.setDefaultVal(v);
            options.add(o);
            return true;
        }
        return false;
    }

    public Option getOption(String k){

        if(isMulti(k)){
            throw new RuntimeException("获取Option时出现错误 "+k+" 对应到多个Option");
        }

        for(Option item : options){
            if(item.getKey().equals(k)){
                return item;
            }
        }
        return null;
    }

    public List<String> vals(String k){

        if(!isExists(k)){
            return new ArrayList<>();
        }

        List<String> ret = new ArrayList<>();

        for(Option i : options){
            if(i.getKey().equals(k)){
               ret.add(i.getVal());
            }
        }
        return ret;
    }


    public String val(String k){

        if(isMulti(k)){
            throw new RuntimeException("获取Option时出现错误 "+k+" 对应到多个Option");
        }

        try{
            return getOption(k).getVal();
        }catch (Exception ex){
            return null;
            //throw new RuntimeException("获取OptionVal时出现错误 Option不存在 Key:"+k);
        }
    }

    @Override
    public Pipeline set(String k, String v) {

        if(isMulti(k)){
            throw new RuntimeException("设置Option时出现错误 Key:"+k+"对应到多个值");
        }

        Option option = getOption(k);

        if(option == null){
            add(k,v);
            return this;
            //throw new RuntimeException("设置Option时出现错误 Option不存在 Key:"+k);
        }

        option.setVal(v);
        return this;
    }

    /**
     * 判断某个key对应的OPT是否有多个
     */
    public boolean isMulti(String k){
        int c = 0;
        for (Option option : options) {
            if (option.getKey().equals(k)) {
                c++;
            }
        }
        return c > 1;
    }

    public boolean isExists(String k){
        for (Option option : options) {
            if (option.getKey().equals(k)) {
                return true;
            }
        }
        return false;
    }


    public OptionCriteria ifNotExists(String k){

        if(!isExists(k)){
            return new OptionCriteria(k,options);
        }

        return new OptionCriteria(null);
    }

    public OptionCriteria ifExists(String k){

        if(isExists(k)){
            Option option = getOption(k);
            return new OptionCriteria(option);
        }

        return new OptionCriteria(null);
    }

    @Override
    public List<Option> getOptions() {
        return options;
    }

    public String getWorkspace(){
        ifNotExists("g.workspace").add(DirectoryTools.getCurrentPath());
        return val("g.workspace");
    }

    public File getWorkspaceDir(){
        return new File(getWorkspace());
    }

    public void clear(){
        options.clear();
    }

    public void declareRequire(String k,String title){
        var d = new OptionDeclare();
        d.setKey(k);
        d.setTitle(title);
        d.setRequired(true);
        optionDeclare.add(d);
    }

    public void declareRequire(String k,String title,String example){
        var d = new OptionDeclare();
        d.setKey(k);
        d.setTitle(title);
        d.setExample(example);
        d.setRequired(true);
        optionDeclare.add(d);
    }

    public void declare(String k,String title){
        var d = new OptionDeclare();
        d.setKey(k);
        d.setTitle(title);
        optionDeclare.add(d);
    }

    public void declare(String k,String title,String example){
        var d = new OptionDeclare();
        d.setKey(k);
        d.setTitle(title);
        d.setExample(example);
        optionDeclare.add(d);
    }

    public List<OptionDeclare> getOptionDeclare(){
        return optionDeclare;
    }

}
