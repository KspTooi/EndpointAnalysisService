package com.ksptooi.nuclear.vue;

import com.ksptooi.commons.parser.DtdParser;
import com.ksptooi.ddl.model.TableDescription;
import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import com.ksptooi.nuclear.vue.models.*;
import com.ksptooi.utils.DirectoryTools;
import com.ksptooi.utils.TextConv;

import java.util.ArrayList;
import java.util.List;

public class VueGeneratorProcessor extends OptionRegister {

    public VueGeneratorProcessor(){
        declareRequire("path.root","项目Root","src");
        declareRequire("path.api","Api文件夹","api.methods");
        declareRequire("path.model","Model文件夹","api.models");
        declareRequire("path.views","视图文件夹","views");
        declareRequire("path.catalog","模块分类名","special");

        declare("api.fname","指定Api文件名(不含后缀)","emergency-goods");
        declare("mod.fname","指定Mod文件名(不含后缀)","emergency-goods");
        declare("view.fname","指定View文件名(不含后缀)","emergency-goods");
        declare("view.ops.fname","指定Ops文件名(不含后缀)","emergency-goods-ops");
    }

    @Override
    public String getName() {
        return "Vue模板解析器";
    }

    @Override
    public void process(DataSource ds, Artifact a) {

        var raw = (DtdParser)ds.getRaw();
        var parse = raw.parse();

        var root = require("path.root");
        var apiRoot = require("path.api");
        var modelRoot = require("path.model");
        var viewsRoot = require("path.views");
        var catalog = require("path.catalog");

        var moduleName = toSnakeMiddle(parse.getClassName());


        //生成API文件路径
        ifNotExists("api.fpath").add(unionPath(root,apiRoot,catalog,moduleName));
        ifNotExists("api.fname").add(unionPath(moduleName));
        ifNotExists("api.fname.suffix").add(unionFile(val("api.fname"),".ts"));

        //生成Model文件路径
        ifNotExists("mod.fpath").add(unionPath(root,modelRoot,catalog,moduleName));
        ifNotExists("mod.fname").add(unionPath(moduleName));
        ifNotExists("mod.fname.suffix").add(unionFile(val("mod.fname"),".ts"));

        //生成views文件路径
        ifNotExists("view.fpath").add(unionPath(root,viewsRoot,catalog,moduleName));
        ifNotExists("view.fname").add(unionPath(moduleName));
        ifNotExists("view.fname.suffix").add(unionFile(val("view.fname"),".vue"));

        //生成views-ops文件路径
        ifNotExists("view.ops.fpath").add(unionPath(root,viewsRoot,catalog,moduleName));
        ifNotExists("view.ops.fname").add(unionPath(moduleName)+"-operable");
        ifNotExists("view.ops.fname.suffix").add(unionFile(val("view.ops.fname"),".vue"));

        //构建Model需要的参数
        //var tsFields = (List<TsField>) a.require("ts.fields");

        //a.put("Mod_ListParams",toModelFields(tsFields));
        //a.put("Mod_ListRequest",new ArrayList<>(tsFields));
        //a.put("Mod_FullData",toModelFullData(tsFields));

        //构建methods所需参数
        var importFrom = unionPath("@",apiRoot,catalog,moduleName,val("mod.fname"));
        a.put("Api_ImportFrom",importFrom);
        a.put("Api_ApiPath",toJavaFiled(parse.getClassName()));

        //构建views所需参数
        a.put("VD","$");
        a.put("V_ViewName",parse.getClassName());
        a.put("V_ViewTitle",require("view.title"));
        a.put("V_ImportModel",unionPath("@",modelRoot,catalog,moduleName,val("mod.fname")));
        a.put("V_ImportApi",unionPath("@",apiRoot,catalog,moduleName,val("api.fname")));

        //a.put("V_DtQuery",toDtQuery(parse));
        //a.put("V_DtShow",toDtShow(parse));

        //构建operable所需参数
        a.put("O_ViewName",parse.getClassName()+"Operable");
        a.put("O_ViewTitle",require("view.title"));
        //a.put("O_UpdateForm", toOpsUpdateForm(parse));
        //a.put("O_UpdateInitVal", toUpdateInitVal(tsFields));
        //a.put("O_FormRules", toOpsFormRules(tsFields));

        a.importOptions(this);

        var curDir = DirectoryTools.getCurrentPath();
        a.addTemplateOutput("methods.ts",unionFile(curDir,val("api.fpath"), val("api.fname.suffix")));
        a.addTemplateOutput("model.vm",unionFile(curDir,val("mod.fpath"), val("mod.fname.suffix")));
        a.addTemplateOutput("operable.vue",unionFile(curDir,val("view.ops.fpath"), val("view.ops.fname.suffix")));
        a.addTemplateOutput("view.vm",unionFile(curDir,val("view.fpath"),  val("view.fname.suffix")));
    }

