package com.ksptool.bio.biz.drivespacemember.service;

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
import com.ksptool.bio.biz.drivespacemember.repository.DriveSpaceMemberRepository;
import com.ksptool.bio.biz.drivespacemember.model.DriveSpaceMemberPo;
import com.ksptool.bio.biz.drivespacemember.model.vo.GetDriveSpaceMemberListVo;
import com.ksptool.bio.biz.drivespacemember.model.dto.GetDriveSpaceMemberListDto;
import com.ksptool.bio.biz.drivespacemember.model.vo.GetDriveSpaceMemberDetailsVo;
import com.ksptool.bio.biz.drivespacemember.model.dto.EditDriveSpaceMemberDto;
import com.ksptool.bio.biz.drivespacemember.model.dto.AddDriveSpaceMemberDto;


@Service
public class DriveSpaceMemberService {

    @Autowired
    private DriveSpaceMemberRepository repository;

    public PageResult<GetDriveSpaceMemberListVo> getDriveSpaceMemberList(GetDriveSpaceMemberListDto dto){
        DriveSpaceMemberPo query = new DriveSpaceMemberPo();
        assign(dto,query);

        Page<DriveSpaceMemberPo> page = repository.getDriveSpaceMemberList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetDriveSpaceMemberListVo> vos = as(page.getContent(), GetDriveSpaceMemberListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addDriveSpaceMember(AddDriveSpaceMemberDto dto){
        DriveSpaceMemberPo insertPo = as(dto,DriveSpaceMemberPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editDriveSpaceMember(EditDriveSpaceMemberDto dto) throws BizException {
        DriveSpaceMemberPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetDriveSpaceMemberDetailsVo getDriveSpaceMemberDetails(CommonIdDto dto) throws BizException {
        DriveSpaceMemberPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetDriveSpaceMemberDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeDriveSpaceMember(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}