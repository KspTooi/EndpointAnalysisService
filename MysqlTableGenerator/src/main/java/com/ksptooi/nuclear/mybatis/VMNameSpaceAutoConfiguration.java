package com.ksptooi.nuclear.mybatis;

import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import com.ksptooi.utils.DateUtils;

public class VMNameSpaceAutoConfiguration extends OptionRegister {

    public VMNameSpaceAutoConfiguration() {
        declareRequire("vm.ns.main","普通模板命名空间");
        declareRequire("vm.ns.real.del","不包含逻辑删除的模板命名空间");
        declareRequire("logic.remove.field","逻辑删除字段名");
    }


    @Override
    public void process(DataSource ds, Artifact artifact) {
        artifact.put("author",require("author"));
        artifact.put("datetime", DateUtils.format(new java.util.Date(), DateUtils.DATE_TIME_PATTERN));
        //获取数据库中所有字段数据
        MysqlDdlProc fieldHelper = (MysqlDdlProc) artifact.get("fieldHelper");

        var nsMain = require("vm.ns.main");
        var nsReal = require("vm.ns.real.del");
        var logicRemoveField = require("logic.remove.field");

        if(fieldHelper.exists(logicRemoveField)){
            artifact.put("template.namespace",nsMain);
        }else {
            artifact.put("template.namespace",nsReal);
        }

    }

    @Override
    public String getName() {
        return "模板命名空间自动配置器";
    }

}
