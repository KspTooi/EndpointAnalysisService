package com.ksptool.bio.biz.gen.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.gen.model.tymschemafield.TymSchemaFieldPo;
import com.ksptool.bio.biz.gen.model.tymschemafield.dto.AddTymSchemaFieldDto;
import com.ksptool.bio.biz.gen.model.tymschemafield.dto.EditTymSchemaFieldDto;
import com.ksptool.bio.biz.gen.model.tymschemafield.dto.GetTymSchemaFieldListDto;
import com.ksptool.bio.biz.gen.model.tymschemafield.vo.GetTymSchemaFieldDetailsVo;
import com.ksptool.bio.biz.gen.model.tymschemafield.vo.GetTymSchemaFieldListVo;
import com.ksptool.bio.biz.gen.repository.TymSchemaFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class TymSchemaFieldService {

    @Autowired
    private TymSchemaFieldRepository repository;

    /**
     * 查询类型映射方案字段列表
     *
     * @param dto 查询参数
     * @return 类型映射方案字段列表
     */
    public PageResult<GetTymSchemaFieldListVo> getTymSchemaFieldList(GetTymSchemaFieldListDto dto) {
        TymSchemaFieldPo query = new TymSchemaFieldPo();
        assign(dto, query);

        Page<TymSchemaFieldPo> page = repository.getTymSchemaFieldList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetTymSchemaFieldListVo> vos = as(page.getContent(), GetTymSchemaFieldListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增类型映射方案字段
     *
     * @param dto 新增参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addTymSchemaField(AddTymSchemaFieldDto dto) {
        TymSchemaFieldPo insertPo = as(dto, TymSchemaFieldPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑类型映射方案字段
     *
     * @param dto 编辑参数
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void editTymSchemaField(EditTymSchemaFieldDto dto) throws BizException {
        TymSchemaFieldPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询类型映射方案字段详情
     *
     * @param dto 查询参数
     * @return 类型映射方案字段详情
     * @throws BizException
     */
    public GetTymSchemaFieldDetailsVo getTymSchemaFieldDetails(CommonIdDto dto) throws BizException {
        TymSchemaFieldPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetTymSchemaFieldDetailsVo.class);
    }

    /**
     * 删除类型映射方案字段
     *
     * @param dto 删除参数
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeTymSchemaField(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}