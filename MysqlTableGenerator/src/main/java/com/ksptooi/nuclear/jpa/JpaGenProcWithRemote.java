package com.ksptooi.nuclear.jpa;

import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import com.ksptooi.nuclear.mybatis.MybatisGenProcWithRemote;
import com.ksptooi.utils.TextConv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JpaGenProcWithRemote extends OptionRegister {

    private static final Logger log = LoggerFactory.getLogger(MybatisGenProcWithRemote.class);

    public JpaGenProcWithRemote() {
        declareRequire("table.name","表名");

        declareRequire("dir.root","输出根目录名","baodian-back");
        declareRequire("dir.classes","输出根目录名","src.main.java");
        // declareRequire("dir.xml","MapperXML文件输出目录","src.main.resources");
        declareRequire("pkg.root","Root顶级包名","com.ksptooi.app");

        declare("exclude.names","从表名中排除字符串");
        declare("class.name.controller","指定控制器名");
        declare("class.name.service","指定服务名");
        declare("class.name.vo","指定VO名称");
        declare("class.name.po","指定PO名称");
        declare("class.name.dto","指定DTO名称");
        // declare("class.name.mapper","指定Mapper名称");
        declare("class.name.repository","指定Repository名称");

        declare("pkg.name.controller","控制器包名");
        declare("pkg.name.service","指定服务包名");
        declare("pkg.name.vo","指定VO包名");
        declare("pkg.name.po","指定PO包名");
        declare("pkg.name.dto","指定DTO包名");
        // declare("pkg.name.mapper","指定Mapper包名");
        declare("pkg.name.repository","指定Repository包名");

        declare("field.name.controller","控制器字段名");
        declare("field.name.service","服务字段名");
        declare("field.name.vo","VO字段名");
        declare("field.name.po","PO字段名");
        declare("field.name.dto","DTO字段名");
        // declare("field.name.mapper","Mapper字段名");

        declare("pkg.name.business","指定业务分类名称");
        declare("request.mapping.name","指定接口RequestMapping");
        declare("api.tag.name","指定SwaggerApi名称");

        declare("gen.controller","true生成 false不生成");
        declare("gen.service","true生成 false不生成");
        declare("gen.po","true生成 false不生成");
        declare("gen.vo","true生成 false不生成");
        declare("gen.dto","true生成 false不生成");
        // declare("gen.mapper","true生成 false不生成");

        declare("logicRemoveField","逻辑删除字段名");
        declare("logicExists","逻辑存在值");
        declare("logicRemoved","逻辑删除值");
    }

    @Override
    public String getName() {
        return "Jpa生成处理器-Remote";
    }

    @Override
    public void process(DataSource ds, Artifact a) {

        String tableName = (String) a.require("table.name");

        //用于排除某些名称前缀
        for(String exclude:vals("exclude.names")){
            tableName = tableName.replace(exclude,"");
            //set("table.name",tableName);
        }

        String businessName = TextConv.toJavaFiled(tableName).toLowerCase();
        a.put("businessClassName",toJavaClass(tableName));
        var businessClassName = a.require("businessClassName");

        //大写类名
        var nameCtl = ifNotExists("class.name.controller").add(toClassName(tableName, "Controller"));
        var nameSrv = ifNotExists("class.name.service").add(toClassName(tableName, "Service"));
        var nameVo = ifNotExists("class.name.vo").add(toClassName(tableName, "Vo"));
        var namePo = ifNotExists("class.name.po").add(toClassName(tableName, "Po"));
        var nameDto = ifNotExists("class.name.dto").add(toClassName(tableName, "Dto"));
        // var nameMapper = ifNotExists("class.name.mapper").add(toClassName(tableName, "Mapper"));
        var nameRepository = ifNotExists("class.name.repository").add(toClassName(tableName, "Repository"));

        var rootPkg = require("pkg.root");

        //包名
        ifExists("pkg.name.controller").set((old)-> unionPkg(rootPkg,old));
        ifExists("pkg.name.service").set((old)-> unionPkg(rootPkg,old));
        ifExists("pkg.name.vo").set((old)-> unionPkg(rootPkg,old));
        ifExists("pkg.name.po").set((old)-> unionPkg(rootPkg,old));
        ifExists("pkg.name.dto").set((old)-> unionPkg(rootPkg,old));
        // ifExists("pkg.name.mapper").set((old)-> unionPkg(rootPkg,old));
        ifExists("pkg.name.repository").set((old)-> unionPkg(rootPkg,old));

        ifNotExists("pkg.name.controller").add(unionPkg(rootPkg,businessName,"controller"));
        ifNotExists("pkg.name.service").add(unionPkg(rootPkg,businessName,"service"));
        ifNotExists("pkg.name.vo").add(unionPkg(rootPkg,businessName,"model.vo"));
        ifNotExists("pkg.name.po").add(unionPkg(rootPkg,businessName,"model.po"));
        ifNotExists("pkg.name.dto").add(unionPkg(rootPkg,businessName,"model.dto"));
        // ifNotExists("pkg.name.mapper").add(unionPkg(rootPkg,businessName,"mapper"));
        ifNotExists("pkg.name.repository").add(unionPkg(rootPkg,businessName,"repository"));

        //字段名
        ifNotExists("field.name.controller").add(toJavaFiled(nameCtl));
        ifNotExists("field.name.service").add(toJavaFiled(nameSrv));
        ifNotExists("field.name.vo").add(toJavaFiled(nameVo));
        ifNotExists("field.name.po").add(toJavaFiled(namePo));
        ifNotExists("field.name.dto").add(toJavaFiled(nameDto));
        // ifNotExists("field.name.mapper").add(toJavaFiled(nameMapper));
        ifNotExists("field.name.repository").add(toJavaFiled(nameRepository));

        ifNotExists("api.tag.name").add(toJavaClass(tableName));
        ifNotExists("request.mapping.name").add(toJavaFiled(nameCtl.replace("Controller","")));

        String workspace = getWorkspace();
        String dirRoot = require("dir.root");
        String dirClasses = require("dir.classes");
        String dirXml = require("dir.xml");

        String parentClasses = unionPath(workspace,dirRoot,dirClasses);
        String parentXml = unionPath(workspace,dirRoot,dirXml);

        var pkgCtl = require("pkg.name.controller");
        var pkgSrv = require("pkg.name.service");
        var pkgVo = require("pkg.name.vo");
        var pkgPo = require("pkg.name.po");
        var pkgDto = require("pkg.name.dto");
        // var pkgMapper = require("pkg.name.mapper");
        var pkgRepository = require("pkg.name.repository");

        var fileController = new File(unionFile(parentClasses,pkgCtl,nameCtl+".java"));
        var fileService = new File(unionFile(parentClasses,pkgSrv,nameSrv+".java"));

        var fileRepository = new File(unionFile(parentClasses,pkgRepository,nameRepository+".java"));
        // var fileXml = new File(unionFile(parentXml,nameRepository+".xml"));
        var filePo = new File(unionFile(parentClasses,pkgPo,namePo+".java"));


        var fileVo = new File(unionFile(parentClasses,pkgVo,nameVo+".java"));
        var fileVoList = new File(unionFile(parentClasses,pkgVo,"Get"+businessClassName+"ListVo.java"));
        var fileVoGet = new File(unionFile(parentClasses,pkgVo,"Get"+businessClassName+"DetailsVo.java"));

        var fileDto = new File(unionFile(parentClasses,pkgDto,nameDto+".java"));
        var fileDtoList = new File(unionFile(parentClasses,pkgDto,"Get"+businessClassName+"ListDto.java"));
        var fileDtoInsert = new File(unionFile(parentClasses,pkgDto,"Add"+businessClassName+"Dto.java"));
        var fileDtoUpdate = new File(unionFile(parentClasses,pkgDto,"Edit"+businessClassName+"Dto.java"));

        ifNotExists("gen.controller").add("true");
        ifNotExists("gen.service").add("true");
        ifNotExists("gen.po").add("true");
        ifNotExists("gen.vo").add("true");
        ifNotExists("gen.dto").add("true");
        // ifNotExists("gen.mapper").add("true");
        ifNotExists("gen.repository").add("true");

        if(val("gen.controller").equals("true")){
            a.addTemplateOutput("controller.vm",fileController);
        }

        if(val("gen.service").equals("true")){
            a.addTemplateOutput("service.vm",fileService);
        }

        if(val("gen.po").equals("true")){
            a.addTemplateOutput("po.vm",filePo);
        }

        if(val("gen.vo").equals("true")){
            a.addTemplateOutput("vo_list.vm",fileVoList);
            a.addTemplateOutput("vo_get.vm",fileVoGet);
        }

        if(val("gen.dto").equals("true")){
            a.addTemplateOutput("dto_list.vm",fileDtoList);
            a.addTemplateOutput("dto_insert.vm",fileDtoInsert);
            a.addTemplateOutput("dto_update.vm",fileDtoUpdate);
        }

        // if(val("gen.mapper").equals("true")){
        //     a.addTemplateOutput("mapper.vm",fileMapper);
        //     a.addTemplateOutput("mapper_xml.vm",fileXml);
        // }
        if(val("gen.repository").equals("true")){
            a.addTemplateOutput("repository.vm",fileRepository);
        }

        a.importOptionsFormat(this);
        clear();
    }


}
