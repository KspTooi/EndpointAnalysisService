package com.ksptooi.biz.core.service;

import com.ksptooi.biz.auth.service.AuthService;
import com.ksptooi.biz.core.model.config.*;
import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.core.repository.ConfigRepository;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.entities.any.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptooi.biz.core.service.SessionService.session;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class ConfigService {

    @Autowired
    private ConfigRepository repository;

    public PageResult<GetConfigListVo> getConfigList(GetConfigListDto dto) throws AuthException {

        Long userId = session().getUserId();

        if (AuthService.hasPermission("panel:config:view:global")) {
            userId = null;
        }

        Pageable pageQuery = PageRequest.of(dto.getPageNum() - 1, dto.getPageSize(), Sort.Direction.DESC, "updateTime");
        Page<GetConfigListVo> pPos = repository.getConfigList(dto.getKeyword(), dto.getUserName(), userId, pageQuery);
        List<GetConfigListVo> vos = as(pPos.getContent(), GetConfigListVo.class);

        for (GetConfigListVo vo : vos) {
            if (vo.getUserName() == null) {
                vo.setUserName("全局");
            }
        }

        return PageResult.success(vos, pPos.getTotalElements());
    }

    public GetConfigDetailsVo getConfigDetails(Long id) throws BizException, AuthException {

        var query = new ConfigPo();
        query.setId(id);

        //无全局数据权限时仅查询当前玩家下的配置
        if (!AuthService.hasPermission("panel:config:view:global")) {
            query.setUser(Any.of().val("id", session().getUserId()).as(UserPo.class));
        }

        ConfigPo po = repository.findOne(Example.of(query))
                .orElseThrow(() -> new BizException("配置项不存在或无权访问"));

        GetConfigDetailsVo vo = as(po, GetConfigDetailsVo.class);

        if (po.getUser() == null) {
            vo.setPlayerName("全局");
        }

        if (po.getUser() != null) {
            vo.setPlayerName(po.getUser().getUsername());
        }

        return vo;
    }

    @Transactional
    public void addConfig(AddConfigDto dto) throws BizException, AuthException {
        if (repository.existsByUserIdAndConfigKey(session().getUserId(), dto.getConfigKey())) {
            throw new RuntimeException("配置键已存在");
        }
        ConfigPo config = new ConfigPo();
        assign(dto, config);
        config.setUser(Any.of().val("id", session().getUserId()).as(UserPo.class));
        repository.save(config);
    }

    @Transactional
    public void editConfig(EditConfigDto dto) throws BizException, AuthException {
        var query = new ConfigPo();
        query.setId(dto.getId());

        if (!AuthService.hasPermission("panel:config:view:global")) {
            query.setUser(Any.of().val("id", session().getUserId()).as(UserPo.class));
        }

        ConfigPo po = repository.findOne(Example.of(query))
                .orElseThrow(() -> new BizException("配置项不存在或无权访问"));

        po.setConfigValue(dto.getConfigValue());
        po.setDescription(dto.getDescription());
        repository.save(po);
    }

    @Transactional
    public void removeConfig(Long id) throws BizException, AuthException {

        var query = new ConfigPo();
        query.setId(id);

        //无全局数据权限时仅查询当前用户下的配置
        if (!AuthService.hasPermission("panel:config:view:global")) {
            query.setUser(Any.of().val("id", session().getUserId()).as(UserPo.class));
        }

        ConfigPo po = repository.findOne(Example.of(query))
                .orElseThrow(() -> new BizException("配置项不存在或无权访问"));

        repository.delete(po);
    }
}
