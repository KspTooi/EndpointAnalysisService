package com.ksptool.bio.biz.qfmodeldeployrcd.service;

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
import com.ksptool.bio.biz.qfmodeldeployrcd.repository.QfModelDeployRcdRepository;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.QfModelDeployRcdPo;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.vo.GetQfModelDeployRcdListVo;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.dto.GetQfModelDeployRcdListDto;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.vo.GetQfModelDeployRcdDetailsVo;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.dto.EditQfModelDeployRcdDto;
import com.ksptool.bio.biz.qfmodeldeployrcd.model.dto.AddQfModelDeployRcdDto;


@Service
public class QfModelDeployRcdService {

    @Autowired
    private QfModelDeployRcdRepository repository;

    public PageResult<GetQfModelDeployRcdListVo> getQfModelDeployRcdList(GetQfModelDeployRcdListDto dto){
        QfModelDeployRcdPo query = new QfModelDeployRcdPo();
        assign(dto,query);

        Page<QfModelDeployRcdPo> page = repository.getQfModelDeployRcdList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetQfModelDeployRcdListVo> vos = as(page.getContent(), GetQfModelDeployRcdListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addQfModelDeployRcd(AddQfModelDeployRcdDto dto){
        QfModelDeployRcdPo insertPo = as(dto,QfModelDeployRcdPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editQfModelDeployRcd(EditQfModelDeployRcdDto dto) throws BizException {
        QfModelDeployRcdPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetQfModelDeployRcdDetailsVo getQfModelDeployRcdDetails(CommonIdDto dto) throws BizException {
        QfModelDeployRcdPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetQfModelDeployRcdDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeQfModelDeployRcd(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}