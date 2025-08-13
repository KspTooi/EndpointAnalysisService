package com.ksptooi.biz.userrequest.service;

import com.ksptooi.biz.core.model.request.RequestPo;
import com.ksptooi.biz.core.repository.RequestRepository;
import com.ksptooi.biz.user.service.AuthService;
import com.ksptooi.biz.userrequest.model.userrequest.*;

import com.ksptooi.commons.utils.web.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import jakarta.security.auth.message.AuthException;

import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.biz.userrequest.repository.UserRequestRepository;


import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class UserRequestService {

    @Autowired
    private UserRequestRepository repository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private AuthService authService;


    /**
     * 保存原始请求为用户请求
     * @param dto 保存请求参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAsUserRequest(SaveAsUserRequestDto dto) throws BizException,AuthException{

        //查询原始请求
        RequestPo requestPo = requestRepository.findById(dto.getRequestId())
            .orElseThrow(()-> new BizException("原始请求不存在."));

        //创建用户请求
        UserRequestPo userRequestPo = new UserRequestPo();
        userRequestPo.setGroup(null);
        userRequestPo.setOriginalRequest(requestPo);
        userRequestPo.setUser(authService.requireUser());
        userRequestPo.setName(requestPo.getRequestId());//如果未提供名称 则使用请求ID作为名称

        if(StringUtils.isNotBlank(dto.getName())){
            userRequestPo.setName(dto.getName());
        }

        userRequestPo.setSeq(0);
        repository.save(userRequestPo);
    }


    /**
     * 获取用户请求树
     * @param dto 获取请求参数
     * @return 用户请求树
     */
    public List<GetUserRequestTreeVo> getUserRequestTree(GetUserRequestTreeDto dto){

        

        return null;
    }



    @Transactional(rollbackFor = Exception.class)
    public void editUserRequest(EditUserRequestDto dto) throws BizException {
        UserRequestPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetUserRequestDetailsVo getUserRequestDetails(CommonIdDto dto) throws BizException {
        UserRequestPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在."));
        return as(po,GetUserRequestDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeUserRequest(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if(!dto.isBatch()){
            repository.deleteById(dto.getId());
        }
    }

}