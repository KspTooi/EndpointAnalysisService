package com.ksptool.bio.biz.gentymschemafield.service;

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
import com.ksptool.bio.biz.gentymschemafield.repository.GenTymSchemaFieldRepository;
import com.ksptool.bio.biz.gentymschemafield.model.GenTymSchemaFieldPo;
import com.ksptool.bio.biz.gentymschemafield.model.vo.GetGenTymSchemaFieldListVo;
import com.ksptool.bio.biz.gentymschemafield.model.dto.GetGenTymSchemaFieldListDto;
import com.ksptool.bio.biz.gentymschemafield.model.vo.GetGenTymSchemaFieldDetailsVo;
import com.ksptool.bio.biz.gentymschemafield.model.dto.EditGenTymSchemaFieldDto;
import com.ksptool.bio.biz.gentymschemafield.model.dto.AddGenTymSchemaFieldDto;


@Service
public class GenTymSchemaFieldService {

    @Autowired
    private GenTymSchemaFieldRepository repository;

    public PageResult<GetGenTymSchemaFieldListVo> getGenTymSchemaFieldList(GetGenTymSchemaFieldListDto dto){
        GenTymSchemaFieldPo query = new GenTymSchemaFieldPo();
        assign(dto,query);

        Page<GenTymSchemaFieldPo> page = repository.getGenTymSchemaFieldList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetGenTymSchemaFieldListVo> vos = as(page.getContent(), GetGenTymSchemaFieldListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addGenTymSchemaField(AddGenTymSchemaFieldDto dto){
        GenTymSchemaFieldPo insertPo = as(dto,GenTymSchemaFieldPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editGenTymSchemaField(EditGenTymSchemaFieldDto dto) throws BizException {
        GenTymSchemaFieldPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetGenTymSchemaFieldDetailsVo getGenTymSchemaFieldDetails(CommonIdDto dto) throws BizException {
        GenTymSchemaFieldPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetGenTymSchemaFieldDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeGenTymSchemaField(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}