package com.ksptooi.mybatis.autoconfig;

import com.ksptooi.mybatis.model.config.MtgGenOptions;
import com.ksptooi.mybatis.model.po.TableField;
import com.ksptooi.utils.TextConv;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.util.List;

public class PacketNameAutoConfigurator implements AutoConfigurator{

    private final Logger log = LoggerFactory.getLogger(PacketNameAutoConfigurator.class);

    @Override
    public void doAutomaticConfiguration(Connection conn, MtgGenOptions opt, List<TableField> fields) {

        assert fields != null && !fields.isEmpty();
        assert !StringUtils.isBlank(opt.getPacketName());
        assert !StringUtils.isBlank(opt.getTableName());

        String pkgName = opt.getPacketName();

        //pkgName以.结尾
        if(pkgName.endsWith(".")){
            pkgName = pkgName.substring(0,pkgName.length() - 1);
            log.info("[自动配置]重命名PacketName为: {}",pkgName);
            opt.setPacketName(pkgName);
        }

        if(StringUtils.isBlank(opt.getPkgNameController())){
            String pkgNameCtl = pkgName + ".controller";
            log.info("[自动配置]PkgNameController为: {}",pkgNameCtl);
            opt.setPkgNameController(pkgNameCtl);
        }else {
            String pkg = TextConv.unionPacket(pkgName,opt.getPkgNameController());
            log.info("[指定配置]PkgNameController为: {}",pkg);
            opt.setPkgNameController(pkg);
        }

        if(StringUtils.isBlank(opt.getPkgNameService())){
            String pkgNameService = pkgName + ".services";
            log.info("[自动配置]PkgNameService为: {}",pkgNameService);
            opt.setPkgNameService(pkgNameService);
        }else {
            String pkg = TextConv.unionPacket(pkgName,opt.getPkgNameService());
            log.info("[指定配置]PkgNameService为: {}",pkg);
            opt.setPkgNameService(pkg);
        }

        if(StringUtils.isBlank(opt.getPkgNameServiceImpl())){

            String pkgNameServiceImpl = pkgName + ".services";

            //开启impl
            if(opt.isWithImpl()){
                pkgNameServiceImpl = pkgName + ".services.impl";
            }

            log.info("[自动配置]PkgNameServiceImpl为: {}",pkgNameServiceImpl);
            opt.setPkgNameServiceImpl(pkgNameServiceImpl);
        }else {
            String pkg = TextConv.unionPacket(pkgName,opt.getPkgNameServiceImpl());
            log.info("[指定配置]PkgNameServiceImpl为: {}",pkg);
            opt.setPkgNameServiceImpl(pkg);
        }

        if(StringUtils.isBlank(opt.getPkgNamePo())){
            String pkgNamePo = pkgName + ".model.po";
            log.info("[自动配置]PkgNamePo为: {}",pkgNamePo);
            opt.setPkgNamePo(pkgNamePo);
        }else {
            String pkg = TextConv.unionPacket(pkgName,opt.getPkgNamePo());
            log.info("[指定配置]PkgNamePo为: {}",pkg);
            opt.setPkgNamePo(pkg);
        }

        if(StringUtils.isBlank(opt.getPkgNameVo())){
            String pkgNameVo = pkgName + ".model.vo";
            log.info("[自动配置]PkgNameVo为: {}",pkgNameVo);
            opt.setPkgNameVo(pkgNameVo);
        }else {
            String pkg = TextConv.unionPacket(pkgName,opt.getPkgNameVo());
            log.info("[指定配置]PkgNameVo为: {}",pkg);
            opt.setPkgNameVo(pkg);
        }

        if(StringUtils.isBlank(opt.getPkgNameDto())){
            String pkgNameDto = pkgName + ".model.dto";
            log.info("[自动配置]PkgNameDto为: {}",pkgNameDto);
            opt.setPkgNameDto(pkgNameDto);
        }else {
            String pkg = TextConv.unionPacket(pkgName,opt.getPkgNameDto());
            log.info("[指定配置]PkgNameDto为: {}",pkg);
            opt.setPkgNameDto(pkg);
        }

        if(StringUtils.isBlank(opt.getPkgNameMapper())){
            String pkgNameMapper = pkgName + ".mapper";
            log.info("[自动配置]PkgNameMapper为: {}",pkgNameMapper);
            opt.setPkgNameMapper(pkgNameMapper);
        }else {
            String pkg = TextConv.unionPacket(pkgName,opt.getPkgNameMapper());
            log.info("[指定配置]PkgNameMapper为: {}",pkg);
            opt.setPkgNameMapper(pkg);
        }

    }


    @Override
    public String getName() {
        return "包名称自动配置器";
    }
}
