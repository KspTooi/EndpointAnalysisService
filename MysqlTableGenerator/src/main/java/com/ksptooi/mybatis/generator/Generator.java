package com.ksptooi.mybatis.generator;

import com.ksptooi.mybatis.model.config.MtgGenOptions;
import com.ksptooi.mybatis.model.po.TableField;

import java.util.List;

public interface Generator {

    public boolean enable(MtgGenOptions opt);

    public void generate(MtgGenOptions opt, List<TableField> fields);

    public String getName();
}
