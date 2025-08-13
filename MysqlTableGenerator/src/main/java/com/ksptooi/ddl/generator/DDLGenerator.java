package com.ksptooi.ddl.generator;

import com.ksptooi.ddl.model.DDLGenOptions;
import com.ksptooi.mybatis.model.config.MtgGenOptions;
import com.ksptooi.mybatis.model.po.TableField;

import java.util.List;

public interface DDLGenerator {

    public void generate(DDLGenOptions options);

    public String getName();
}
