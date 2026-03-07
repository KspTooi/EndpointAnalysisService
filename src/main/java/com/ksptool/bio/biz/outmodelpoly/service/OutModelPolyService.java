package com.ksptool.bio.biz.outmodelpoly.service;

import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.exception.BizException;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import java.util.Optional;
import com.ksptool.bio.biz.outmodelpoly.repository.OutModelPolyRepository;
import com.ksptool.bio.biz.outmodelpoly.model.OutModelPolyPo;
import com.ksptool.bio.biz.outmodelpoly.model.vo.GetOutModelPolyListVo;
import com.ksptool.bio.biz.outmodelpoly.model.dto.GetOutModelPolyListDto;
import com.ksptool.bio.biz.outmodelpoly.model.vo.GetOutModelPolyDetailsVo;
import com.ksptool.bio.biz.outmodelpoly.model.dto.EditOutModelPolyDto;
import com.ksptool.bio.biz.outmodelpoly.model.dto.AddOutModelPolyDto;


@Service
public class OutModelPolyService {

    @Autowired
    private OutModelPolyRepository repository;

    public PageResult<GetOutModelPolyListVo> getOutModelPolyList(GetOutModelPolyListDto dto){
        OutModelPolyPo query = new OutModelPolyPo();
        assign(dto,query);

        Page<OutModelPolyPo> page = repository.getOutModelPolyList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetOutModelPolyListVo> vos = as(page.getContent(), GetOutModelPolyListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addOutModelPoly(AddOutModelPolyDto dto){
        OutModelPolyPo insertPo = as(dto,OutModelPolyPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editOutModelPoly(EditOutModelPolyDto dto) throws BizException {
        OutModelPolyPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetOutModelPolyDetailsVo getOutModelPolyDetails(CommonIdDto dto) throws BizException {
        OutModelPolyPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetOutModelPolyDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeOutModelPoly(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}