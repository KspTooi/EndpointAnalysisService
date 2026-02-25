package com.ksptool.bio.biz.drive.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.drive.model.drivespace.DriveSpacePo;
import com.ksptool.bio.biz.drive.model.drivespace.dto.AddDriveSpaceDto;
import com.ksptool.bio.biz.drive.model.drivespace.dto.EditDriveSpaceDto;
import com.ksptool.bio.biz.drive.model.drivespace.dto.GetDriveSpaceListDto;
import com.ksptool.bio.biz.drive.model.drivespace.vo.GetDriveSpaceDetailsVo;
import com.ksptool.bio.biz.drive.model.drivespace.vo.GetDriveSpaceListVo;
import com.ksptool.bio.biz.drive.repository.DriveSpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class DriveSpaceService {

    @Autowired
    private DriveSpaceRepository repository;

    /**
     * 查询云盘空间列表
     *
     * @param dto 查询条件
     * @return 云盘空间列表
     */
    public PageResult<GetDriveSpaceListVo> getDriveSpaceList(GetDriveSpaceListDto dto) {
        DriveSpacePo query = new DriveSpacePo();
        assign(dto, query);

        Page<DriveSpacePo> page = repository.getDriveSpaceList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetDriveSpaceListVo> vos = as(page.getContent(), GetDriveSpaceListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增云盘空间
     *
     * @param dto 新增条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void addDriveSpace(AddDriveSpaceDto dto) {
        DriveSpacePo insertPo = as(dto, DriveSpacePo.class);
        insertPo.setQuotaUsed(0L);
        repository.save(insertPo);
    }

    /**
     * 编辑云盘空间
     *
     * @param dto 编辑条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editDriveSpace(EditDriveSpaceDto dto) throws BizException {
        DriveSpacePo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询云盘空间详情
     *
     * @param dto 查询条件
     * @return 云盘空间详情
     * @throws BizException 业务异常
     */
    public GetDriveSpaceDetailsVo getDriveSpaceDetails(CommonIdDto dto) throws BizException {
        DriveSpacePo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetDriveSpaceDetailsVo.class);
    }

    /**
     * 删除云盘空间
     *
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeDriveSpace(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}