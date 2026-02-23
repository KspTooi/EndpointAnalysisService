package com.ksptool.bio.biz.zremovedauthgroup.service;

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
import com.ksptool.bio.biz.zremovedauthgroup.repository.ZremovedAuthGroupRepository;
import com.ksptool.bio.biz.zremovedauthgroup.model.ZremovedAuthGroupPo;
import com.ksptool.bio.biz.zremovedauthgroup.model.vo.GetZremovedAuthGroupListVo;
import com.ksptool.bio.biz.zremovedauthgroup.model.dto.GetZremovedAuthGroupListDto;
import com.ksptool.bio.biz.zremovedauthgroup.model.vo.GetZremovedAuthGroupDetailsVo;
import com.ksptool.bio.biz.zremovedauthgroup.model.dto.EditZremovedAuthGroupDto;
import com.ksptool.bio.biz.zremovedauthgroup.model.dto.AddZremovedAuthGroupDto;


@Service
public class ZremovedAuthGroupService {

    @Autowired
    private ZremovedAuthGroupRepository repository;

    public PageResult<GetZremovedAuthGroupListVo> getZremovedAuthGroupList(GetZremovedAuthGroupListDto dto){
        ZremovedAuthGroupPo query = new ZremovedAuthGroupPo();
        assign(dto,query);

        Page<ZremovedAuthGroupPo> page = repository.getZremovedAuthGroupList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetZremovedAuthGroupListVo> vos = as(page.getContent(), GetZremovedAuthGroupListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addZremovedAuthGroup(AddZremovedAuthGroupDto dto){
        ZremovedAuthGroupPo insertPo = as(dto,ZremovedAuthGroupPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editZremovedAuthGroup(EditZremovedAuthGroupDto dto) throws BizException {
        ZremovedAuthGroupPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetZremovedAuthGroupDetailsVo getZremovedAuthGroupDetails(CommonIdDto dto) throws BizException {
        ZremovedAuthGroupPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetZremovedAuthGroupDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeZremovedAuthGroup(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}