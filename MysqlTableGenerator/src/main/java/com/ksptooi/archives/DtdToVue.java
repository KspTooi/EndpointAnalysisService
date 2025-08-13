package com.ksptooi.archives;

import com.ksptooi.commons.ArtifactJsonOutputProc;
import com.ksptooi.commons.ArtifactPushProcessor;
import com.ksptooi.commons.GenericToolsProc;
import com.ksptooi.ng.MultiTaskLauncher;
import com.ksptooi.commons.VelocityProcessor;
import com.ksptooi.nuclear.datasource.DtdDataSource;
import com.ksptooi.nuclear.vue.CrlfToLfProcessor;
import com.ksptooi.nuclear.vue.UnionFieldsProc;
import com.ksptooi.nuclear.vue.VueTemplateProc;

public class DtdToVue {


    public static void main(String[] args) {

        generate("SpacialRecordFire");

    }

    public static void generate(String dtd){
        MultiTaskLauncher launcher = new MultiTaskLauncher();

        var ds = new DtdDataSource();
        ds.set("dtd.path","MysqlTableGenerator.src.main.java.com.ksptooi.dtd."+dtd);

        var gen = new VueTemplateProc();
        gen.set("view.title","模板管理");
        gen.set("path.root","src.生成预览.");
        gen.set("path.root","src.");

        //gen.set("path.api","-");
        //gen.set("path.model","-");
        //gen.set("path.views","-");
        //gen.set("path.catalog","-");

        //gen.set("api.fname","api");
        //gen.set("mod.fname","mod");
        //gen.set("view.fname","view");
        //gen.set("view.ops.fname","view-ops");

        gen.set("path.api",".api.methods");
        gen.set("path.model",".api.models");
        gen.set("path.views",".views");
        gen.set("path.catalog","special.");

        var velocity = new VelocityProcessor();
        velocity.set("template.dir","velocity_vue");
        velocity.set("template.namespace","ns_confidential");
        velocity.add("silence","true");

        var push = new ArtifactPushProcessor();
        push.set("endpoint.host","127.0.0.1");
        push.set("endpoint.host","192.168.10.42");
        push.set("endpoint.port","59000");

        launcher.add(ds);
        launcher.add(new GenericToolsProc());
        launcher.add(new UnionFieldsProc());
        launcher.add(gen);
        launcher.add(velocity);
        launcher.add(new CrlfToLfProcessor());
        launcher.add(new ArtifactJsonOutputProc());
        launcher.add(push);
        launcher.launch();
    }


}
