package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.filter.SimpleFilterOperationPo;
import com.ksptooi.biz.core.model.filter.SimpleFilterPo;
import com.ksptooi.biz.core.model.filter.SimpleFilterTriggerPo;
import com.ksptooi.biz.core.model.filter.dto.*;
import com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterDetailsVo;
import com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterListVo;
import com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterOperationDetailsVo;
import com.ksptooi.biz.core.model.filter.vo.GetSimpleFilterTriggerDetailsVo;
import com.ksptooi.biz.core.repository.SimpleFilterRepository;
import com.ksptooi.biz.user.model.user.UserPo;
import com.ksptooi.biz.user.service.AuthService;
import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimpleFilterService {

    @Autowired
    private SimpleFilterRepository repository;

    @Autowired
    private AuthService authService;

    /**
     * 获取过滤器列表
     *
     * @param dto 查询条件
     * @return 过滤器列表
     */
    public PageResult<GetSimpleFilterListVo> getSimpleFilterList(GetSimpleFilterListDto dto) {
        Page<GetSimpleFilterListVo> pVos = repository.getSimpleFilterList(dto, dto.pageRequest());
        return PageResult.success(pVos.getContent(), pVos.getTotalElements());
    }

    /**
     * 新增过滤器
     *
     * @param dto 过滤器信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void addSimpleFilter(AddSimpleFilterDto dto) throws AuthException {

        //获取当前登录用户
        UserPo user = authService.requireUser();

        //创建过滤器
        SimpleFilterPo insertPo = new SimpleFilterPo();
        insertPo.setUser(user); //默认为用户所有的过滤器
        insertPo.setName(dto.getName());
        insertPo.setDirection(dto.getDirection());
        insertPo.setStatus(dto.getStatus());

        var seq = 0;

        //处理触发器
        List<SimpleFilterTriggerPo> triggers = new ArrayList<>();
        for (AddSimpleFilterTriggerDto triggerDto : dto.getTriggers()) {
            SimpleFilterTriggerPo trigger = new SimpleFilterTriggerPo();
            trigger.setFilter(insertPo);
            trigger.setTarget(triggerDto.getTarget());
            trigger.setKind(triggerDto.getKind());
            trigger.setTk(triggerDto.getTk());
            trigger.setTv(triggerDto.getTv());
            trigger.setSeq(seq++);
            triggers.add(trigger);
        }

        //重置序号
        seq = 0;
        insertPo.setTriggers(triggers);

        //处理操作
        List<SimpleFilterOperationPo> operations = new ArrayList<>();
        for (AddSimpleFilterOperationDto operationDto : dto.getOperations()) {
            SimpleFilterOperationPo operation = new SimpleFilterOperationPo();
            operation.setFilter(insertPo);
            operation.setKind(operationDto.getKind());
            operation.setTarget(operationDto.getTarget());
            operation.setF(operationDto.getF());
            operation.setT(operationDto.getT());
            operation.setSeq(seq++);
            operations.add(operation);
        }
        insertPo.setOperations(operations);

        //保存过滤器
        repository.save(insertPo);
    }

    /**
     * 编辑过滤器
     *
     * @param dto 过滤器信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void editSimpleFilter(EditSimpleFilterDto dto) throws BizException, AuthException {

        //获取当前登录用户
        UserPo user = authService.requireUser();

        SimpleFilterPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        //判断过滤器是否属于当前用户
        if (updatePo.getUser() != null && !updatePo.getUser().getId().equals(user.getId())) {
            throw new AuthException("无权限操作该过滤器");
        }

        //处理过滤器基本信息更新
        updatePo.setName(dto.getName());
        updatePo.setDirection(dto.getDirection());
        updatePo.setStatus(dto.getStatus());

        //处理触发器
        List<SimpleFilterTriggerPo> triggerPos = updatePo.getTriggers();
        List<Long> removeTriggerIds = new ArrayList<>();

        //搜集现存触发器中最大的排序
        var maxSeq = 0;
        for (var item : triggerPos) {
            if (item.getSeq() > maxSeq) {
                maxSeq = item.getSeq();
            }
        }

        //处理新增触发器 (前端传入的触发器中，id为null的为新增)
        for (var dtoItem : dto.getTriggers()) {
            if (dtoItem.getId() == null) {
                SimpleFilterTriggerPo trigger = new SimpleFilterTriggerPo();
                trigger.setFilter(updatePo);
                trigger.setTarget(dtoItem.getTarget());
                trigger.setKind(dtoItem.getKind());
                trigger.setTk(dtoItem.getTk());
                trigger.setTv(dtoItem.getTv());
                trigger.setSeq(maxSeq++);
                triggerPos.add(trigger);
            }
        }

        //处理更新与删除触发器
        for (var item : triggerPos) {

            var needRemove = true;

            //更新触发器并搜集需要删除的触发器
            for (var dtoItem : dto.getTriggers()) {
                if (dtoItem.getId() == item.getId()) {
                    item.setTarget(dtoItem.getTarget());
                    item.setKind(dtoItem.getKind());
                    item.setTk(dtoItem.getTk());
                    item.setTv(dtoItem.getTv());
                    item.setSeq(dtoItem.getSeq());
                    needRemove = false;
                }
            }

            if (needRemove) {
                removeTriggerIds.add(item.getId());
            }

        }

        //删除需要删除的触发器
        if (!removeTriggerIds.isEmpty()) {
            triggerPos.removeIf(item -> removeTriggerIds.contains(item.getId()));
        }

        //处理操作
        List<SimpleFilterOperationPo> operationPos = updatePo.getOperations();
        List<Long> removeOperationIds = new ArrayList<>();

        //搜集现存操作中最大的排序
        maxSeq = 0;
        for (var item : operationPos) {
            if (item.getSeq() > maxSeq) {
                maxSeq = item.getSeq();
            }
        }

        //处理新增操作 (前端传入的操作中，id为null的为新增)
        for (var dtoItem : dto.getOperations()) {
            if (dtoItem.getId() == null) {
                SimpleFilterOperationPo operation = new SimpleFilterOperationPo();
                operation.setFilter(updatePo);
                operation.setKind(dtoItem.getKind());
                operation.setTarget(dtoItem.getTarget());
                operation.setF(dtoItem.getF());
                operation.setT(dtoItem.getT());
                operation.setSeq(maxSeq++);
                operationPos.add(operation);
            }
        }

        //处理更新与删除操作
        for (var item : operationPos) {
            var needRemove = true;
            for (var dtoItem : dto.getOperations()) {
                if (dtoItem.getId() == item.getId()) {
                    item.setKind(dtoItem.getKind());
                    item.setTarget(dtoItem.getTarget());
                    item.setF(dtoItem.getF());
                    item.setT(dtoItem.getT());
                    item.setSeq(dtoItem.getSeq());
                    needRemove = false;
                }
            }

            if (needRemove) {
                removeOperationIds.add(item.getId());
            }

        }

        //删除需要删除的操作
        if (!removeOperationIds.isEmpty()) {
            operationPos.removeIf(item -> removeOperationIds.contains(item.getId()));
        }

        //保存过滤器
        repository.save(updatePo);
    }

    /**
     * 获取过滤器详情
     *
     * @param dto 过滤器ID
     * @return 过滤器详情
     */
    public GetSimpleFilterDetailsVo getSimpleFilterDetails(CommonIdDto dto) throws BizException, AuthException {
        SimpleFilterPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("过滤器不存在"));

        //判断过滤器是否属于当前用户
        if (po.getUser() != null && !po.getUser().getId().equals(authService.requireUser().getId())) {
            throw new AuthException("无权限操作该过滤器");
        }

        //处理基础信息
        GetSimpleFilterDetailsVo vo = new GetSimpleFilterDetailsVo();
        vo.setId(po.getId());
        vo.setName(po.getName());
        vo.setDirection(po.getDirection());
        vo.setStatus(po.getStatus());

        //处理触发器
        List<GetSimpleFilterTriggerDetailsVo> triggerVos = new ArrayList<>();
        for (var item : po.getTriggers()) {
            GetSimpleFilterTriggerDetailsVo triggerVo = new GetSimpleFilterTriggerDetailsVo();
            triggerVo.setId(item.getId());
            triggerVo.setName(item.getName());
            triggerVo.setTarget(item.getTarget());
            triggerVo.setKind(item.getKind());
            triggerVo.setTk(item.getTk());
            triggerVo.setTv(item.getTv());
            triggerVo.setSeq(item.getSeq());
            triggerVos.add(triggerVo);
        }

        //处理操作
        List<GetSimpleFilterOperationDetailsVo> operationVos = new ArrayList<>();

        for (var item : po.getOperations()) {
            GetSimpleFilterOperationDetailsVo operationVo = new GetSimpleFilterOperationDetailsVo();
            operationVo.setId(item.getId());
            operationVo.setName(item.getName());
            operationVo.setKind(item.getKind());
            operationVo.setTarget(item.getTarget());
            operationVo.setF(item.getF());
            operationVo.setT(item.getT());
            operationVo.setSeq(item.getSeq());
            operationVos.add(operationVo);
        }

        vo.setTriggers(triggerVos);
        vo.setOperations(operationVos);
        return vo;
    }

    /**
     * 删除过滤器
     *
     * @param dto 过滤器ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeSimpleFilter(CommonIdDto dto) throws BizException, AuthException {
        if (dto.isBatch()) {
            throw new BizException("批量删除过滤器不支持");
        }
        //获取过滤器
        SimpleFilterPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("过滤器不存在"));

        //判断过滤器是否属于当前用户
        if (po.getUser() != null && !po.getUser().getId().equals(authService.requireUser().getId())) {
            throw new AuthException("无权限操作该过滤器");
        }

        repository.delete(po);
    }

}