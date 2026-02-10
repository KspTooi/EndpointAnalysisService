package com.ksptooi.biz.qt.service;

import com.ksptooi.biz.qt.common.LocalBeanExecutionJob;
import com.ksptooi.biz.qt.common.QuickTask;
import com.ksptooi.biz.qt.common.QuickTaskRegistry;
import com.ksptooi.biz.qt.model.qttask.QtTaskPo;
import com.ksptooi.biz.qt.model.qttask.dto.AddQtTaskDto;
import com.ksptooi.biz.qt.model.qttask.dto.EditQtTaskDto;
import com.ksptooi.biz.qt.model.qttask.dto.GetQtTaskListDto;
import com.ksptooi.biz.qt.model.qttask.vo.GetLocalBeanListVo;
import com.ksptooi.biz.qt.model.qttask.vo.GetQtTaskDetailsVo;
import com.ksptooi.biz.qt.model.qttask.vo.GetQtTaskListVo;
import com.ksptooi.biz.qt.model.qttaskgroup.QtTaskGroupPo;
import com.ksptooi.biz.qt.repository.QtTaskGroupRepository;
import com.ksptooi.biz.qt.repository.QtTaskRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
@Slf4j
public class QtTaskService {

    @Autowired
    private QtTaskRepository repository;

    @Autowired
    private QtTaskGroupRepository groupRepository;

    @Autowired
    private Scheduler scheduler;

    /**
     * 获取任务列表
     *
     * @param dto 查询条件
     * @return 任务列表
     */
    public PageResult<GetQtTaskListVo> getQtTaskList(GetQtTaskListDto dto) {
        QtTaskPo query = new QtTaskPo();
        assign(dto, query);

        Page<QtTaskPo> page = repository.getQtTaskList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetQtTaskListVo> vos = as(page.getContent(), GetQtTaskListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增任务
     *
     * @param dto 新增任务信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void addQtTask(AddQtTaskDto dto) throws BizException {
        QtTaskPo insertPo = as(dto, QtTaskPo.class);

        //验证任务名称是否被占用
        Long count = repository.countByName(dto.getName());
        if (count > 0) {
            throw new BizException("任务名称已存在:[" + dto.getName() + "]");
        }

        //如果配置了分组 需要处理分组信息
        if (dto.getGroupId() != null) {
            QtTaskGroupPo groupPo = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new BizException("任务分组不存在:[" + dto.getGroupId() + "]"));
            insertPo.setGroupName(groupPo.getName());
        }

        //验证任务信息
        String validateResult = insertPo.validate();

        if (validateResult != null) {
            throw new BizException(validateResult);
        }

        //保存任务信息
        repository.save(insertPo);

        //更新Quartz Job
        updateQuartzJob(insertPo);
    }

    /**
     * 编辑任务
     *
     * @param dto 编辑任务信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void editQtTask(EditQtTaskDto dto) throws BizException {
        QtTaskPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);

        //验证任务名称是否被占用
        Long count = repository.countByNameExcludeId(dto.getName(), updatePo.getId());
        if (count > 0) {
            throw new BizException("任务名称已存在:[" + dto.getName() + "]");
        }

        //如果配置了分组 需要处理分组信息
        if (dto.getGroupId() != null) {
            QtTaskGroupPo groupPo = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new BizException("任务分组不存在:[" + dto.getGroupId() + "]"));
            updatePo.setGroupName(groupPo.getName());
        }

        //验证任务信息
        String validateResult = updatePo.validate();

        if (validateResult != null) {
            throw new BizException(validateResult);
        }

        //保存任务信息
        repository.save(updatePo);

        //更新Quartz Job
        updateQuartzJob(updatePo);
    }

    /**
     * 获取任务详情
     *
     * @param dto 获取任务详情条件
     * @return 任务详情
     */
    public GetQtTaskDetailsVo getQtTaskDetails(CommonIdDto dto) throws BizException {
        QtTaskPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetQtTaskDetailsVo.class);
    }

    /**
     * 删除任务
     *
     * @param dto 删除任务条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeQtTask(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

    /**
     * 获取本地任务Bean列表
     *
     * @return 本地任务Bean列表
     */
    public List<GetLocalBeanListVo> getLocalBeanList() {

        List<GetLocalBeanListVo> vos = new ArrayList<>();
        for (QuickTask<?> bean : QuickTaskRegistry.getBeans()) {
            GetLocalBeanListVo vo = new GetLocalBeanListVo();
            vo.setName(bean.getClass().getSimpleName());
            vo.setFullClassName(bean.getClass().getName());
            vos.add(vo);
        }
        return vos;
    }


    /**
     * 更新Quartz Job
     *
     * @param po 任务信息
     */
    public void updateQuartzJob(QtTaskPo po) throws BizException {

        //构建JobDetail
        JobDetail jobDetail = JobBuilder.newJob(LocalBeanExecutionJob.class)
                .withIdentity(po.getIdentity())
                .usingJobData("taskId", po.getId())
                .usingJobData("target", po.getTarget()) // Bean Name
                .usingJobData("params", po.getTargetParam()) // JSON
                .build();

        //构建Trigger
        var cronSchedule = CronScheduleBuilder.cronSchedule(po.getCron());

        //处理过期策略 0:放弃执行(不补跑)
        if (po.getMisfirePolicy() == 0) {
            cronSchedule.withMisfireHandlingInstructionDoNothing();
        }

        //1:立即执行(仅一次)
        if (po.getMisfirePolicy() == 1) {
            cronSchedule.withMisfireHandlingInstructionFireAndProceed();
        }

        //2:全部执行(补跑所有)
        if (po.getMisfirePolicy() == 2) {
            cronSchedule.withMisfireHandlingInstructionIgnoreMisfires();
        }

        var trigger = TriggerBuilder.newTrigger()
                .withIdentity(po.getIdentity())
                .withSchedule(cronSchedule)
                .build();

        try {

            //如果任务存在则覆盖
            if (scheduler.checkExists(jobDetail.getKey())) {
                scheduler.deleteJob(jobDetail.getKey());
            }

            //只有状态为“正常”才调度，暂停的不调度
            if (po.getStatus() == 0) {
                scheduler.scheduleJob(jobDetail, trigger);
            }

        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
            throw new BizException("处理QuartZ任务时发生了一个错误,请联系管理员.");
        }


    }


}