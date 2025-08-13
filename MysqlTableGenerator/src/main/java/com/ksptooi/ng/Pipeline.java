package com.ksptooi.ng;

import java.util.List;

public interface Pipeline {

    /**
     * 获取管线名称
     */
    public String getName();

    /**
     * 获取当前管线的全部可用选项
     */
    public List<Option> getOptions();

    /**
     * 设置选项的值
     */
    public Pipeline set(String optionKey,String v);

    public void process(DataSource ds,Artifact artifact);

    public List<OptionDeclare> getOptionDeclare();

    public void clear();

}
