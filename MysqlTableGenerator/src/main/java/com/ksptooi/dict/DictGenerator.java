package com.ksptooi.dict;

import com.ksptooi.ddl.model.DDLGenOptions;

public interface DictGenerator {

    public void generate(DictGenOptions options);

    public String getName();
}
