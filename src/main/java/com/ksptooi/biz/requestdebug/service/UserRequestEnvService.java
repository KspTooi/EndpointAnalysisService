package com.ksptooi.biz.requestdebug.service;

import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.biz.requestdebug.model.userrequestenv.UserRequestEnvPo;
import com.ksptooi.biz.requestdebug.model.userrequestenv.dto.AddUserRequestEnvDto;
import com.ksptooi.biz.requestdebug.model.userrequestenv.dto.EditUserRequestEnvDto;
import com.ksptooi.biz.requestdebug.model.userrequestenv.dto.GetUserRequestEnvListDto;
import com.ksptooi.biz.requestdebug.model.userrequestenv.vo.GetUserRequestEnvDetailsVo;
import com.ksptooi.biz.requestdebug.model.userrequestenv.vo.GetUserRequestEnvListVo;
import com.ksptooi.biz.requestdebug.repoistory.UserRequestEnvRepository;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class UserRequestEnvService {

    @Autowired
    private UserRequestEnvRepository repository;

    @Autowired
    private AuthService authService;

    public PageResult<GetUserRequestEnvListVo> getUserRequestEnvList(GetUserRequestEnvListDto dto) throws AuthException {
        UserRequestEnvPo query = new UserRequestEnvPo();
        assign(dto, query);

        Page<UserRequestEnvPo> page = repository.getUserRequestEnvList(query, AuthService.requireUserId(), dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetUserRequestEnvListVo> vos = as(page.getContent(), GetUserRequestEnvListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addUserRequestEnv(AddUserRequestEnvDto dto) throws AuthException {
        UserRequestEnvPo insertPo = as(dto, UserRequestEnvPo.class);
        insertPo.setUser(authService.requireUser());
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editUserRequestEnv(EditUserRequestEnvDto dto) throws BizException {
        UserRequestEnvPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetUserRequestEnvDetailsVo getUserRequestEnvDetails(CommonIdDto dto) throws BizException {
        UserRequestEnvPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetUserRequestEnvDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeUserRequestEnv(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}