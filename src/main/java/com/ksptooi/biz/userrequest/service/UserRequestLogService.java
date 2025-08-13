package com.ksptooi.biz.userrequest.service;

import com.ksptooi.biz.userrequest.model.userrequestlog.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.ksptooi.commons.utils.web.PageResult;
import com.ksptooi.commons.utils.web.CommonIdDto;
import org.springframework.data.domain.Page;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.biz.userrequest.repository.UserRequestLogRepository;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class UserRequestLogService {

    @Autowired
    private UserRequestLogRepository repository;

    public PageResult<GetUserRequestLogListVo> getUserRequestLogList(GetUserRequestLogListDto dto){
        UserRequestLogPo query = new UserRequestLogPo();
        assign(dto,query);

        Page<UserRequestLogPo> page = repository.getUserRequestLogList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetUserRequestLogListVo> vos = as(page.getContent(), GetUserRequestLogListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addUserRequestLog(AddUserRequestLogDto dto){
        UserRequestLogPo insertPo = as(dto,UserRequestLogPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editUserRequestLog(EditUserRequestLogDto dto) throws BizException {
        UserRequestLogPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetUserRequestLogDetailsVo getUserRequestLogDetails(CommonIdDto dto) throws BizException {
        UserRequestLogPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在."));
        return as(po,GetUserRequestLogDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeUserRequestLog(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if(!dto.isBatch()){
            repository.deleteById(dto.getId());
        }
    }

}