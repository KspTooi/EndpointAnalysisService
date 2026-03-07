package com.ksptool.bio.biz.gen.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.gen.model.tymschema.TymSchemaPo;
import com.ksptool.bio.biz.gen.model.tymschema.dto.AddTymSchemaDto;
import com.ksptool.bio.biz.gen.model.tymschema.dto.EditTymSchemaDto;
import com.ksptool.bio.biz.gen.model.tymschema.dto.GetTymSchemaListDto;
import com.ksptool.bio.biz.gen.model.tymschema.vo.GetTymSchemaDetailsVo;
import com.ksptool.bio.biz.gen.model.tymschema.vo.GetTymSchemaListVo;
import com.ksptool.bio.biz.gen.repository.TymSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class TymSchemaService {

    @Autowired
    private TymSchemaRepository repository;

    /**
     * 查询类型映射方案列表
     * @param dto 查询参数
     * @return 类型映射方案列表
     */
    public PageResult<GetTymSchemaListVo> getTymSchemaList(GetTymSchemaListDto dto) {
        TymSchemaPo query = new TymSchemaPo();
        assign(dto, query);

        Page<TymSchemaPo> page = repository.getTymSchemaList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetTymSchemaListVo> vos = as(page.getContent(), GetTymSchemaListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增类型映射方案
     * @param dto 新增参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addTymSchema(AddTymSchemaDto dto) {
        TymSchemaPo insertPo = as(dto, TymSchemaPo.class);
        insertPo.setTypeCount(0);
        repository.save(insertPo);
    }

    /**
     * 编辑类型映射方案
     * @param dto 编辑参数
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void editTymSchema(EditTymSchemaDto dto) throws BizException {
        TymSchemaPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询类型映射方案详情
     * @param dto 查询参数
     * @return 类型映射方案详情
     * @throws BizException
     */
    public GetTymSchemaDetailsVo getTymSchemaDetails(CommonIdDto dto) throws BizException {
        TymSchemaPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetTymSchemaDetailsVo.class);
    }

    /**
     * 删除类型映射方案
     * @param dto 删除参数
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeTymSchema(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}