package com.ksptooi.biz.userrequest.service;

import com.ksptooi.biz.user.service.AuthService;
import com.ksptooi.biz.userrequest.model.userrequestenv.UserRequestEnvPo;
import com.ksptooi.biz.userrequest.model.userrequestenvstorage.UserRequestEnvStoragePo;
import com.ksptooi.biz.userrequest.model.userrequestenvstorage.dto.AddUserRequestEnvStorageDto;
import com.ksptooi.biz.userrequest.model.userrequestenvstorage.dto.EditUserRequestEnvStorageDto;
import com.ksptooi.biz.userrequest.model.userrequestenvstorage.dto.GetUserRequestEnvStorageListDto;
import com.ksptooi.biz.userrequest.model.userrequestenvstorage.vo.GetUserRequestEnvStorageDetailsVo;
import com.ksptooi.biz.userrequest.model.userrequestenvstorage.vo.GetUserRequestEnvStorageListVo;
import com.ksptooi.biz.userrequest.repository.UserRequestEnvRepository;
import com.ksptooi.biz.userrequest.repository.UserRequestEnvStorageRepository;
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
public class UserRequestEnvStorageService {

    @Autowired
    private UserRequestEnvStorageRepository repository;

    @Autowired
    private UserRequestEnvRepository userRequestEnvRepository;


    public PageResult<GetUserRequestEnvStorageListVo> getUserRequestEnvStorageList(GetUserRequestEnvStorageListDto dto) throws Exception {

        //查询该用户下的环境
        UserRequestEnvPo env = userRequestEnvRepository.getByIdAndUserId(dto.getRequestEnvId(), AuthService.requireUserId());

        if (env == null) {
            throw new BizException("用户环境不存在或无权限访问");
        }

        UserRequestEnvStoragePo query = new UserRequestEnvStoragePo();
        assign(dto, query);

        Page<UserRequestEnvStoragePo> page = repository.getUserRequestEnvStorageList(query, dto.getRequestEnvId(), dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetUserRequestEnvStorageListVo> vos = as(page.getContent(), GetUserRequestEnvStorageListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addUserRequestEnvStorage(AddUserRequestEnvStorageDto dto) throws Exception {
        UserRequestEnvStoragePo insertPo = as(dto, UserRequestEnvStoragePo.class);

        //查询该用户下的环境
        UserRequestEnvPo env = userRequestEnvRepository.getByIdAndUserId(dto.getRequestEnvId(), AuthService.requireUserId());
        if (env == null) {
            throw new BizException("用户环境不存在或无权限访问");
        }

        insertPo.setEnv(env);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editUserRequestEnvStorage(EditUserRequestEnvStorageDto dto) throws BizException {
        UserRequestEnvStoragePo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    public GetUserRequestEnvStorageDetailsVo getUserRequestEnvStorageDetails(CommonIdDto dto) throws BizException {
        UserRequestEnvStoragePo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetUserRequestEnvStorageDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeUserRequestEnvStorage(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}