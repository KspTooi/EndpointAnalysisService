package com.ksptool.bio.biz.qfmodelgroup.service;

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
import com.ksptool.bio.biz.qfmodelgroup.repository.QfModelGroupRepository;
import com.ksptool.bio.biz.qfmodelgroup.model.QfModelGroupPo;
import com.ksptool.bio.biz.qfmodelgroup.model.vo.GetQfModelGroupListVo;
import com.ksptool.bio.biz.qfmodelgroup.model.dto.GetQfModelGroupListDto;
import com.ksptool.bio.biz.qfmodelgroup.model.vo.GetQfModelGroupDetailsVo;
import com.ksptool.bio.biz.qfmodelgroup.model.dto.EditQfModelGroupDto;
import com.ksptool.bio.biz.qfmodelgroup.model.dto.AddQfModelGroupDto;


@Service
public class QfModelGroupService {

    @Autowired
    private QfModelGroupRepository repository;

    /**
     * 查询流程模型分组列表
     * @param dto 查询条件
     * @return 查询结果
     */
    public PageResult<GetQfModelGroupListVo> getQfModelGroupList(GetQfModelGroupListDto dto){
        QfModelGroupPo query = new QfModelGroupPo();
        assign(dto,query);

        Page<QfModelGroupPo> page = repository.getQfModelGroupList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetQfModelGroupListVo> vos = as(page.getContent(), GetQfModelGroupListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增流程模型分组
     * @param dto 新增条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void addQfModelGroup(AddQfModelGroupDto dto){
        QfModelGroupPo insertPo = as(dto,QfModelGroupPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑流程模型分组
     * @param dto 编辑条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editQfModelGroup(EditQfModelGroupDto dto) throws BizException {
        QfModelGroupPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询流程模型分组详情
     * @param dto 查询条件
     * @return 查询结果
     * @throws BizException 业务异常
     */
    public GetQfModelGroupDetailsVo getQfModelGroupDetails(CommonIdDto dto) throws BizException {
        QfModelGroupPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetQfModelGroupDetailsVo.class);
    }

    /**
     * 删除流程模型分组
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeQfModelGroup(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}
