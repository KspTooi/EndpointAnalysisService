package com.ksptooi.biz.userrequest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ksptooi.biz.user.service.AuthService;
import com.ksptooi.biz.userrequest.model.userrequest.UserRequestPo;
import com.ksptooi.biz.userrequest.model.userrequestgroup.UserRequestGroupPo;
import com.ksptooi.biz.userrequest.model.userrequesttree.UserRequestTreePo;
import com.ksptooi.biz.userrequest.model.userrequesttree.dto.AddUserRequestTreeDto;
import com.ksptooi.biz.userrequest.repository.UserRequestTreeRepository;
import com.ksptooi.commons.exception.BizException;

import jakarta.security.auth.message.AuthException;


@Service
public class UserRequestTreeService {

    @Autowired
    private UserRequestTreeRepository userRequestTreeRepository;

    @Autowired
    private AuthService authService;

    /**
     * 新增用户请求树
     * @param dto 参数
     */
    public void addUserRequestTree(AddUserRequestTreeDto dto) throws BizException, AuthException {

        UserRequestTreePo parentPo = null;

        // 如果父级ID不为空，则查询父级ID
        if (dto.getParentId() != null) {
            parentPo = userRequestTreeRepository.findById(dto.getParentId())
                .orElseThrow(() -> new BizException("父级ID不存在"));
        }

        UserRequestTreePo userRequestTreePo = new UserRequestTreePo();
        userRequestTreePo.setUser(authService.requireUser());
        userRequestTreePo.setParent(parentPo);
        userRequestTreePo.setName(dto.getName());
        userRequestTreePo.setKind(dto.getKind());
        userRequestTreePo.setSeq(userRequestTreeRepository.getMaxSeqInParent(dto.getParentId()));

        //KIND 0:请求组 1:用户请求 
        
        //挂载空请求组
        if (dto.getKind() == 0) {
            UserRequestGroupPo userRequestGroupPo = new UserRequestGroupPo();
            userRequestGroupPo.setTree(userRequestTreePo);
            userRequestGroupPo.setUser(authService.requireUser());
            userRequestGroupPo.setParent(null);
            userRequestGroupPo.setName(dto.getName());
            userRequestGroupPo.setDescription(null);
            userRequestGroupPo.setSeq(0);
            userRequestTreePo.setGroup(userRequestGroupPo);
        }
        //挂载空用户请求
        if (dto.getKind() == 1) {
            UserRequestPo userRequestPo = new UserRequestPo();
            userRequestPo.setTree(userRequestTreePo);
            userRequestPo.setGroup(null);
            
        }

        userRequestTreeRepository.save(userRequestTreePo);
    }


}
