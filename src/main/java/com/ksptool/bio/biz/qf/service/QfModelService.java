package com.ksptool.bio.biz.qf.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.qf.model.qfmodel.QfModelPo;
import com.ksptool.bio.biz.qf.model.qfmodel.dto.AddQfModelDto;
import com.ksptool.bio.biz.qf.model.qfmodel.dto.EditQfModelDto;
import com.ksptool.bio.biz.qf.model.qfmodel.dto.GetQfModelListDto;
import com.ksptool.bio.biz.qf.model.qfmodel.vo.GetQfModelDetailsVo;
import com.ksptool.bio.biz.qf.model.qfmodel.vo.GetQfModelListVo;
import com.ksptool.bio.biz.qf.repository.QfModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class QfModelService {

    @Autowired
    private QfModelRepository repository;

    /**
     * 查询流程模型列表
     *
     * @param dto 查询条件
     * @return 查询结果
     */
    public PageResult<GetQfModelListVo> getQfModelList(GetQfModelListDto dto) {
        QfModelPo query = new QfModelPo();
        assign(dto, query);

        Page<QfModelPo> page = repository.getQfModelList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetQfModelListVo> vos = as(page.getContent(), GetQfModelListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增流程模型
     *
     * @param dto 新增条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void addQfModel(AddQfModelDto dto) {
        QfModelPo insertPo = as(dto, QfModelPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑流程模型
     *
     * @param dto 编辑条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editQfModel(EditQfModelDto dto) throws BizException {
        QfModelPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询流程模型详情
     *
     * @param dto 查询条件
     * @return 查询结果
     * @throws BizException 业务异常
     */
    public GetQfModelDetailsVo getQfModelDetails(CommonIdDto dto) throws BizException {
        QfModelPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetQfModelDetailsVo.class);
    }

    /**
     * 删除流程模型
     *
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeQfModel(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}
