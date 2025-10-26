package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.company.CompanyPo;
import com.ksptooi.biz.core.model.company.dto.AddCompanyDto;
import com.ksptooi.biz.core.model.company.dto.EditCompanyDto;
import com.ksptooi.biz.core.model.company.dto.GetCurrentUserCompanyListDto;
import com.ksptooi.biz.core.model.company.vo.GetCompanyDetailsVo;
import com.ksptooi.biz.core.model.company.vo.GetCurrentUserCompanyListVo;
import com.ksptooi.biz.core.model.companymember.CompanyMemberPo;
import com.ksptooi.biz.core.repository.CompanyMemberRepository;
import com.ksptooi.biz.core.repository.CompanyRepository;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository repository;

    @Autowired
    private CompanyMemberRepository memberRepository;

    @Autowired
    private AuthService authService;

    /**
     * 获取当前用户加入的公司列表
     *
     * @param dto
     * @return
     */
    public PageResult<GetCurrentUserCompanyListVo> getCurrentUserCompanyList(GetCurrentUserCompanyListDto dto) throws AuthException {

        Long userId = AuthService.requireUserId();

        Page<GetCurrentUserCompanyListVo> page = repository.getCurrentUserCompanyList(dto.getName(), userId, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        return PageResult.success(page.getContent(), (int) page.getTotalElements());
    }

    /**
     * 新增公司
     *
     * @param dto
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCompany(AddCompanyDto dto) throws Exception {
        CompanyPo insertPo = as(dto, CompanyPo.class);

        Long count = repository.countByCompanyName(dto.getName());

        if (count > 0) {
            throw new BizException("公司名称已存在");
        }
        insertPo.setFounder(authService.requireUser());

        //新增成功以后，将当前用户加入公司
        insertPo.addMember(authService.requireUser(), 0); //0:CEO 1:成员
        repository.save(insertPo);
    }

    /**
     * 编辑公司
     *
     * @param dto
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void editCompany(EditCompanyDto dto) throws Exception {
        CompanyPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        assign(dto, updatePo);

        if (!updatePo.getFounder().getId().equals(AuthService.requireUserId())) {
            throw new BizException("无权限编辑此公司");
        }

        Long count = repository.countByCompanyNameExcludeId(dto.getName(), updatePo.getId());

        if (count > 0) {
            throw new BizException("公司名称已存在");
        }

        repository.save(updatePo);
    }

    /**
     * 获取公司详情
     *
     * @param dto
     * @return
     * @throws BizException
     */
    public GetCompanyDetailsVo getCompanyDetails(CommonIdDto dto) throws BizException {
        CompanyPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetCompanyDetailsVo.class);
    }

    /**
     * 删除公司
     *
     * @param dto
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeCompany(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

    /**
     * 退出公司
     *
     * @param dto 公司ID
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void leaveCompany(CommonIdDto dto) throws Exception {
        Long userId = AuthService.requireUserId();
        Long companyId = dto.getId();

        CompanyPo company = repository.findById(companyId)
                .orElseThrow(() -> new BizException("团队不存在"));

        // 获取当前用户在公司中的成员关系
        CompanyMemberPo currentMember = memberRepository.findByCompanyIdAndUserId(companyId, userId);
        if (currentMember == null) {
            throw new BizException("您不是此团队成员");
        }

        // 检查公司成员数量
        Set<CompanyMemberPo> members = company.getMembers();
        if (members.size() <= 1) {
            // 公司只有一个成员，直接关闭公司
            repository.deleteById(companyId);
            return;
        }

        if (currentMember.getRole() == 0) { // 0:CEO 1:成员
            throw new BizException("CEO无法退出团队");
        }

        // 删除成员关系
        memberRepository.deleteById(currentMember.getId());
    }

}