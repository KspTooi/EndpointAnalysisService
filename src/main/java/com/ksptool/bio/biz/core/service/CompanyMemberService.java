package com.ksptool.bio.biz.core.service;

import com.ksptooi.biz.auth.service.SessionService;
import com.ksptool.bio.biz.core.model.company.CompanyPo;
import com.ksptool.bio.biz.core.model.companymember.CompanyMemberPo;
import com.ksptooi.biz.core.model.companymember.dto.*;
import com.ksptool.bio.biz.core.model.companymember.dto.*;
import com.ksptool.bio.biz.core.model.companymember.vo.GetCompanyMemberDetailsVo;
import com.ksptool.bio.biz.core.model.companymember.vo.GetCompanyMemberListVo;
import com.ksptool.bio.biz.core.model.companymember.vo.GetCurrentUserActiveCompanyMemberListVo;
import com.ksptool.bio.biz.core.model.user.UserPo;
import com.ksptool.bio.biz.core.repository.CompanyMemberRepository;
import com.ksptool.bio.biz.core.repository.CompanyRepository;
import com.ksptool.bio.biz.core.repository.UserRepository;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ksptooi.biz.auth.service.SessionService.session;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class CompanyMemberService {

    @Autowired
    private CompanyMemberRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;


    /**
     * 获取公司成员列表
     *
     * @param dto 查询条件
     * @return 公司成员列表
     */
    public PageResult<GetCompanyMemberListVo> getCompanyMemberList(GetCompanyMemberListDto dto) throws Exception {

        //非该公司的成员，无法获取成员列表
        Long userId = session().getUserId();

        companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new BizException("公司不存在"));

        CompanyMemberPo currentMember = repository.findByCompanyIdAndUserId(dto.getCompanyId(), userId);

        if (currentMember == null) {
            throw new BizException("您还不是此公司的成员,无法获取成员列表。");
        }

        CompanyMemberPo query = new CompanyMemberPo();
        assign(dto, query);

        Page<CompanyMemberPo> pPos = repository.getCompanyMemberList(dto, dto.pageRequest());
        List<GetCompanyMemberListVo> pVos = new ArrayList<>();

        for (CompanyMemberPo po : pPos) {
            GetCompanyMemberListVo vo = new GetCompanyMemberListVo();
            //映射基础字段
            assign(po, vo);

            //映射用户名称
            vo.setUsername(po.getUser().getUsername());
            vo.setUserId(po.getUser().getId());
            pVos.add(vo);
        }

        return PageResult.success(pVos, pPos.getTotalElements());
    }

    public Result<GetCurrentUserActiveCompanyMemberListVo> getCurrentUserActiveCompanyMemberList(GetCurrentUserActiveCompanyMemberListDto dto) throws Exception {

        //查询用户当前激活的公司
        UserPo user = sessionService.requireUser();

        if (user.getActiveCompany() == null) {
            throw new BizException("用户当前没有激活的公司");
        }

        Long companyId = user.getActiveCompany().getId();

        Page<CompanyMemberPo> pPos = repository.getCompanyMemberList(new GetCompanyMemberListDto() {{
            setCompanyId(companyId);
            setUsername(dto.getUsername());
            setRole(dto.getRole());
        }}, dto.pageRequest());

        GetCurrentUserActiveCompanyMemberListVo retVo = new GetCurrentUserActiveCompanyMemberListVo();
        retVo.setCompanyName(user.getActiveCompany().getName());
        retVo.setCompanyId(companyId);

        List<GetCompanyMemberListVo> memberVos = new ArrayList<>();

        for (CompanyMemberPo po : pPos) {
            GetCompanyMemberListVo vo = new GetCompanyMemberListVo();
            //映射基础字段
            assign(po, vo);

            //映射用户名称
            vo.setUsername(po.getUser().getUsername());
            vo.setUserId(po.getUser().getId());
            memberVos.add(vo);
        }

        retVo.setMembers(PageResult.success(memberVos, pPos.getTotalElements()));
        return Result.success(retVo);
    }

    /**
     * 新增公司成员
     *
     * @param dto 新增公司成员参数
     * @throws BizException  业务异常
     * @throws AuthException 认证异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCompanyMember(AddCompanyMemberDto dto) throws BizException, AuthException {

        //查询公司
        CompanyPo companyPo = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new BizException("公司不存在"));

        Long currentUserId = session().getUserId();
        CompanyMemberPo currentMember = repository.findByCompanyIdAndUserId(dto.getCompanyId(), currentUserId);

        if (currentMember == null) {
            throw new BizException("您不是此公司的成员");
        }
        if (currentMember.getRole() != 0) {
            throw new BizException("只有CEO才能添加成员");
        }

        UserPo user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BizException("用户不存在"));

        if (user.getStatus() == 1) {
            throw new BizException("用户已被封禁");
        }

        CompanyMemberPo existingMember = repository.getByCompanyIdAndUserIdWithLock(dto.getCompanyId(), dto.getUserId());

        if (existingMember != null) {
            throw new BizException("该用户已是公司成员");
        }

        try {
            CompanyMemberPo insertPo = new CompanyMemberPo();
            insertPo.setCompany(companyPo);
            insertPo.setUser(user);
            insertPo.setRole(dto.getRole());
            insertPo.setJoinedTime(LocalDateTime.now());

            //查询用户是否有激活公司
            CompanyPo activeCompany = user.getActiveCompany();

            if (activeCompany == null) {
                user.setActiveCompany(companyPo);
            }

            repository.save(insertPo);
            userRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            throw new BizException("该用户已是公司成员");
        }
    }

    /**
     * 编辑公司成员
     *
     * @param dto 编辑公司成员参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editCompanyMember(EditCompanyMemberDto dto) throws BizException {
        CompanyMemberPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 获取公司成员详情
     *
     * @param dto 查询公司成员参数
     * @return 公司成员详情
     * @throws BizException 业务异常
     */
    public GetCompanyMemberDetailsVo getCompanyMemberDetails(CommonIdDto dto) throws BizException {
        CompanyMemberPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetCompanyMemberDetailsVo.class);
    }

    /**
     * 删除公司成员
     *
     * @param dto 删除公司成员参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeCompanyMember(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

    /**
     * 开除公司成员
     *
     * @param dto 开除公司成员参数
     * @throws BizException  业务异常
     * @throws AuthException 认证异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void fireCompanyMember(FireCompanyMemberDto dto) throws BizException, AuthException {
        companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new BizException("公司不存在"));

        Long currentUserId = session().getUserId();
        CompanyMemberPo currentMember = repository.findByCompanyIdAndUserId(dto.getCompanyId(), currentUserId);

        if (currentMember == null) {
            throw new BizException("您不是此公司的成员");
        }
        if (currentMember.getRole() != 0) {
            throw new BizException("只有CEO才能开除成员");
        }

        if (currentUserId.equals(dto.getUserId())) {
            throw new BizException("不能开除自己");
        }

        CompanyMemberPo targetMember = repository.findByCompanyIdAndUserId(dto.getCompanyId(), dto.getUserId());

        if (targetMember == null) {
            throw new BizException("该用户不是此公司的成员");
        }

        if (targetMember.getRole() == 0) {
            throw new BizException("不能开除CEO");
        }

        repository.deleteById(targetMember.getId());

        UserPo targetUser = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BizException("用户不存在"));

        if (targetUser.getActiveCompany() != null && targetUser.getActiveCompany().getId().equals(dto.getCompanyId())) {
            targetUser.setActiveCompany(null);
            userRepository.save(targetUser);
        }
    }

}