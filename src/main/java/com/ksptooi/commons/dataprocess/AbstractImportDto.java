package com.ksptooi.commons.dataprocess;

/**
 * 导入数据验证基类
 * 所有导入数据验证类都继承自此基类
 * 所有导入数据验证类都实现validate方法
 */
public abstract class AbstractImportDto {

    /**
     * 验证导入数据
     * @return 验证结果 如果验证通过则返回null 否则返回错误信息
     */
    public abstract String validate();
    
}
