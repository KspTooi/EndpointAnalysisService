package com.ksptool.bio.biz.gen.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.core.service.AttachService;
import com.ksptool.bio.biz.gen.common.assemblybp.collector.MysqlCollector;
import com.ksptool.bio.biz.gen.common.assemblybp.collector.VelocityBlueprintCollector;
import com.ksptool.bio.biz.gen.common.assemblybp.converter.StaticPolyConv;
import com.ksptool.bio.biz.gen.common.assemblybp.core.AssemblyFactory;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.PolyField;
import com.ksptool.bio.biz.gen.common.assemblybp.projector.Projector;
import com.ksptool.bio.biz.gen.common.assemblybp.projector.VelocityProjector;
import com.ksptool.bio.biz.gen.common.assemblybp.utils.NamesTool;
import com.ksptool.bio.biz.gen.model.datsource.DataSourcePo;
import com.ksptool.bio.biz.gen.model.outmodelorigin.OutModelOriginPo;
import com.ksptool.bio.biz.gen.model.outschema.OutSchemaPo;
import com.ksptool.bio.biz.gen.model.outschema.dto.AddOutSchemaDto;
import com.ksptool.bio.biz.gen.model.outschema.dto.EditOutSchemaDto;
import com.ksptool.bio.biz.gen.model.outschema.dto.GetOutSchemaListDto;
import com.ksptool.bio.biz.gen.model.outschema.vo.GetOutSchemaDetailsVo;
import com.ksptool.bio.biz.gen.model.outschema.vo.GetOutSchemaListVo;
import com.ksptool.bio.biz.gen.model.scm.ScmPo;
import com.ksptool.bio.biz.gen.model.tymschema.TymSchemaPo;
import com.ksptool.bio.biz.gen.model.tymschemafield.TymSchemaFieldPo;
import com.ksptool.bio.biz.gen.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Slf4j
@Service
public class OutSchemaService {

    @Autowired
    private OutSchemaRepository repository;

    @Autowired
    private DataSourceRepository datasourceRepository;

    @Autowired
    private OutModelOriginService outModelOriginService;

    @Autowired
    private OutModelOriginRepository outModelOriginRepository;

    @Autowired
    private ScmRepository scmRepository;

    @Autowired
    private AttachService attachService;

    @Autowired
    private ScmService scmService;

    @Autowired
    private TymSchemaRepository tymsRepository;

    @Autowired
    private TymSchemaFieldRepository tymsfRepository;

    @Autowired
    private OutModelPolyRepository outModelPolyRepository;

