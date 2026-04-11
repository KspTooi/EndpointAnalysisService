package com.ksptool.bio.biz.user.service;

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
import com.ksptool.bio.biz.user.repository.UserRepository;
import com.ksptool.bio.biz.user.model.UserPo;
import com.ksptool.bio.biz.user.model.vo.GetUserListVo;
import com.ksptool.bio.biz.user.model.dto.GetUserListDto;
import com.ksptool.bio.biz.user.model.vo.GetUserDetailsVo;
import com.ksptool.bio.biz.user.model.dto.EditUserDto;
import com.ksptool.bio.biz.user.model.dto.AddUserDto;


@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    /**
     * 查询用户列表
     * @param dto 查询条件
     * @return 查询结果
     */
    public PageResult<GetUserListVo> getUserList(GetUserListDto dto){
        UserPo query = new UserPo();
        assign(dto,query);

        Page<UserPo> page = repository.getUserList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetUserListVo> vos = as(page.getContent(), GetUserListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增用户
     * @param dto 新增条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUser(AddUserDto dto){
        UserPo insertPo = as(dto,UserPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑用户
     * @param dto 编辑条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editUser(EditUserDto dto) throws BizException {
        UserPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询用户详情
     * @param dto 查询条件
     * @return 查询结果
     * @throws BizException 业务异常
     */
    public GetUserDetailsVo getUserDetails(CommonIdDto dto) throws BizException {
        UserPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetUserDetailsVo.class);
    }

    /**
     * 删除用户
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeUser(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}
