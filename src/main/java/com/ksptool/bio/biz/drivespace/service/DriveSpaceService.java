package com.ksptool.bio.biz.drivespace.service;

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
import com.ksptool.bio.biz.drivespace.repository.DriveSpaceRepository;
import com.ksptool.bio.biz.drivespace.model.DriveSpacePo;
import com.ksptool.bio.biz.drivespace.model.vo.GetDriveSpaceListVo;
import com.ksptool.bio.biz.drivespace.model.dto.GetDriveSpaceListDto;
import com.ksptool.bio.biz.drivespace.model.vo.GetDriveSpaceDetailsVo;
import com.ksptool.bio.biz.drivespace.model.dto.EditDriveSpaceDto;
import com.ksptool.bio.biz.drivespace.model.dto.AddDriveSpaceDto;


@Service
public class DriveSpaceService {

    @Autowired
    private DriveSpaceRepository repository;

    public PageResult<GetDriveSpaceListVo> getDriveSpaceList(GetDriveSpaceListDto dto){
        DriveSpacePo query = new DriveSpacePo();
        assign(dto,query);

        Page<DriveSpacePo> page = repository.getDriveSpaceList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetDriveSpaceListVo> vos = as(page.getContent(), GetDriveSpaceListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addDriveSpace(AddDriveSpaceDto dto){
        DriveSpacePo insertPo = as(dto,DriveSpacePo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editDriveSpace(EditDriveSpaceDto dto) throws BizException {
        DriveSpacePo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetDriveSpaceDetailsVo getDriveSpaceDetails(CommonIdDto dto) throws BizException {
        DriveSpacePo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetDriveSpaceDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeDriveSpace(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}