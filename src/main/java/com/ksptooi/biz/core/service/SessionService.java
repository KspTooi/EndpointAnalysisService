package com.ksptooi.biz.core.service;

import com.google.gson.Gson;
import com.ksptooi.biz.core.model.session.GetSessionDetailsVo;
import com.ksptooi.biz.core.model.session.GetSessionListDto;
import com.ksptooi.biz.core.model.session.GetSessionListVo;
import com.ksptooi.biz.core.model.session.UserSessionPo;
import com.ksptooi.biz.core.repository.UserRepository;
import com.ksptooi.biz.core.repository.UserSessionRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SessionService {

    @Autowired
    private UserSessionRepository repository;

    @Autowired
    private UserRepository userRepository;


    public PageResult<GetSessionListVo> getSessionList(GetSessionListDto dto) {
        Page<GetSessionListVo> pPos = repository.getSessionList(dto, dto.pageRequest());
        return PageResult.success(pPos.getContent(), pPos.getTotalElements());
    }

    public GetSessionDetailsVo getSessionDetails(Long id) throws BizException {

        UserSessionPo session = repository.findById(id)
                .orElseThrow(() -> new BizException("会话不存在"));

        GetSessionDetailsVo vo = new GetSessionDetailsVo();
        vo.setId(session.getId());
        vo.setUsername("----");
        userRepository.findById(session.getUserId())
                .ifPresent(user -> vo.setUsername(user.getUsername()));
        vo.setCreateTime(session.getCreateTime());
        vo.setExpiresAt(session.getExpiresAt());

        Gson gson = new Gson();
        vo.setPermissions(gson.fromJson(session.getPermissions(), Set.class));
        return vo;
    }

    public void closeSession(Long id) throws BizException {

        UserSessionPo session = repository.findById(id)
                .orElseThrow(() -> new BizException("会话不存在"));

        repository.delete(session);
    }
}
