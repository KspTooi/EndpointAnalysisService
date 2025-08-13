package com.ksptooi.mybatis.generator;

import com.ksptooi.mybatis.model.config.MtgGenOptions;
import com.ksptooi.mybatis.model.po.TableField;
import com.ksptooi.utils.TextConv;
import com.ksptooi.utils.VelocityWrapper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.List;

public class GeneratorExtraEntities implements Generator{

    @Override
    public boolean enable(MtgGenOptions opt) {
        return opt.isGenPo();
    }


    @Override
    public void generate(MtgGenOptions opt, List<TableField> fields) {


        Template t = VelocityWrapper.getTemplate("po.vm");
        VelocityContext vc = VelocityWrapper.injectContext(opt, fields);

        File out = new File(opt.getOutputPath(), TextConv.pkgToPath(opt.getPkgNamePo()) + "\\"+opt.getPoName() + ".java");

        VelocityWrapper.mergeAndOutput(t,vc,out);

        if(opt.isGenVo()){

            String tableName = opt.getTableName();
            String clazzName = TextConv.toJavaClass(tableName);

            String listVo = "List"+opt.getVoName();
            String listDto = "List"+opt.getDtoName();
            String insertDto = "Insert"+opt.getDtoName();
            String updateDto = "Update"+opt.getDtoName();
            String getOne = "Get"+opt.getVoName();


            //生成额外的5个VO DTO
            t = VelocityWrapper.getTemplate("vo.vm");
            vc.put("voName", listVo);
            out = new File(opt.getOutputPath(), TextConv.pkgToPath(opt.getPkgNameVo()) + "\\"+listVo + ".java");
            VelocityWrapper.mergeAndOutput(t,vc,out);

            vc.put("dtoName", listDto);
            t = VelocityWrapper.getTemplate("dto.vm");
            out = new File(opt.getOutputPath(), TextConv.pkgToPath(opt.getPkgNameDto()) + "\\"+listDto + ".java");
            VelocityWrapper.mergeAndOutput(t,vc,out);

            vc.put("dtoName", insertDto);
            t = VelocityWrapper.getTemplate("dto.vm");
            out = new File(opt.getOutputPath(), TextConv.pkgToPath(opt.getPkgNameDto()) + "\\"+insertDto + ".java");
            VelocityWrapper.mergeAndOutput(t,vc,out);

            vc.put("dtoName", updateDto);
            t = VelocityWrapper.getTemplate("dto.vm");
            out = new File(opt.getOutputPath(), TextConv.pkgToPath(opt.getPkgNameDto()) + "\\"+updateDto + ".java");
            VelocityWrapper.mergeAndOutput(t,vc,out);

            t = VelocityWrapper.getTemplate("vo.vm");
            vc.put("voName", getOne);
            out = new File(opt.getOutputPath(), TextConv.pkgToPath(opt.getPkgNameVo()) + "\\"+getOne + ".java");
            VelocityWrapper.mergeAndOutput(t,vc,out);

        }
    }

    @Override
    public String getName() {
        return "额外实体类生成器";
    }
}
