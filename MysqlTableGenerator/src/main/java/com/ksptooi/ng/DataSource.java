package com.ksptooi.ng;

import java.util.List;

public interface DataSource extends Pipeline{

    /**
     * 用于初始化数据源
     */
    public void init();

    /**
     * 从数据源读取Material
     */
    public Object getRaw();

}
