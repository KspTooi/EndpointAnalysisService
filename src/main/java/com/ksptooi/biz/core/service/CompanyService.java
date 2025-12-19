package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.company.CompanyPo;
import com.ksptooi.biz.core.model.company.dto.AddCompanyDto;
import com.ksptooi.biz.core.model.company.dto.EditCompanyDto;
import com.ksptooi.biz.core.model.company.dto.GetCurrentUserCompanyListDto;
import com.ksptooi.biz.core.model.company.dto.ResignCeoDto;
import com.ksptooi.biz.core.model.company.vo.GetCompanyDetailsVo;
import com.ksptooi.biz.core.model.company.vo.GetCurrentUserCompanyListVo;
import com.ksptooi.biz.core.model.companymember.CompanyMemberPo;
import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.core.repository.CompanyMemberRepository;
import com.ksptooi.biz.core.repository.CompanyRepository;
import com.ksptooi.biz.core.repository.UserRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取当前用户加入的公司列表
     *
     * @param dto
     * @return
     */
    public PageResult<GetCurrentUserCompanyListVo> getCurrentUserCompanyList(GetCurrentUserCompanyListDto dto) throws AuthException {
        UserPo user = authService.requireUser();
        Page<GetCurrentUserCompanyListVo> pVos = repository.getCurrentUserCompanyList(dto.getName(), user.getId(), dto.pageRequest());
        return PageResult.success(pVos.getContent(), pVos.getTotalElements());
    }

    /**
     * 新增公司
     *
     * @param dto
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCompany(AddCompanyDto dto) throws Exception {
        UserPo user = authService.requireUser();
        CompanyPo insertPo = as(dto, CompanyPo.class);

        Long count = repository.countByCompanyName(dto.getName());

        if (count > 0) {
            throw new BizException("公司名称已存在");
        }
        insertPo.setFounder(user);

        //新增成功以后，将当前用户加入公司
        insertPo.addMember(user, 0); //0:CEO 1:成员
        

        //如果该用户名下没有担任CEO的公司，则将当前公司设置为激活公司
        Long ceoCompanyCount = repository.getCompanyCountByUserRole(user.getId(), 0);

        if (ceoCompanyCount < 1) {
            //将当前公司设置为激活公司
            user.setActiveCompany(insertPo);
        }

        repository.save(insertPo);
        userRepository.save(user);
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

        //只有CEO才可以编辑公司
        CompanyMemberPo currentMember = memberRepository.findByCompanyIdAndUserId(updatePo.getId(), AuthService.requireUserId());
        if (currentMember == null) {
            throw new BizException("您不是此公司成员");
        }

        if (currentMember.getRole() != 0) {
            throw new BizException("您不是CEO，无权限编辑此公司");
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

        repository.findById(companyId)
                .orElseThrow(() -> new BizException("团队不存在"));

        // 获取当前用户在公司中的成员关系
        CompanyMemberPo currentMember = memberRepository.findByCompanyIdAndUserId(companyId, userId);
        if (currentMember == null) {
            throw new BizException("您不是此团队成员");
        }

        // 检查公司成员数量（使用COUNT查询，避免加载集合）
        Long memberCount = memberRepository.countByCompanyId(companyId);

        // 如果是CEO，且团队中还有其他成员时，无法退出
        if (currentMember.getRole() == 0 && memberCount > 1) { // 0:CEO 1:成员
            throw new BizException("如果您是CEO，并且团队中还有其他成员时，您将无法退出团队");
        }

        // 如果是最后一个成员，退出会注销团队
        if (memberCount <= 1) {
            repository.deleteById(companyId);
            // 更新用户已激活的公司
            UserPo user = authService.requireUser();
            if (user.getActiveCompany() != null && user.getActiveCompany().getId().equals(companyId)) {
                user.setActiveCompany(null);
                userRepository.save(user);
            }
            return;
        }

        // 直接删除成员关系（使用逻辑删除）
        memberRepository.deleteById(currentMember.getId());

        // 更新用户已激活的公司（退出后将无法访问团队资源）
        UserPo user = authService.requireUser();
        if (user.getActiveCompany() != null && user.getActiveCompany().getId().equals(companyId)) {
            user.setActiveCompany(null);
            userRepository.save(user);
        }

    }

    /**
     * 激活公司
     *
     * @param dto 公司ID
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void activateCompany(CommonIdDto dto) throws Exception {
        Long userId = AuthService.requireUserId();

        //查询公司
        CompanyPo company = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("公司不存在"));

        //查询用户是否在该公司中
        CompanyMemberPo currentMember = memberRepository.findByCompanyIdAndUserId(dto.getId(), userId);

        if (currentMember == null) {
            throw new BizException("您不是此公司成员");
        }

        //更新用户已激活的公司
        UserPo user = authService.requireUser();

        //检查是否已重复激活相同公司
        if (user.getActiveCompany() != null && user.getActiveCompany().getId().equals(company.getId())) {
            throw new BizException("您已激活此公司");
        }

        user.setActiveCompany(company);

        //激活团队后，刷新用户会话
        authService.refreshUserSession(user.getId());
        userRepository.save(user);
    }

    /**
     * 辞去CEO职位
     *
     * @param dto 公司ID
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void resignCEO(ResignCeoDto dto) throws Exception {

        repository.findById(dto.getCompanyId())
                .orElseThrow(() -> new BizException("公司不存在"));

        Long userId = AuthService.requireUserId();

        //查询用户是否在该公司中
        CompanyMemberPo currentMember = memberRepository.findByCompanyIdAndUserId(dto.getCompanyId(), userId);

        if (currentMember == null) {
            throw new BizException("您不是此公司成员");
        }

        //检查是否是CEO 0:CEO 1:成员
        if (currentMember.getRole() != 0) {
            throw new BizException("您现在还不是CEO，无法辞去CEO职位。");
        }

        //查询CEO移交用户是否在该公司中
        CompanyMemberPo newCeoMember = memberRepository.findByCompanyIdAndUserId(dto.getCompanyId(), dto.getNewCeoUserId());

        if (newCeoMember == null) {
            throw new BizException("CEO移交用户不存在或不是公司成员");
        }

        //检查移交用户是否已经是CEO
        if (newCeoMember.getRole() == 0) {
            throw new BizException("该用户已经是CEO了，您这是想让他升级为CEO Pro吗？");
        }

        //更新CEO移交用户为CEO
        newCeoMember.setRole(0);
        //更新当前用户为成员
        currentMember.setRole(1);

        memberRepository.save(newCeoMember);
        memberRepository.save(currentMember);
    }

}