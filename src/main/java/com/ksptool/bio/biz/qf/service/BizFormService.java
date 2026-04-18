package com.ksptool.bio.biz.qf.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.qf.model.qfbizform.BizFormPo;
import com.ksptool.bio.biz.qf.model.qfbizform.dto.AddBizFormDto;
import com.ksptool.bio.biz.qf.model.qfbizform.dto.EditBizFormDto;
import com.ksptool.bio.biz.qf.model.qfbizform.dto.GetBizFormListDto;
import com.ksptool.bio.biz.qf.model.qfbizform.vo.GetBizFormDetailsVo;
import com.ksptool.bio.biz.qf.model.qfbizform.vo.GetBizFormListVo;
import com.ksptool.bio.biz.qf.repository.BizFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class BizFormService {

    @Autowired
    private BizFormRepository repository;

    /**
     * 查询业务表单列表
     *
     * @param dto 查询条件
     * @return 查询结果
     */
    public PageResult<GetBizFormListVo> getBizFormList(GetBizFormListDto dto) {
        BizFormPo query = new BizFormPo();
        assign(dto, query);

        Page<BizFormPo> page = repository.getBizFormList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetBizFormListVo> vos = as(page.getContent(), GetBizFormListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增业务表单
     *
     * @param dto 新增条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void addBizForm(AddBizFormDto dto) {
        BizFormPo insertPo = as(dto, BizFormPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑业务表单
     *
     * @param dto 编辑条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editBizForm(EditBizFormDto dto) throws BizException {
        BizFormPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询业务表单详情
     *
     * @param dto 查询条件
     * @return 查询结果
     * @throws BizException 业务异常
     */
    public GetBizFormDetailsVo getBizFormDetails(CommonIdDto dto) throws BizException {
        BizFormPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetBizFormDetailsVo.class);
    }

    /**
     * 删除业务表单
     *
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeBizForm(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}
