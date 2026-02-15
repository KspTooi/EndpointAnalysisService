package com.ksptooi.biz.authgroupdept.service;

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
import com.ksptooi.biz.authgroupdept.repository.AuthGroupDeptRepository;
import com.ksptooi.biz.authgroupdept.model.AuthGroupDeptPo;
import com.ksptooi.biz.authgroupdept.model.vo.GetAuthGroupDeptListVo;
import com.ksptooi.biz.authgroupdept.model.dto.GetAuthGroupDeptListDto;
import com.ksptooi.biz.authgroupdept.model.vo.GetAuthGroupDeptDetailsVo;
import com.ksptooi.biz.authgroupdept.model.dto.EditAuthGroupDeptDto;
import com.ksptooi.biz.authgroupdept.model.dto.AddAuthGroupDeptDto;


@Service
public class AuthGroupDeptService {

    @Autowired
    private AuthGroupDeptRepository repository;

    public PageResult<GetAuthGroupDeptListVo> getAuthGroupDeptList(GetAuthGroupDeptListDto dto){
        AuthGroupDeptPo query = new AuthGroupDeptPo();
        assign(dto,query);

        Page<AuthGroupDeptPo> page = repository.getAuthGroupDeptList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetAuthGroupDeptListVo> vos = as(page.getContent(), GetAuthGroupDeptListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addAuthGroupDept(AddAuthGroupDeptDto dto){
        AuthGroupDeptPo insertPo = as(dto,AuthGroupDeptPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editAuthGroupDept(EditAuthGroupDeptDto dto) throws BizException {
        AuthGroupDeptPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetAuthGroupDeptDetailsVo getAuthGroupDeptDetails(CommonIdDto dto) throws BizException {
        AuthGroupDeptPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetAuthGroupDeptDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeAuthGroupDept(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}