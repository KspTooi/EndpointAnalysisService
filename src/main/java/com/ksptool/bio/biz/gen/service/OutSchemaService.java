package com.ksptool.bio.biz.gen.service;

import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.exception.BizException;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import com.ksptool.bio.biz.gen.repository.OutSchemaRepository;
import com.ksptool.bio.biz.gen.model.outmodelorigin.OutModelOriginPo;
import com.ksptool.bio.biz.gen.model.outschema.OutSchemaPo;
import com.ksptool.bio.biz.gen.model.outschema.vo.GetOutSchemaListVo;
import com.ksptool.bio.biz.gen.model.outschema.dto.GetOutSchemaListDto;
import com.ksptool.bio.biz.gen.model.outschema.vo.GetOutSchemaDetailsVo;
import com.ksptool.bio.biz.gen.model.outschema.dto.EditOutSchemaDto;
import com.ksptool.bio.biz.gen.model.outschema.dto.AddOutSchemaDto;
import com.ksptool.bio.biz.gen.repository.DataSourceRepository;
import com.ksptool.bio.biz.gen.repository.OutModelOriginRepository;



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


        //如果输入数据源,则查询数据源
        if(dto.getDataSourceId() != null){

            var dataSource = datasourceRepository.findById(dto.getDataSourceId()).orElse(null);

            if(dataSource == null){
                return "新增成功,但未能从数据源导入原始字段,数据源不存在或无权限访问！";
            }
            
            var fields = outModelOriginService.getDataSourceTableFields(dataSource);

            if(fields == null){
                return "新增成功,但未能从数据源导入原始字段,查询数据源表字段失败！";
            }

            var insertFields = new ArrayList<OutModelOriginPo>();

            int seq = 1;
            for(var field : fields){
                field.setOutputSchemaId(insertPo.getId());
                field.setSeq(seq++);
                insertFields.add(field);
            }
            
            //批量保存原始字段
            outModelOriginRepository.saveAll(insertFields);

            //更新输出方案字段数量
            insertPo.setFieldCountOrigin(insertFields.size());
            repository.save(insertPo);

            return "新增成功,已从数据源导入"+insertFields.size()+"个原始字段！";
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

        // 没有配置数据源，直接保存
        if (dto.getDataSourceId() == null) {
            repository.save(updatePo);
            return "修改成功";
        }

        var dataSource = datasourceRepository.findById(dto.getDataSourceId()).orElse(null);
        if (dataSource == null) {
            return "未能同步原始字段,数据源不存在或无权限访问！";
        }

        var latestFields = outModelOriginService.getDataSourceTableFields(dataSource);
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

        return "修改成功,新增"+toInsert.size()+"个原始字段,删除"+toDelete.size()+"个原始字段！";
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

}