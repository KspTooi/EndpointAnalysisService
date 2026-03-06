package com.ksptool.bio.biz.gendatsource.service;

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
import com.ksptool.bio.biz.gendatsource.repository.GenDatSourceRepository;
import com.ksptool.bio.biz.gendatsource.model.GenDatSourcePo;
import com.ksptool.bio.biz.gendatsource.model.vo.GetGenDatSourceListVo;
import com.ksptool.bio.biz.gendatsource.model.dto.GetGenDatSourceListDto;
import com.ksptool.bio.biz.gendatsource.model.vo.GetGenDatSourceDetailsVo;
import com.ksptool.bio.biz.gendatsource.model.dto.EditGenDatSourceDto;
import com.ksptool.bio.biz.gendatsource.model.dto.AddGenDatSourceDto;


@Service
public class GenDatSourceService {

    @Autowired
    private GenDatSourceRepository repository;

    public PageResult<GetGenDatSourceListVo> getGenDatSourceList(GetGenDatSourceListDto dto){
        GenDatSourcePo query = new GenDatSourcePo();
        assign(dto,query);

        Page<GenDatSourcePo> page = repository.getGenDatSourceList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetGenDatSourceListVo> vos = as(page.getContent(), GetGenDatSourceListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addGenDatSource(AddGenDatSourceDto dto){
        GenDatSourcePo insertPo = as(dto,GenDatSourcePo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editGenDatSource(EditGenDatSourceDto dto) throws BizException {
        GenDatSourcePo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetGenDatSourceDetailsVo getGenDatSourceDetails(CommonIdDto dto) throws BizException {
        GenDatSourcePo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetGenDatSourceDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeGenDatSource(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}