    /**
     * 查询输出方案列表
     *
     * @param dto 查询参数
     * @return 输出方案列表
     */
    public PageResult<GetOutSchemaListVo> getOutSchemaList(GetOutSchemaListDto dto) {
        OutSchemaPo query = new OutSchemaPo();
        assign(dto, query);

        Page<OutSchemaPo> page = repository.getOutSchemaList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetOutSchemaListVo> vos = as(page.getContent(), GetOutSchemaListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增输出方案
     *
     * @param dto 新增参数
     */
    @Transactional(rollbackFor = Exception.class)
    public String addOutSchema(AddOutSchemaDto dto) {
        OutSchemaPo insertPo = as(dto, OutSchemaPo.class);
        insertPo.setFieldCountOrigin(0);
        insertPo.setFieldCountPoly(0);

        //先保存输出方案，这样才能获取到主键ID
        insertPo = repository.save(insertPo);


        //如果输入数据源和表名,则查询数据源表字段
        if (dto.getDataSourceId() != null && StringUtils.isNotBlank(dto.getTableName())) {

            var dataSource = datasourceRepository.findById(dto.getDataSourceId()).orElse(null);

            if (dataSource == null) {
                return "新增成功,但未能从数据源导入原始字段,数据源不存在或无权限访问！";
            }

            var fields = outModelOriginService.getDataSourceTableFields(dataSource, dto.getTableName());

            if (fields == null) {
                return "新增成功,但未能从数据源导入原始字段,查询数据源表字段失败！";
            }

            var insertFields = new ArrayList<OutModelOriginPo>();

            int seq = 1;
            for (var field : fields) {
                field.setOutputSchemaId(insertPo.getId());
                field.setSeq(seq++);
                insertFields.add(field);
            }

            //批量保存原始字段
            outModelOriginRepository.saveAll(insertFields);

            //更新输出方案字段数量
            insertPo.setFieldCountOrigin(insertFields.size());
            repository.save(insertPo);

            return "新增成功,已从数据源导入" + insertFields.size() + "个原始字段！";
        }

        return "新增成功,未导入任何原始字段！";
    }

    /**
     * 编辑输出方案
     *
     * @param dto 编辑参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public String editOutSchema(EditOutSchemaDto dto) throws BizException {
        OutSchemaPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);

        // 没有配置数据源或表名，直接保存
        if (dto.getDataSourceId() == null || StringUtils.isBlank(dto.getTableName())) {
            repository.save(updatePo);
            return "修改成功";
        }

        var dataSource = datasourceRepository.findById(dto.getDataSourceId()).orElse(null);
        if (dataSource == null) {
            return "未能同步原始字段,数据源不存在或无权限访问！";
        }

        var latestFields = outModelOriginService.getDataSourceTableFields(dataSource, dto.getTableName());
        if (latestFields == null) {
            return "未能同步原始字段,查询数据源表字段失败！";
        }

        // 已存在的原始字段，以字段名为 key
        List<OutModelOriginPo> existingFields = outModelOriginRepository.getOmoByOutputSchemaId(dto.getId());
        Map<String, OutModelOriginPo> existingMap = existingFields.stream()
                .collect(Collectors.toMap(OutModelOriginPo::getName, f -> f));

        // 最新字段名集合，用于检测数据库中已删除的字段
        java.util.Set<String> latestNames = latestFields.stream()
                .map(OutModelOriginPo::getName)
                .collect(Collectors.toSet());

        // 删除数据库中已不存在的字段
        List<OutModelOriginPo> toDelete = existingFields.stream()
                .filter(f -> !latestNames.contains(f.getName()))
                .collect(Collectors.toList());
        if (!toDelete.isEmpty()) {
            outModelOriginRepository.deleteAll(toDelete);
        }

        // 新增数据库中不存在的字段，已存在的跳过（保留用户可能已修改的数据）
        List<OutModelOriginPo> toInsert = new ArrayList<>();
        int maxSeq = existingFields.stream().mapToInt(OutModelOriginPo::getSeq).max().orElse(0);
        for (var field : latestFields) {
            if (existingMap.containsKey(field.getName())) {
                continue;
            }
            field.setOutputSchemaId(dto.getId());
            field.setSeq(++maxSeq);
            toInsert.add(field);
        }
        if (!toInsert.isEmpty()) {
            outModelOriginRepository.saveAll(toInsert);
        }

        // 更新字段数量
        int newCount = existingFields.size() - toDelete.size() + toInsert.size();
        updatePo.setFieldCountOrigin(newCount);
        repository.save(updatePo);

        return "修改成功,新增" + toInsert.size() + "个原始字段,删除" + toDelete.size() + "个原始字段！";
    }

    /**
     * 查询输出方案详情
     *
     * @param dto 查询参数
     * @return 输出方案详情
     * @throws BizException 业务异常
     */
    public GetOutSchemaDetailsVo getOutSchemaDetails(CommonIdDto dto) throws BizException {
        OutSchemaPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetOutSchemaDetailsVo.class);
    }

    /**
     * 删除输出方案元素
     *
     * @param dto 删除参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeOutSchema(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

    /**
     * 执行输出方案
     *
     * @param dto 执行参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void executeOutSchema(CommonIdDto dto) throws BizException {

        //查询输出方案
        OutSchemaPo outSchemaPo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("执行输出方案失败,数据不存在或无权限访问."));

        //查询聚合模型
        var ompPos = outModelPolyRepository.getByOutputSchemaId(outSchemaPo.getId());

        if (ompPos == null || ompPos.isEmpty()) {
            throw new BizException("执行输出方案失败,输出方案下没有聚合模型,请先创建聚合模型.");
        }

        //查询输入与输出SCM
        ScmPo inputScmPo = scmRepository.findById(outSchemaPo.getInputScmId())
                .orElseThrow(() -> new BizException("执行输出方案失败,输入SCM不存在或无权限访问."));

        ScmPo outputScmPo = scmRepository.findById(outSchemaPo.getOutputScmId())
                .orElseThrow(() -> new BizException("执行输出方案失败,输出SCM不存在或无权限访问."));

        //查询数据源
        DataSourcePo dataSourcePo = datasourceRepository.findById(outSchemaPo.getDataSourceId())
                .orElseThrow(() -> new BizException("执行输出方案失败,数据源不存在或无权限访问."));

        //查询类型映射方案
        TymSchemaPo tymsPo = tymsRepository.findById(outSchemaPo.getTypeSchemaId())
                .orElseThrow(() -> new BizException("执行输出方案失败,类型映射方案不存在或无权限访问."));

        //查询类型映射方案字段
        List<TymSchemaFieldPo> tymsfPos = tymsfRepository.getTymSfByTymSid(tymsPo.getId());

        //准备工作空间
        var workSpaceName = "gen_workspace_" + outSchemaPo.getName();
        var workSpacePath = attachService.getAttachLocalPath(Paths.get(workSpaceName));
        var workSpaceInputPath = workSpacePath.resolve("input");
        var workSpaceOutputPath = workSpacePath.resolve("output");

        //不存在创建
        if (!Files.exists(workSpacePath)) {
            try {
                Files.createDirectories(workSpacePath);
            } catch (IOException e) {
                log.error("创建工作空间失败: {} 路径: {}", e.getMessage(), workSpacePath.toString(), e);
                throw new BizException("创建工作空间失败: " + e.getMessage());
            }
        }

        //先检出两个SCM
        scmService.pullFromScm(inputScmPo, workSpaceInputPath.toString());
        scmService.pullFromScm(outputScmPo, workSpaceOutputPath.toString());

        var iAppendPath = outSchemaPo.getBaseInput().trim();
        var oAppendPath = outSchemaPo.getBaseOutput().trim();

        //绝对路径转换为相对路径
        if (iAppendPath.startsWith("/")) {
            iAppendPath = iAppendPath.substring(1);
        }

        if (oAppendPath.startsWith("/")) {
            oAppendPath = oAppendPath.substring(1);
        }

        //蓝图输入路径
        var iBpPath = workSpaceInputPath.resolve(iAppendPath);

        //投影输出路径
        var oPrjPath = workSpaceOutputPath.resolve(oAppendPath);

        //配置AssemblyBlueprint 直接创建Mysql采集器(这里复用"输出方案"中绑定的"数据源")
        MysqlCollector coll = new MysqlCollector();
        coll.setUrl(dataSourcePo.getUrl());
        coll.setUsername(dataSourcePo.getUsername());
        coll.setPassword(dataSourcePo.getPassword());
        coll.setDatabase(dataSourcePo.getDbSchema());


        //创建蓝图采集器、聚合转换器、投影仪
        VelocityBlueprintCollector blueprintCollector = new VelocityBlueprintCollector();

        //使用静态字段的聚合转换器以织入自定义的聚合模型
        StaticPolyConv converter = new StaticPolyConv();

        //插入所有ompPos为polyField
        for (var ompPo : ompPos) {
            var pf = new PolyField();

            //设置标准名称 omo中是下划线命名,需要转换为大驼峰命名
            pf.setStdName(NamesTool.toPascalCase(ompPo.getName()));
            pf.setType(ompPo.getKind());
            pf.setComment(ompPo.getRemark());

            //处理必填 0:否 1:是
            pf.setRequired(false);

            if (ompPo.getRequire() == 1) {
                pf.setRequired(true);
            }

            pf.setSeq(ompPo.getSeq());

            //向PF中注入OMP专有的附加字段
            pf.put("omp_length", ompPo.getLength());
            pf.put("omp_require", ompPo.getRequire());

            //处理PCJ 如果PCJ中有ADD EDIT LQ LW 则设置为true,否则设置为false
            pf.put("omp_add", false);
            pf.put("omp_edit", false);
            pf.put("omp_lq", false);
            pf.put("omp_lw", false);

            if (ompPo.getPolicyCrudJson().contains("ADD")) {
                pf.put("omp_add", true);
            }
            if (ompPo.getPolicyCrudJson().contains("EDIT")) {
                pf.put("omp_edit", true);
            }
            if (ompPo.getPolicyCrudJson().contains("LQ")) {
                pf.put("omp_lq", true);
            }
            if (ompPo.getPolicyCrudJson().contains("LW")) {
                pf.put("omp_lw", true);
            }

            //处理PQ
            pf.put("omp_pq", ompPo.getPolicyQuery());

            //处理PV
            pf.put("omp_pv", ompPo.getPolicyView());
            converter.addStaticField(pf);
        }

        //配置投影仪
        Projector projector = new VelocityProjector();

        //创建AssemblyFactory
        AssemblyFactory factory = new AssemblyFactory();
        factory.setCollector(coll);
        factory.setCollector(blueprintCollector);
        factory.setConverter(converter);
        factory.setProjector(projector);

        //启用投影参数日志
        projector.enableProjectorMap(true);

        //设置输入和输出路径(使用绝对路径)
        factory.setInputBasePath(iBpPath.toString());
        factory.setOutputBasePath(oPrjPath.toString());

        //选择要收集的表
        factory.selectTables(outSchemaPo.getTableName());

        //需移除的表前缀
        factory.removeTablePrefixes(outSchemaPo.getRemoveTablePrefix());
        factory.setOverwriteEnabled(true);

        factory.putGlobalVar("modelName", outSchemaPo.getModelName());

        //执行AssemblyBlueprint流水线
        factory.execute();

        //将输出目录推送到输出SCM
        scmService.pushToScm(outputScmPo, oPrjPath.toString());
    }
}