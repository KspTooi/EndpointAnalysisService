package com.ksptool.bio.biz.outschema.service;

import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.exception.BizException;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import java.util.Optional;
import com.ksptool.bio.biz.outschema.repository.OutSchemaRepository;
import com.ksptool.bio.biz.outschema.model.OutSchemaPo;
import com.ksptool.bio.biz.outschema.model.vo.GetOutSchemaListVo;
import com.ksptool.bio.biz.outschema.model.dto.GetOutSchemaListDto;
import com.ksptool.bio.biz.outschema.model.vo.GetOutSchemaDetailsVo;
import com.ksptool.bio.biz.outschema.model.dto.EditOutSchemaDto;
import com.ksptool.bio.biz.outschema.model.dto.AddOutSchemaDto;


@Service
public class OutSchemaService {

    @Autowired
    private OutSchemaRepository repository;

    public PageResult<GetOutSchemaListVo> getOutSchemaList(GetOutSchemaListDto dto){
        OutSchemaPo query = new OutSchemaPo();
        assign(dto,query);

        Page<OutSchemaPo> page = repository.getOutSchemaList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetOutSchemaListVo> vos = as(page.getContent(), GetOutSchemaListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addOutSchema(AddOutSchemaDto dto){
        OutSchemaPo insertPo = as(dto,OutSchemaPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editOutSchema(EditOutSchemaDto dto) throws BizException {
        OutSchemaPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetOutSchemaDetailsVo getOutSchemaDetails(CommonIdDto dto) throws BizException {
        OutSchemaPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetOutSchemaDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeOutSchema(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}