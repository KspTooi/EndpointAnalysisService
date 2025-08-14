package com.ksptooi.biz.simplefilter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.ksptooi.commons.utils.web.PageResult;
import com.ksptooi.commons.utils.web.CommonIdDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import com.ksptooi.commons.exception.BizException;
import java.util.Optional;
import com.ksptooi.biz.simplefilter.repository.SimpleFilterRepository;
import com.ksptooi.biz.simplefilter.model.po.SimpleFilterPo;
import com.ksptooi.biz.simplefilter.model.vo.GetSimpleFilterListVo;
import com.ksptooi.biz.simplefilter.model.dto.GetSimpleFilterListDto;
import com.ksptooi.biz.simplefilter.model.vo.GetSimpleFilterDetailsVo;
import com.ksptooi.biz.simplefilter.model.dto.EditSimpleFilterDto;
import com.ksptooi.biz.simplefilter.model.dto.AddSimpleFilterDto;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class SimpleFilterService {

    @Autowired
    private SimpleFilterRepository repository;

    public PageResult<GetSimpleFilterListVo> getSimpleFilterList(GetSimpleFilterListDto dto){
        SimpleFilterPo query = new SimpleFilterPo();
        assign(dto,query);

        Page<SimpleFilterPo> page = repository.getSimpleFilterList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetSimpleFilterListVo> vos = as(page.getContent(), GetSimpleFilterListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSimpleFilter(AddSimpleFilterDto dto){
        SimpleFilterPo insertPo = as(dto,SimpleFilterPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editSimpleFilter(EditSimpleFilterDto dto) throws BizException {
        SimpleFilterPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetSimpleFilterDetailsVo getSimpleFilterDetails(CommonIdDto dto) throws BizException {
        SimpleFilterPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在."));
        return as(po,GetSimpleFilterDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeSimpleFilter(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if(!dto.isBatch()){
            repository.deleteById(dto.getId());
        }
    }

}