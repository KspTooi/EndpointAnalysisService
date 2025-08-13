package com.ksptooi.nuclear.vue;

import com.ksptooi.commons.parser.DtdParser;
import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import com.ksptooi.utils.DirectoryTools;

public class VueTemplateProc extends OptionRegister {


    public VueTemplateProc(){
        declareRequire("path.root","项目Root","src");
        declareRequire("path.api","Api文件夹","api.methods");
        declareRequire("path.model","Model文件夹","api.models");
        declareRequire("path.views","视图文件夹","views");
        declareRequire("path.catalog","模块分类名","special");
        declareRequire("view.title","视图显示标题","动火记录管理");

        declare("api.fname","指定Api文件名(不含后缀)","emergency-goods");
        declare("mod.fname","指定Mod文件名(不含后缀)","emergency-goods");
        declare("view.fname","指定View文件名(不含后缀)","emergency-goods");
        declare("view.ops.fname","指定Ops文件名(不含后缀)","emergency-goods-ops");
    }

    @Override
    public String getName() {
        return "Vue模板解析器-TEMP";
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

        //构建methods所需参数
        var importFrom = unionPath("@",modelRoot,catalog,moduleName,val("mod.fname"));
        a.put("Api_ImportFrom",importFrom);
        a.put("Api_ApiPath",toJavaFiled(parse.getClassName()));

        //构建views所需参数
        a.put("VD","$");
        a.put("V_ViewName",parse.getClassName());
        a.put("V_ViewTitle",require("view.title"));
        a.put("V_ImportModel",unionPath("@",modelRoot,catalog,moduleName,val("mod.fname")));
        a.put("V_ImportApi",unionPath("@",apiRoot,catalog,moduleName,val("api.fname")));

        //构建operable所需参数
        a.put("O_ViewName",parse.getClassName()+"Operable");
        a.put("O_ViewTitle",require("view.title"));

        a.importOptions(this);

        var curDir = getWorkspace();
        a.addTemplateOutput("methods.ts",unionFile(curDir,val("api.fpath"), val("api.fname.suffix")));
        a.addTemplateOutput("model.vm",unionFile(curDir,val("mod.fpath"), val("mod.fname.suffix")));
        a.addTemplateOutput("operable.vue",unionFile(curDir,val("view.ops.fpath"), val("view.ops.fname.suffix")));
        a.addTemplateOutput("view.vm",unionFile(curDir,val("view.fpath"),  val("view.fname.suffix")));
        clear();
    }



}
