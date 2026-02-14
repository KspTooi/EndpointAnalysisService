package com.ksptooi.biz.qt.service;

import com.ksptooi.biz.qt.model.qttask.QtTaskPo;
import com.ksptooi.biz.qt.model.qttaskrcd.QtTaskRcdPo;
import com.ksptooi.biz.qt.model.qttaskrcd.dto.AddQtTaskRcdDto;
import com.ksptooi.biz.qt.model.qttaskrcd.dto.EditQtTaskRcdDto;
import com.ksptooi.biz.qt.model.qttaskrcd.dto.GetQtTaskRcdListDto;
import com.ksptooi.biz.qt.model.qttaskrcd.vo.GetQtTaskRcdDetailsVo;
import com.ksptooi.biz.qt.model.qttaskrcd.vo.GetQtTaskRcdListVo;
import com.ksptooi.biz.qt.repository.QtTaskRcdRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;

import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class QtTaskRcdService {

    @Autowired
    private QtTaskRcdRepository repository;

    /**
     * 查询调度日志列表
     * @param dto
     * @return
     */
    public PageResult<GetQtTaskRcdListVo> getQtTaskRcdList(GetQtTaskRcdListDto dto) {
        QtTaskRcdPo query = new QtTaskRcdPo();
        assign(dto, query);

        Page<QtTaskRcdPo> page = repository.getQtTaskRcdList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetQtTaskRcdListVo> vos = as(page.getContent(), GetQtTaskRcdListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }


    /**
     * 查询调度日志详情
     * @param dto
     * @return
     * @throws BizException
     */
    public GetQtTaskRcdDetailsVo getQtTaskRcdDetails(CommonIdDto dto) throws BizException {
        QtTaskRcdPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetQtTaskRcdDetailsVo.class);
    }

    /**
     * 删除调度日志
     * @param dto
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeQtTaskRcd(CommonIdDto dto) throws BizException {

        var ids = dto.getIds();

        if (ids == null || ids.isEmpty()) {
            return;
        }

        repository.deleteAllById(ids);
    }

    /**
     * 异步记录任务调度日志
     * @param dataMap 任务数据
     * @param result 调用目标返回内容(错误时为异常堆栈)
     * @param status 运行状态 0:正常 1:失败 2:超时 3:已调度
     * @param taskPo 任务对象
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void addQtTaskRcdAsync(JobDataMap dataMap,String result,String status,QtTaskPo taskPo) {

        var taskId = dataMap.getLong("taskId"); // 任务ID
        var beanName = dataMap.getString("target");   // BEAN代码
        var jsonParams = dataMap.getString("params"); // 调用参数JSON
        var policyError = dataMap.getInt("policyError"); // 失败策略 0:默认 1:自动暂停
        var policyRcd = dataMap.getInt("policyRcd"); // 日志策略 0:全部 1:仅异常 2:不记录

    }

    
    /**
     * 创建新的任务调度日志
     * @param taskPo 任务对象
     * @return 任务调度日志对象
     */
    public QtTaskRcdPo createNewTaskRcd(QtTaskPo taskPo){
        var taskRcdPo = new QtTaskRcdPo();
        taskRcdPo.setTaskId(taskPo.getId());
        taskRcdPo.setTaskName(taskPo.getName());
        taskRcdPo.setGroupName(taskPo.getGroupName());
        taskRcdPo.setTarget(taskPo.getTarget());
        taskRcdPo.setTargetParam(taskPo.getTargetParam());
        taskRcdPo.setTargetResult(null);
        taskRcdPo.setStatus(3); //0:正常 1:失败 2:超时 3:已调度
        taskRcdPo.setStartTime(LocalDateTime.now());
        taskRcdPo.setEndTime(null);
        taskRcdPo.setCostTime(null);
        return repository.saveAndFlush(taskRcdPo);
    }

    /**
     * 创建新的任务调度日志 并设置异常状态
     * @param taskPo 任务对象
     * @return 任务调度日志对象
     */
    public QtTaskRcdPo buildNewTaskRcdWithException(QtTaskPo taskPo){
        var taskRcdPo = new QtTaskRcdPo();
        taskRcdPo.setTaskId(taskPo.getId());
        taskRcdPo.setTaskName(taskPo.getName());
        taskRcdPo.setGroupName(taskPo.getGroupName());
        taskRcdPo.setTarget(taskPo.getTarget());
        taskRcdPo.setTargetParam(taskPo.getTargetParam());
        taskRcdPo.setTargetResult(null);
        taskRcdPo.setStatus(1); //0:正常 1:失败 2:超时 3:已调度
        taskRcdPo.setStartTime(LocalDateTime.now());
        taskRcdPo.setEndTime(null);
        taskRcdPo.setCostTime(null);
        return taskRcdPo;
    }

}