package com.ksptool.bio.biz.qf.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.QfModelDeployRcdPo;
import com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.dto.AddQfModelDeployRcdDto;
import com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.dto.EditQfModelDeployRcdDto;
import com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.dto.GetQfModelDeployRcdListDto;
import com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.vo.GetQfModelDeployRcdDetailsVo;
import com.ksptool.bio.biz.qf.model.qfmodeldeployrcd.vo.GetQfModelDeployRcdListVo;
import com.ksptool.bio.biz.qf.repository.QfModelDeployRcdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class QfModelDeployRcdService {

    @Autowired
    private QfModelDeployRcdRepository repository;

    /**
     * 查询流程模型部署历史列表
     *
     * @param dto 查询条件
     * @return 查询结果
     */
    public PageResult<GetQfModelDeployRcdListVo> getQfModelDeployRcdList(GetQfModelDeployRcdListDto dto) {
        QfModelDeployRcdPo query = new QfModelDeployRcdPo();
        assign(dto, query);

        Page<QfModelDeployRcdPo> page = repository.getQfModelDeployRcdList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetQfModelDeployRcdListVo> vos = as(page.getContent(), GetQfModelDeployRcdListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增流程模型部署历史
     *
     * @param dto 新增条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void addQfModelDeployRcd(AddQfModelDeployRcdDto dto) {
        QfModelDeployRcdPo insertPo = as(dto, QfModelDeployRcdPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑流程模型部署历史
     *
     * @param dto 编辑条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editQfModelDeployRcd(EditQfModelDeployRcdDto dto) throws BizException {
        QfModelDeployRcdPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询流程模型部署历史详情
     *
     * @param dto 查询条件
     * @return 查询结果
     * @throws BizException 业务异常
     */
    public GetQfModelDeployRcdDetailsVo getQfModelDeployRcdDetails(CommonIdDto dto) throws BizException {
        QfModelDeployRcdPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetQfModelDeployRcdDetailsVo.class);
    }

    /**
     * 删除流程模型部署历史
     *
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeQfModelDeployRcd(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}