    private ArrayList<TsField> toModelFields(List<TsField> tsFields) {
        var ret = new ArrayList<TsField>();
        for(var i : tsFields){
            var f = new TsField();
            f.setName(i.getName());
            f.setType(i.getType());
            f.setType(i.getType());
            f.setOptions(" | null | undefined");
            f.setComment(i.getComment());
            ret.add(f);
        }
        ret.add(new TsField("pageNum?","","number",""));
        ret.add(new TsField("pageSize?","","number",""));
        return ret;
    }

    private ArrayList<TsField> toModelFullData(List<TsField> tsFields) {
        var ret = new ArrayList<TsField>();
        for(var i : tsFields){
            var f = new TsField();
            f.setName(i.getName());
            f.setType(i.getType());
            f.setType(i.getType());

            if(i.getOriginType().equals("Date")){
                f.setOptions(" | null | undefined");
            }else {
                f.setOptions("");
            }
            f.setComment(i.getComment());
            ret.add(f);
        }
        return ret;
    }


    //视图列表条件查询
    private ArrayList<ViewDtQueryItem> toDtQuery(TableDescription parse){
        var vDtQuery = new ArrayList<ViewDtQueryItem>();

        for(var f : parse.getFields()){

            var filedName = f.getName();

            if(filedName.contains("id") || filedName.contains("createTime") || filedName.contains("createBy") || filedName.contains("updateTime") || filedName.contains("updateBy") || filedName.contains("delStatus")){
                continue;
            }

            var i = new ViewDtQueryItem();
            i.fieldName = f.getName();
            i.label = f.getComment();
            i.placeholder = f.getComment();
            i.path = f.getName();
            i.type = "input";

            if(f.getType().equals("Date")){
                i.type = "date";
            }

            vDtQuery.add(i);
        }
        return vDtQuery;
    }

    //添加 更新
    private ArrayList<OpsUpdateItem> toOpsUpdateForm(TableDescription parse){
        var vDtQuery = new ArrayList<OpsUpdateItem>();

        for(var f : parse.getFields()){

            var filedName = f.getName();

            if(filedName.contains("createTime") || filedName.contains("createBy") || filedName.contains("updateTime") || filedName.contains("updateBy") || filedName.contains("delStatus")){
                continue;
            }

            var i = new OpsUpdateItem();
            i.fieldName = f.getName();
            i.label = f.getComment();
            i.placeholder = f.getComment();
            i.path = f.getName();
            i.type = "input";

            if(f.getType().equals("Date")){
                i.type = "date";
            }

            vDtQuery.add(i);
        }
        return vDtQuery;
    }

    //OPS表单默认值
    private ArrayList<OpsUpdateInitVal> toUpdateInitVal(List<TsField> parse){
        var vDtQuery = new ArrayList<OpsUpdateInitVal>();

        for(var f : parse){

            var filedName = f.getName();

            if(filedName.contains("createTime") || filedName.contains("createBy") || filedName.contains("updateTime") || filedName.contains("updateBy") || filedName.contains("delStatus")){
                continue;
            }

            var i = new OpsUpdateInitVal();
            i.fieldName = f.getName();
            i.comment = f.getComment();
            i.type = "";
            i.initVal = "''";

            if(f.getOriginType().equals("Date")){
                i.initVal = "undefined as FullData['" + i.getFieldName() +"']";
            }

            vDtQuery.add(i);
        }
        return vDtQuery;
    }

    //OPS表单校验值
    private ArrayList<OpsFormRules> toOpsFormRules(List<TsField> parse){

        var ret = new ArrayList<OpsFormRules>();

        for(var f : parse){

            var filedName = f.getName();

            if(filedName.contains("createTime") || filedName.contains("createBy") || filedName.contains("updateTime") || filedName.contains("updateBy") || filedName.contains("delStatus")){
                continue;
            }
            var v = new OpsFormRules();
            v.fieldName = f.getName();
            v.type = f.getType();
            if(f.getOriginType().equals("Long") || f.getOriginType().equals("Integer") || f.getOriginType().equals("Double") || f.getOriginType().equals("Float") || f.getOriginType().equals("Short")){
                v.type = "number";
            }
            if(f.getName().contains("id") || f.getName().contains("Id")){
                v.type = "string";
            }
            v.required = "true";
            v.hint = "请输入"+f.getComment();
            ret.add(v);
        }
        return ret;
    }

    private ArrayList<ViewDtShowItem> toDtShow(TableDescription parse){
        var vDtShow = new ArrayList<ViewDtShowItem>();

        for(var f : parse.getFields()){

            var filedName = f.getName();

            if(filedName.contains("id") || filedName.contains("createTime") || filedName.contains("createBy") || filedName.contains("updateTime") || filedName.contains("updateBy") || filedName.contains("delStatus")){
                continue;
            }

            var i = new ViewDtShowItem();
            i.title = f.getComment();
            i.key = f.getName();
            vDtShow.add(i);
        }
        return vDtShow;
    }



}
