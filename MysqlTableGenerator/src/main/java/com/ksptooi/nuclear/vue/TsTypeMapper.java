package com.ksptooi.nuclear.vue;

import com.ksptooi.commons.parser.DtdParser;
import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import com.ksptooi.nuclear.vue.models.TsField;

import java.util.ArrayList;

public class TsTypeMapper extends OptionRegister {

    @Override
    public String getName() {
        return "TBTD-TS数据类型转换器";
    }

    @Override
    public void process(DataSource ds, Artifact a) {

        var raw = (DtdParser)ds.getRaw();
        var parse = raw.parse();

        var ret = new ArrayList<TsField>();

        for(var item : parse.getFields()){

            if(item.getName().contains("createTime") || item.getName().contains("createBy") || item.getName().contains("updateTime") || item.getName().contains("updateBy") || item.getName().contains("delStatus")){
                continue;
            }

            var f = new TsField();
            f.setName(item.getName());
            f.setOptions("");
            f.setComment(item.getComment());
            f.setOriginType(item.getType());
            ret.add(f);

            var type = item.getType();

            if(type.equals("Long") || type.equals("String") || type.equals("Date") || type.equals("Text")){
                f.setType("string");
                continue;
            }

            if(type.equals("Integer") || type.equals("Short")){
                f.setType("number");
                continue;
            }

            f.setType("string");

        }

        a.put("ts.fields", ret);

    }

}
