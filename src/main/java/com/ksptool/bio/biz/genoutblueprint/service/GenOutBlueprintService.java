package com.ksptool.bio.biz.genoutblueprint.service;

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
import com.ksptool.bio.biz.genoutblueprint.repository.GenOutBlueprintRepository;
import com.ksptool.bio.biz.genoutblueprint.model.GenOutBlueprintPo;
import com.ksptool.bio.biz.genoutblueprint.model.vo.GetGenOutBlueprintListVo;
import com.ksptool.bio.biz.genoutblueprint.model.dto.GetGenOutBlueprintListDto;
import com.ksptool.bio.biz.genoutblueprint.model.vo.GetGenOutBlueprintDetailsVo;
import com.ksptool.bio.biz.genoutblueprint.model.dto.EditGenOutBlueprintDto;
import com.ksptool.bio.biz.genoutblueprint.model.dto.AddGenOutBlueprintDto;


@Service
public class GenOutBlueprintService {

    @Autowired
    private GenOutBlueprintRepository repository;

    public PageResult<GetGenOutBlueprintListVo> getGenOutBlueprintList(GetGenOutBlueprintListDto dto){
        GenOutBlueprintPo query = new GenOutBlueprintPo();
        assign(dto,query);

        Page<GenOutBlueprintPo> page = repository.getGenOutBlueprintList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetGenOutBlueprintListVo> vos = as(page.getContent(), GetGenOutBlueprintListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addGenOutBlueprint(AddGenOutBlueprintDto dto){
        GenOutBlueprintPo insertPo = as(dto,GenOutBlueprintPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editGenOutBlueprint(EditGenOutBlueprintDto dto) throws BizException {
        GenOutBlueprintPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetGenOutBlueprintDetailsVo getGenOutBlueprintDetails(CommonIdDto dto) throws BizException {
        GenOutBlueprintPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetGenOutBlueprintDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeGenOutBlueprint(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}