package com.ksptooi.biz.rdbg.service;

import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.biz.rdbg.model.userrequestenv.UserRequestEnvPo;
import com.ksptooi.biz.rdbg.model.userrequestenvstorage.UserRequestEnvStoragePo;
import com.ksptooi.biz.rdbg.model.userrequestenvstorage.dto.AddUserRequestEnvStorageDto;
import com.ksptooi.biz.rdbg.model.userrequestenvstorage.dto.EditUserRequestEnvStorageDto;
import com.ksptooi.biz.rdbg.model.userrequestenvstorage.dto.GetUserRequestEnvStorageListDto;
import com.ksptooi.biz.rdbg.model.userrequestenvstorage.vo.GetUserRequestEnvStorageDetailsVo;
import com.ksptooi.biz.rdbg.model.userrequestenvstorage.vo.GetUserRequestEnvStorageListVo;
import com.ksptooi.biz.rdbg.repoistory.UserRequestEnvRepository;
import com.ksptooi.biz.rdbg.repoistory.UserRequestEnvStorageRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
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
        List<GetUserRequestEnvStorageListVo> vos = as(page.getContent(), GetUserRequestEnvStorageListVo.class);
        return PageResult.success(vos, page.getTotalElements());
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