package com.ksptooi.biz.requestdebug.service;

import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.biz.requestdebug.model.filter.SimpleFilterPo;
import com.ksptooi.biz.requestdebug.model.filter.vo.GetSimpleFilterListVo;
import com.ksptooi.biz.requestdebug.model.userrequestgroup.EditUserRequestGroupDto;
import com.ksptooi.biz.requestdebug.model.userrequestgroup.GetUserRequestGroupDetailsVo;
import com.ksptooi.biz.requestdebug.model.userrequestgroup.UserRequestGroupPo;
import com.ksptooi.biz.requestdebug.model.userrequesttree.UserRequestTreePo;
import com.ksptooi.biz.requestdebug.repoistory.SimpleFilterRepository;
import com.ksptooi.biz.requestdebug.repoistory.UserRequestGroupRepository;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRequestGroupService {

    @Autowired
    private UserRequestGroupRepository repository;

    @Autowired
    private AuthService authService;

    @Autowired
    private SimpleFilterRepository simpleFilterRepository;


    /**
     * 编辑请求组
     *
     * @param dto 编辑请求组参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void editUserRequestGroup(EditUserRequestGroupDto dto) throws BizException, AuthException {

        UserPo user = authService.requireUser();
        UserRequestGroupPo updatePo = repository.getRequestGroupByIdAndUserId(dto.getId(), user.getId());

        if (updatePo == null) {
            throw new BizException("更新失败,数据不存在.");
        }

        //如果组有更名 同步修改树
        if (!updatePo.getName().equals(dto.getName())) {
            UserRequestTreePo treePo = updatePo.getTree();
            treePo.setName(dto.getName());
        }

        //处理基本信息
        updatePo.setName(dto.getName());
        updatePo.setDescription(dto.getDescription());

        //查找过滤器
        List<SimpleFilterPo> filterPos = simpleFilterRepository.getSimpleFilterByIds(dto.getSimpleFilterIds(), user.getId());
        //查找已绑定的过滤器
        List<SimpleFilterPo> applyedFilterPos = updatePo.getFilters();

        if (dto.getSimpleFilterIds().size() != filterPos.size()) {
            throw new BizException("无法处理过滤器应用,至少有一个过滤器不存在.");
        }

        //已绑定的过滤器中不存在前端传入的过滤器 处理新增
        for (var item : filterPos) {
            var find = false;
            for (var applyedItem : applyedFilterPos) {
                if (applyedItem.getId().equals(item.getId())) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                item.getGroups().add(updatePo);
            }
        }

        //前端的过滤器列表中无法找到已绑定的过滤器 处理关系解绑
        for (var item : applyedFilterPos) {
            var find = false;
            for (var filterPosItem : filterPos) {
                if (filterPosItem.getId().equals(item.getId())) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                item.getGroups().remove(updatePo);
            }
        }

        //级联修改
        repository.save(updatePo);
    }

    /**
     * 查询请求组详情
     *
     * @param dto 查询请求组参数
     * @return 请求组详情
     */
    public GetUserRequestGroupDetailsVo getUserRequestGroupDetails(CommonIdDto dto) throws BizException {
        UserRequestGroupPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        GetUserRequestGroupDetailsVo vo = new GetUserRequestGroupDetailsVo();
        vo.setId(po.getId());
        vo.setName(po.getName());
        vo.setDescription(po.getDescription());

        //查询在此组上应用的基本过滤器
        List<GetSimpleFilterListVo> simpleFilterVos = simpleFilterRepository.getSimpleFilterListByGroupId(po.getId());
        vo.setSimpleFilters(simpleFilterVos);
        return vo;
    }

}