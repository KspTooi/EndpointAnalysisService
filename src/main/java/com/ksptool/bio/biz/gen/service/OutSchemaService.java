package com.ksptool.bio.biz.gen.service;

import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.exception.BizException;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import com.ksptool.bio.biz.gen.repository.OutSchemaRepository;
import com.ksptool.bio.biz.gen.model.outschema.OutSchemaPo;
import com.ksptool.bio.biz.gen.model.outschema.vo.GetOutSchemaListVo;
import com.ksptool.bio.biz.gen.model.outschema.dto.GetOutSchemaListDto;
import com.ksptool.bio.biz.gen.model.outschema.vo.GetOutSchemaDetailsVo;
import com.ksptool.bio.biz.gen.model.outschema.dto.EditOutSchemaDto;
import com.ksptool.bio.biz.gen.model.outschema.dto.AddOutSchemaDto;


@Service
public class OutSchemaService {

    @Autowired
    private OutSchemaRepository repository;

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
    public void addOutSchema(AddOutSchemaDto dto) {
        OutSchemaPo insertPo = as(dto, OutSchemaPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑输出方案
     *
     * @param dto 编辑参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editOutSchema(EditOutSchemaDto dto) throws BizException {
        OutSchemaPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
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