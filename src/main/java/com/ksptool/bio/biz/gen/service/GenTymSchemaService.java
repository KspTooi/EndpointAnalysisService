package com.ksptool.bio.biz.gen.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.gen.model.gentymschema.GenTymSchemaPo;
import com.ksptool.bio.biz.gen.model.gentymschema.dto.AddGenTymSchemaDto;
import com.ksptool.bio.biz.gen.model.gentymschema.dto.EditGenTymSchemaDto;
import com.ksptool.bio.biz.gen.model.gentymschema.dto.GetGenTymSchemaListDto;
import com.ksptool.bio.biz.gen.model.gentymschema.vo.GetGenTymSchemaDetailsVo;
import com.ksptool.bio.biz.gen.model.gentymschema.vo.GetGenTymSchemaListVo;
import com.ksptool.bio.biz.gen.repository.GenTymSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class GenTymSchemaService {

    @Autowired
    private GenTymSchemaRepository repository;

    public PageResult<GetGenTymSchemaListVo> getGenTymSchemaList(GetGenTymSchemaListDto dto) {
        GenTymSchemaPo query = new GenTymSchemaPo();
        assign(dto, query);

        Page<GenTymSchemaPo> page = repository.getGenTymSchemaList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetGenTymSchemaListVo> vos = as(page.getContent(), GetGenTymSchemaListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addGenTymSchema(AddGenTymSchemaDto dto) {
        GenTymSchemaPo insertPo = as(dto, GenTymSchemaPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editGenTymSchema(EditGenTymSchemaDto dto) throws BizException {
        GenTymSchemaPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetGenTymSchemaDetailsVo getGenTymSchemaDetails(CommonIdDto dto) throws BizException {
        GenTymSchemaPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetGenTymSchemaDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeGenTymSchema(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}