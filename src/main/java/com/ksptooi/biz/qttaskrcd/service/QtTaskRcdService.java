package com.ksptooi.biz.qttaskrcd.service;

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
import com.ksptooi.biz.qttaskrcd.repository.QtTaskRcdRepository;
import com.ksptooi.biz.qttaskrcd.model.QtTaskRcdPo;
import com.ksptooi.biz.qttaskrcd.model.vo.GetQtTaskRcdListVo;
import com.ksptooi.biz.qttaskrcd.model.dto.GetQtTaskRcdListDto;
import com.ksptooi.biz.qttaskrcd.model.vo.GetQtTaskRcdDetailsVo;
import com.ksptooi.biz.qttaskrcd.model.dto.EditQtTaskRcdDto;
import com.ksptooi.biz.qttaskrcd.model.dto.AddQtTaskRcdDto;


@Service
public class QtTaskRcdService {

    @Autowired
    private QtTaskRcdRepository repository;

    public PageResult<GetQtTaskRcdListVo> getQtTaskRcdList(GetQtTaskRcdListDto dto){
        QtTaskRcdPo query = new QtTaskRcdPo();
        assign(dto,query);

        Page<QtTaskRcdPo> page = repository.getQtTaskRcdList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetQtTaskRcdListVo> vos = as(page.getContent(), GetQtTaskRcdListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addQtTaskRcd(AddQtTaskRcdDto dto){
        QtTaskRcdPo insertPo = as(dto,QtTaskRcdPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editQtTaskRcd(EditQtTaskRcdDto dto) throws BizException {
        QtTaskRcdPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetQtTaskRcdDetailsVo getQtTaskRcdDetails(CommonIdDto dto) throws BizException {
        QtTaskRcdPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetQtTaskRcdDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeQtTaskRcd(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}