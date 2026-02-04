package com.ksptooi.biz.audit.service;

import com.ksptooi.biz.audit.modal.auditlogin.AuditLoginPo;
import com.ksptooi.biz.audit.modal.auditlogin.dto.AddAuditLoginDto;
import com.ksptooi.biz.audit.modal.auditlogin.dto.EditAuditLoginDto;
import com.ksptooi.biz.audit.modal.auditlogin.dto.GetAuditLoginListDto;
import com.ksptooi.biz.audit.modal.auditlogin.vo.GetAuditLoginDetailsVo;
import com.ksptooi.biz.audit.modal.auditlogin.vo.GetAuditLoginListVo;
import com.ksptooi.biz.audit.repository.AuditLoginRepository;
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
public class AuditLoginService {

    @Autowired
    private AuditLoginRepository repository;

    public PageResult<GetAuditLoginListVo> getAuditLoginList(GetAuditLoginListDto dto) {
        AuditLoginPo query = new AuditLoginPo();
        assign(dto, query);

        Page<AuditLoginPo> page = repository.getAuditLoginList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetAuditLoginListVo> vos = as(page.getContent(), GetAuditLoginListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addAuditLogin(AddAuditLoginDto dto) {
        AuditLoginPo insertPo = as(dto, AuditLoginPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editAuditLogin(EditAuditLoginDto dto) throws BizException {
        AuditLoginPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetAuditLoginDetailsVo getAuditLoginDetails(CommonIdDto dto) throws BizException {
        AuditLoginPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetAuditLoginDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeAuditLogin(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}