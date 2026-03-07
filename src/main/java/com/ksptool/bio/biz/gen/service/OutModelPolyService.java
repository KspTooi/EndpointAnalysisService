package com.ksptool.bio.biz.gen.service;

import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.exception.BizException;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import com.ksptool.bio.biz.gen.repository.OutModelPolyRepository;
import com.ksptool.bio.biz.gen.model.outmodelpoly.OutModelPolyPo;
import com.ksptool.bio.biz.gen.model.outmodelpoly.vo.GetOutModelPolyListVo;
import com.ksptool.bio.biz.gen.model.outmodelpoly.dto.GetOutModelPolyListDto;
import com.ksptool.bio.biz.gen.model.outmodelpoly.vo.GetOutModelPolyDetailsVo;
import com.ksptool.bio.biz.gen.model.outmodelpoly.dto.EditOutModelPolyDto;
import com.ksptool.bio.biz.gen.model.outmodelpoly.dto.AddOutModelPolyDto;


@Service
public class OutModelPolyService {

    @Autowired
    private OutModelPolyRepository repository;

    /**
     * 查询聚合模型列表
     *
     * @param dto 查询参数
     * @return 聚合模型列表
     */
    public PageResult<GetOutModelPolyListVo> getOutModelPolyList(GetOutModelPolyListDto dto) {
        OutModelPolyPo query = new OutModelPolyPo();
        assign(dto, query);

        Page<OutModelPolyPo> page = repository.getOutModelPolyList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetOutModelPolyListVo> vos = as(page.getContent(), GetOutModelPolyListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增聚合模型
     *
     * @param dto 新增参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOutModelPoly(AddOutModelPolyDto dto) {
        OutModelPolyPo insertPo = as(dto, OutModelPolyPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑聚合模型
     *
     * @param dto 编辑参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editOutModelPoly(EditOutModelPolyDto dto) throws BizException {
        OutModelPolyPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询聚合模型详情
     *
     * @param dto 查询参数
     * @return 聚合模型详情
     * @throws BizException 业务异常
     */
    public GetOutModelPolyDetailsVo getOutModelPolyDetails(CommonIdDto dto) throws BizException {
        OutModelPolyPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetOutModelPolyDetailsVo.class);
    }

    /**
     * 删除聚合模型元素
     *
     * @param dto 删除参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeOutModelPoly(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}