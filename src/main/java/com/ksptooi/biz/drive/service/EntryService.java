package com.ksptooi.biz.drive.service;

import com.ksptooi.biz.core.model.attach.AttachPo;
import com.ksptooi.biz.core.repository.AttachRepository;
import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.biz.drive.model.EntryPo;
import com.ksptooi.biz.drive.model.dto.AddEntryDto;
import com.ksptooi.biz.drive.model.dto.CopyEntryDto;
import com.ksptooi.biz.drive.model.dto.EditEntryDto;
import com.ksptooi.biz.drive.model.dto.GetEntryListDto;
import com.ksptooi.biz.drive.model.vo.GetDriveInfo;
import com.ksptooi.biz.drive.model.vo.GetEntryDetailsVo;
import com.ksptooi.biz.drive.model.vo.GetEntryListVo;
import com.ksptooi.biz.drive.repository.EntryRepository;
import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import jakarta.security.auth.message.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Slf4j
@Service
public class EntryService {

    @Autowired
    private EntryRepository repository;

    @Autowired
    private AuthService authService;

    @Autowired
    private AttachRepository attachRepository;

    /**
     * 获取云盘信息
     *
     * @return 云盘信息
     */
    public GetDriveInfo getDriveInfo() throws AuthException {
        
        var companyId = AuthService.requireCompanyId();
        var ret = repository.getDriveInfo(companyId);

        //总容量为2TB
        ret.setTotalCapacity(2L * 1024 * 1024 * 1024 * 1024);
        return ret;
    }

    /**
     * 查询条目列表
     *
     * @param dto 查询条件
     * @return 条目列表
     */
    public PageResult<GetEntryListVo> getEntryList(GetEntryListDto dto) throws AuthException {

        Long companyId = AuthService.requireCompanyId();

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

            if (!Objects.equals(attachPo.getCreatorId(), userId)) {
                throw new BizException("指定的文件附件不属于当前用户,无法新增驱动器条目.");
            }

            insertPo.setKind(0);
            insertPo.setAttachId(attachPo.getId());
            insertPo.setAttachSize(attachPo.getTotalSize());
            insertPo.setAttachSuffix(attachPo.getSuffix());
            insertPo.setAttachStatus(attachPo.getStatus());
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
     * 复制条目
     *
     * @param dto 复制条目
     */
    @Transactional(rollbackFor = Exception.class)
    public void copyEntry(CopyEntryDto dto) throws BizException, AuthException {

        var companyId = AuthService.requireCompanyId();

        //查询要复制的条目
        var entryPos = repository.getByIdAndCompanyIds(dto.getEntryIds(), companyId);

        if (entryPos.isEmpty()) {
            throw new BizException("要复制的条目不存在或无权限访问");
        }

        //查找父级条目
        EntryPo parentPo = null;

        if (dto.getParentId() != null) {
            parentPo = repository.getByIdAndCompanyId(dto.getParentId(), companyId);

            if (parentPo == null) {
                throw new BizException("指定的父级条目不存在或无权限访问");
            }

            if (parentPo.getKind() != 1) {
                throw new BizException("指定的挂载父级目录必须是文件夹。");
            }

        }

        //组装复制数据
        var copyPos = new ArrayList<EntryPo>();

        for (var entryPo : entryPos) {
            var newEntryPo = as(entryPo, EntryPo.class);
            newEntryPo.setId(IdWorker.nextId());
            newEntryPo.setCompanyId(companyId);
            newEntryPo.setParent(parentPo);
            copyPos.add(newEntryPo);

            //判断目标目录中是否有同名文件
            if (repository.countByNameParentIdAndCompanyId(companyId, dto.getParentId(), newEntryPo.getName()) > 0) {
                newEntryPo.setName(newEntryPo.getName() + "-副本");
            }

        }

        //保存复制条目
        repository.saveAll(copyPos);
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


    /**
     * 定时任务 5秒执行一次,检查entrySyncList中的数据,如果存在数据,则执行同步操作
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional(rollbackFor = Exception.class)
    public void syncEntry() {

        //查询全部需要同步的云盘条目
        var entryPos = repository.getNeedSyncEntryList(500);

        if (entryPos.isEmpty()) {
            return;
        }

        log.info("正在检查:{} 个云盘条目", entryPos.size());

        var attachIds = new ArrayList<Long>();

        //获取所有需要同步的文件附件ID
        for (EntryPo entryPo : entryPos) {
            attachIds.add(entryPo.getAttachId());
        }

        //获取所有需要同步的文件附件
        var attachList = attachRepository.findAllById(attachIds);

        //校验失败或错误的条目IDS
        var errorEntries = new ArrayList<Long>();

        //校验成功的条目
        var successEntries = new ArrayList<Long>();

        for (var entryPo : entryPos) {

            AttachPo attach = null;

            for (var item : attachList) {
                if (Objects.equals(item.getId(), entryPo.getAttachId())) {
                    attach = item;
                    break;
                }
            }

            //文件附件不存在
            if (attach == null) {
                errorEntries.add(entryPo.getId());
                continue;
            }

            //文件附件校验通过
            if (attach.getStatus() == 3) {
                successEntries.add(entryPo.getId());
                continue;
            }

            //文件附件校验中
            if (attach.getStatus() == 2) {
                continue;
            }

            //其他异常状态
            errorEntries.add(entryPo.getId());
        }

        //更新云盘条目状态
        if (!errorEntries.isEmpty()) {
            var count = repository.removeEntryByEntryIds(errorEntries);
            if (count > 0) {
                log.info("已有 {} 个条云盘条目因文件附件不存在或校验失败被删除", errorEntries.size());
            }
        }
        if (!successEntries.isEmpty()) {
            var count = repository.updateEntryAttachStatusByEntryIds(successEntries, 3);
            if (count > 0) {
                log.info("已有 {} 个条云盘条目校验通过", successEntries.size());
            }
        }
    }

}