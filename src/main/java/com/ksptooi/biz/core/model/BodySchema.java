package com.ksptooi.biz.core.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class BodySchema {

    //原名
    private String schemaName;

    //参数列表
    private List<BodySchemaParam> params;
    
}
