package com.ksptooi.biz.qt.service;

import com.ksptooi.biz.qt.model.qttaskrcd.QtTaskRcdPo;
import com.ksptooi.biz.qt.model.qttaskrcd.dto.AddQtTaskRcdDto;
import com.ksptooi.biz.qt.model.qttaskrcd.dto.EditQtTaskRcdDto;
import com.ksptooi.biz.qt.model.qttaskrcd.dto.GetQtTaskRcdListDto;
import com.ksptooi.biz.qt.model.qttaskrcd.vo.GetQtTaskRcdDetailsVo;
import com.ksptooi.biz.qt.model.qttaskrcd.vo.GetQtTaskRcdListVo;
import com.ksptooi.biz.qt.repository.QtTaskRcdRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class QtTaskRcdService {

    @Autowired
    private QtTaskRcdRepository repository;

    public PageResult<GetQtTaskRcdListVo> getQtTaskRcdList(GetQtTaskRcdListDto dto) {
        QtTaskRcdPo query = new QtTaskRcdPo();
        assign(dto, query);

        Page<QtTaskRcdPo> page = repository.getQtTaskRcdList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetQtTaskRcdListVo> vos = as(page.getContent(), GetQtTaskRcdListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addQtTaskRcd(AddQtTaskRcdDto dto) {
        QtTaskRcdPo insertPo = as(dto, QtTaskRcdPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editQtTaskRcd(EditQtTaskRcdDto dto) throws BizException {
        QtTaskRcdPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetQtTaskRcdDetailsVo getQtTaskRcdDetails(CommonIdDto dto) throws BizException {
        QtTaskRcdPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetQtTaskRcdDetailsVo.class);
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