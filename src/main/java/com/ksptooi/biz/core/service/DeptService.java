package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.dept.DeptPo;
import com.ksptooi.biz.core.model.dept.dto.AddDeptDto;
import com.ksptooi.biz.core.model.dept.dto.EditDeptDto;
import com.ksptooi.biz.core.model.dept.dto.GetDeptListDto;
import com.ksptooi.biz.core.model.dept.vo.GetDeptDetailsVo;
import com.ksptooi.biz.core.model.dept.vo.GetDeptListVo;
import com.ksptooi.biz.core.repository.DeptRepository;
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
public class DeptService {

    @Autowired
    private DeptRepository repository;

    public PageResult<GetDeptListVo> getDeptList(GetDeptListDto dto) {
        DeptPo query = new DeptPo();
        assign(dto, query);

        Page<DeptPo> page = repository.getDeptList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetDeptListVo> vos = as(page.getContent(), GetDeptListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addDept(AddDeptDto dto) {
        DeptPo insertPo = as(dto, DeptPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editDept(EditDeptDto dto) throws BizException {
        DeptPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetDeptDetailsVo getDeptDetails(CommonIdDto dto) throws BizException {
        DeptPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetDeptDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeDept(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}