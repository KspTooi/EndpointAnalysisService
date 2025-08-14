package com.ksptooi.biz.userrequest.service;

import com.ksptooi.biz.user.model.user.UserPo;
import com.ksptooi.biz.userrequest.model.userrequestgroup.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.ksptooi.commons.utils.web.PageResult;

import jakarta.security.auth.message.AuthException;

import com.ksptooi.commons.utils.web.CommonIdDto;
import org.springframework.data.domain.Page;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.biz.userrequest.repository.UserRequestGroupRepository;
import com.ksptooi.biz.user.service.AuthService;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class UserRequestGroupService {

    @Autowired
    private UserRequestGroupRepository repository;

    @Autowired
    private AuthService authService;

    /**
     * 查询请求组列表
     * @param dto 查询请求组参数
     * @return 请求组列表
     */
    public PageResult<GetUserRequestGroupListVo> getUserRequestGroupList(GetUserRequestGroupListDto dto){
        UserRequestGroupPo query = new UserRequestGroupPo();
        assign(dto,query);

        Page<UserRequestGroupPo> page = repository.getUserRequestGroupList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetUserRequestGroupListVo> vos = as(page.getContent(), GetUserRequestGroupListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增请求组
     * @param dto 新增请求组参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUserRequestGroup(AddUserRequestGroupDto dto) throws BizException,AuthException {

        UserPo user = authService.requireUser();

        UserRequestGroupPo parentPo = null;

        if(dto.getParentId() != null){
            parentPo = repository.getRequestGroupByIdAndUserId(dto.getParentId(), user.getId());
            if(parentPo == null){
                throw new BizException("父级组不存在.");
            }
        }

        UserRequestGroupPo insertPo = new UserRequestGroupPo();
        insertPo.setUser(user);
        insertPo.setParent(parentPo);
        insertPo.setName(dto.getName());
        insertPo.setSeq(repository.getNextSeq(AuthService.requireUserId()));
        repository.save(insertPo);
    }

    /**
     * 编辑请求组
     * @param dto 编辑请求组参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void editUserRequestGroup(EditUserRequestGroupDto dto) throws BizException,AuthException {

        UserPo user = authService.requireUser();
        UserRequestGroupPo updatePo = repository.getRequestGroupByIdAndUserId(dto.getId(), user.getId());
        if(updatePo == null){
            throw new BizException("更新失败,数据不存在.");
        }

        if(dto.getParentId() != null){
            UserRequestGroupPo parentPo = repository.getRequestGroupByIdAndUserId(dto.getParentId(), user.getId());
            if(parentPo == null){
                throw new BizException("父级组不存在.");
            }
            updatePo.setParent(parentPo);
        }

        updatePo.setName(dto.getName());
        updatePo.setDescription(dto.getDescription());
        updatePo.setSeq(dto.getSeq());
        repository.save(updatePo);
    }

    /**
     * 查询请求组详情
     * @param dto 查询请求组参数
     * @return 请求组详情
     */
    public GetUserRequestGroupDetailsVo getUserRequestGroupDetails(CommonIdDto dto) throws BizException {
        UserRequestGroupPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在."));
        return as(po,GetUserRequestGroupDetailsVo.class);
    }

    /**
     * 删除请求组
     * @param dto 删除请求组参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRequestGroup(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if(!dto.isBatch()){
            repository.deleteById(dto.getId());
        }
    }

}