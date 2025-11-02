package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.companymember.CompanyMemberPo;
import com.ksptooi.biz.core.model.companymember.dto.AddCompanyMemberDto;
import com.ksptooi.biz.core.model.companymember.dto.EditCompanyMemberDto;
import com.ksptooi.biz.core.model.companymember.dto.GetCompanyMemberListDto;
import com.ksptooi.biz.core.model.companymember.vo.GetCompanyMemberDetailsVo;
import com.ksptooi.biz.core.model.companymember.vo.GetCompanyMemberListVo;
import com.ksptooi.biz.core.repository.CompanyMemberRepository;
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
public class CompanyMemberService {

    @Autowired
    private CompanyMemberRepository repository;

    public PageResult<GetCompanyMemberListVo> getCompanyMemberList(GetCompanyMemberListDto dto) {
        CompanyMemberPo query = new CompanyMemberPo();
        assign(dto, query);

        Page<CompanyMemberPo> page = repository.getCompanyMemberList(query, dto.pageRequest());
        List<GetCompanyMemberListVo> vos = as(page.getContent(), GetCompanyMemberListVo.class);
        return PageResult.success(vos, page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addCompanyMember(AddCompanyMemberDto dto) {
        CompanyMemberPo insertPo = as(dto, CompanyMemberPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editCompanyMember(EditCompanyMemberDto dto) throws BizException {
        CompanyMemberPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetCompanyMemberDetailsVo getCompanyMemberDetails(CommonIdDto dto) throws BizException {
        CompanyMemberPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetCompanyMemberDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeCompanyMember(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}