package com.ksptooi.biz.qt.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}