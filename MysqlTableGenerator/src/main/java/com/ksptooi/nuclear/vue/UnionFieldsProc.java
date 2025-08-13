package com.ksptooi.nuclear.vue;

import com.google.gson.Gson;
import com.ksptooi.commons.parser.ClassField;
import com.ksptooi.commons.parser.DtdParser;
import com.ksptooi.commons.parser.FieldAnnotation;
import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import com.ksptooi.nuclear.vue.models.UnionFields;
import com.ksptooi.utils.TextConv;

import java.util.*;

public class UnionFieldsProc extends OptionRegister {

    @Override
    public String getName() {
        return "DTD-TS联合字段转换器(前端)";
    }

    @Override
    public void process(DataSource ds, Artifact a) {

        var raw = (DtdParser)ds.getRaw();
        var parse = raw.parse();

        a.put("apiTagName",toJavaClass(parse.getClassName()));

        //构建联合字段
        var ufList = new ArrayList<UnionFields>();

        for(var f : parse.getFields()){

            if(f.hasAnno("Ignore")){
                continue;
            }

            var uf = new UnionFields();
            uf.setName(f.getName());

            //dtd中该字段有注解 则使用注解的label 否则使用整个注释作为label
            uf.setLabel(f.getComment());
            var label = f.getAnno("Label");
            if(label != null){
                uf.setLabel(label.getDefaultVal());
            }

            //TS类型
            uf.setTsType(toTsType(f.getName(),f.getType()));
            var tsType = f.getAnno("TsType");
            if(tsType != null){
                uf.setTsType(tsType.getDefaultVal());
            }

            uf.setJavaType(f.getType());
            uf.setComment(f.getComment());

            //如果是日期需要有额外选项
            uf.setmOptions("");
            if(f.getType().equals("Date")){
                uf.setmOptions(" | null | undefined");
            }

            uf.setRequire("true");
            var notRequire = f.getAnno("NotRequire");
            if(notRequire != null){
                uf.setRequire("false");
            }

            uf.setHint("请填写"+uf.getLabel());
            var hint = f.getAnno("Hint");
            if(hint != null){
                uf.setHint(hint.getDefaultVal());
            }

            uf.setPlaceholder("请填写"+uf.getLabel());

            uf.setDateFormat(null);
            if(f.getType().equals("Date")){
                uf.setDateFormat("yyyy-MM-dd HH:mm:ss");

                var df = f.getAnno("DateFormat");
                if(df != null){
                    uf.setDateFormat(df.getDefaultVal());
                }
            }

            uf.setvType("input");

            if(f.hasAnno("Radio")){
                uf.setvType("radio");
                uf.setTsType("string");
            }

            if(f.hasAnno("Select")){
                uf.setvType("select");
            }

            if(f.getType().equals("Date")){
                uf.setvType("date");
            }

            //设置operable初始值
            uf.setInitVal("''");
            if(f.getType().equals("Date")){
                uf.setInitVal("null");
            }
            if(uf.getTsType().equals("number")) {
                uf.setInitVal("0");
            }

            uf.setListParam(false);
            if(f.hasAnno("ListParam")){
                uf.setListParam(true);
            }

            uf.setListRet(false);
            if(f.hasAnno("ListResult")){
                uf.setListRet(true);
            }

            if(f.hasAnno("ListField")){
                uf.setListParam(true);
                uf.setListRet(true);
            }

            //解析内联字典 格式: 字典1:值 字典2:值 字典3:值
            var iDict = new HashMap<String, String>();
            uf.setInlineDict(iDict);

            if(f.hasAnno("Radio")){
                parseInlineDict(iDict,f.getAnno("Radio").getDefaultVal());
                Gson g = new Gson();
                uf.setInlineDictJson(g.toJson(iDict));
            }

            uf.setDictName(null);
            var dict = f.getAnno("Dict");
            if(dict != null){
                uf.setDictName(dict.getDefaultVal());
            }

            ufList.add(uf);
        }

        a.put("union_fields", ufList);

        collectAllDict(parse.getFields(),a);
    }

    //收集所有字典数据
    private void collectAllDict(List<ClassField> fields,Artifact a){

        Set<String> ret = new HashSet<>();

        for(var f : fields){
            if(f.hasAnno("Dict")){
                ret.add(f.getAnno("Dict").getDefaultVal());
            }
        }

        a.put("v_all_dicts",ret);

        StringBuilder sb = new StringBuilder();
        for (String s : ret) {
            if (!sb.isEmpty()) {
                sb.append(",");
            }
            sb.append(s);
        }
        a.put("v_all_dicts_f",sb.toString());
    }

    private String toTsType(String name,String javaType){

        if(javaType.equals("Long") || javaType.equals("String") || javaType.equals("Date")){
            return "string";
        }

        if(javaType.equals("Integer") || javaType.equals("Short")){
            return "number";
        }

        return "string";
    }

    public void parseInlineDict(HashMap<String, String> map,String dict){
        var items = dict.split(" ");
        for(var i : items){
            var d = i.split(":");
            map.put(d[0],d[1]);
        }
    }


}
