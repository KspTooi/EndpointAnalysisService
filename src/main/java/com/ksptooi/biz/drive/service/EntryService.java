package com.ksptooi.biz.drive.service;

import com.ksptooi.biz.core.repository.AttachRepository;
import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.biz.drive.model.dto.AddEntryDto;
import com.ksptooi.biz.drive.model.dto.EditEntryDto;
import com.ksptooi.biz.drive.model.dto.GetEntryListDto;
import com.ksptooi.biz.drive.model.po.EntryPo;
import com.ksptooi.biz.drive.model.vo.GetEntryDetailsVo;
import com.ksptooi.biz.drive.model.vo.GetEntryListVo;
import com.ksptooi.biz.drive.repository.EntryRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class EntryService {

    @Autowired
    private EntryRepository repository;

    @Autowired
    private AuthService authService;

    @Autowired
    private AttachRepository attachRepository;

    /**
     * 查询条目列表
     *
     * @param dto 查询条件
     * @return 条目列表
     */
    public PageResult<GetEntryListVo> getEntryList(GetEntryListDto dto) throws AuthException {

        Long companyId = AuthService.requireCompanyId();

        EntryPo query = new EntryPo();
        assign(dto, query);

        Page<EntryPo> page = repository.getEntryList(dto.getParentId(), dto.getKeyword(), companyId, dto.pageRequest());

        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetEntryListVo> vos = as(page.getContent(), GetEntryListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增条目
     *
     * @param dto 新增条目
     */
    @Transactional(rollbackFor = Exception.class)
    public void addEntry(AddEntryDto dto) throws BizException, AuthException {

        var companyId = AuthService.requireCompanyId();
        var userId = AuthService.requireUserId();

        if (repository.countByName(companyId, dto.getParentId(), dto.getName()) > 0) {
            throw new BizException("指定的条目与已存在的条目名称重复");
        }

        //组装新增数据
        EntryPo insertPo = as(dto, EntryPo.class);
        insertPo.setCompanyId(companyId);

        //文件夹
        if (dto.getKind() == 1) {
            insertPo.setKind(1);
            insertPo.setAttachId(null);
            insertPo.setAttachSize(0L);
            insertPo.setAttachSuffix(null);
        }

        //文件
        if (dto.getKind() == 0) {
            var attachPo = attachRepository.findById(dto.getAttachId()).orElseThrow(() -> new BizException("指定的文件附件不存在."));

            if (attachPo.getStatus() != 3) {
                throw new BizException("指定的文件附件尚未校验完成,无法新增驱动器条目.");
            }

            if (attachPo.getCreatorId() != userId) {
                throw new BizException("指定的文件附件不属于当前用户,无法新增驱动器条目.");
            }

            insertPo.setKind(0);
            insertPo.setAttachId(attachPo.getId());
            insertPo.setAttachSize(attachPo.getTotalSize());
            insertPo.setAttachSuffix(attachPo.getSuffix());
        }

        //挂载到父级条目
        if (dto.getParentId() != null) {
            var parentPo = repository.findById(dto.getParentId()).orElseThrow(() -> new BizException("指定的父级条目不存在."));

            if (parentPo.getKind() != 1) {
                throw new BizException("指定的挂载父级目录必须是文件夹。");
            }

            insertPo.setParent(parentPo);
            parentPo.getChildren().add(insertPo);
        }

        //保存条目
        repository.save(insertPo);
    }

    /**
     * 编辑条目
     *
     * @param dto 编辑条目
     */
    @Transactional(rollbackFor = Exception.class)
    public void editEntry(EditEntryDto dto) throws BizException {
        EntryPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询条目详情
     *
     * @param dto 查询条件
     * @return 条目详情
     */
    public GetEntryDetailsVo getEntryDetails(CommonIdDto dto) throws BizException {
        EntryPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetEntryDetailsVo.class);
    }

    /**
     * 删除条目
     *
     * @param dto 删除条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeEntry(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

}