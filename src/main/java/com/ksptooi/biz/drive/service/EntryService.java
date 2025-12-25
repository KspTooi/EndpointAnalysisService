package com.ksptooi.biz.drive.service;

import com.ksptooi.biz.core.model.attach.AttachPo;
import com.ksptooi.biz.core.repository.AttachRepository;
import com.ksptooi.biz.core.service.AttachService;
import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.biz.drive.model.EntryPo;
import com.ksptooi.biz.drive.model.dto.AddEntryDto;
import com.ksptooi.biz.drive.model.dto.CopyEntryDto;
import com.ksptooi.biz.drive.model.dto.GetEntryListDto;
import com.ksptooi.biz.drive.model.dto.MoveEntryDto;
import com.ksptooi.biz.drive.model.dto.RenameEntry;
import com.ksptooi.biz.drive.model.vo.CheckEntryMoveVo;
import com.ksptooi.biz.drive.model.vo.EntrySignVo;
import com.ksptooi.biz.drive.model.vo.GetDriveInfo;
import com.ksptooi.biz.drive.model.vo.GetEntryDetailsVo;
import com.ksptooi.biz.drive.model.vo.GetEntryListVo;
import com.ksptooi.biz.drive.repository.EntryRepository;
import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.ksptool.entities.Entities.as;

@Slf4j
@Service
public class EntryService {

    @Autowired
    private EntryRepository repository;

    @Autowired
    private AuthService authService;

    @Autowired
    private AttachRepository attachRepository;

    @Autowired
    private AttachService attachService;

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
            insertPo.setAttach(null);
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
            insertPo.setAttach(attachPo);
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
        var copyPos = copyEntry(entryPos);

        for (var entryPo : copyPos) {

            entryPo.setParent(parentPo);

            //判断目标目录中是否有同名文件
            if (repository.countByNameParentIdAndCompanyId(companyId, dto.getParentId(), entryPo.getName()) > 0) {
                entryPo.setName(entryPo.getName() + "-副本");
            }

        }

        //保存复制条目
        repository.saveAll(copyPos);
    }


    /**
     * 重命名条目
     *
     * @param dto 重命名条目
     */
    @Transactional(rollbackFor = Exception.class)
    public void renameEntry(RenameEntry dto) throws BizException, AuthException {

        var companyId = AuthService.requireCompanyId();

        EntryPo updatePo = repository.findById(dto.getEntryId())
                .orElseThrow(() -> new BizException("重命名失败,数据不存在."));

        if (!Objects.equals(updatePo.getCompanyId(), companyId)) {
            throw new BizException("重命名失败,无权限访问.");
        }

        Long parentId = null;

        if (updatePo.getParent() != null) {
            parentId = updatePo.getParent().getId();
        }

        if (repository.countByNameParentIdAndCompanyId(companyId, parentId, dto.getName()) > 0) {
            throw new BizException("此位置已存在同名文件,无法重命名.");
        }
        updatePo.setName(dto.getName());
        updatePo.setUpdaterId(AuthService.requireUserId());
        updatePo.setUpdateTime(LocalDateTime.now());
        repository.save(updatePo);
    }

    /**
     * 条目移动检测
     *
     * @param dto 条目移动检测
     */
    public CheckEntryMoveVo checkEntryMove(MoveEntryDto dto) throws BizException, AuthException {

        var ret = new CheckEntryMoveVo();
        ret.setCanMove(2); //0:可以移动 1:名称冲突 2:不可移动

        var companyId = AuthService.requireCompanyId();
        var targetEntryPo = repository.getByIdAndCompanyId(dto.getTargetId(), companyId);

        if (targetEntryPo == null) {
            ret.setCanMove(2);
            ret.setMessage("目标条目不存在或无权限访问");
            return ret;
        }

        if (targetEntryPo.getKind() != 1) {
            ret.setCanMove(2);
            ret.setMessage("目标条目必须是文件夹");
            return ret;
        }

        //检测是否有自身移动
        if (dto.getEntryIds().contains(dto.getTargetId())) {
            ret.setCanMove(2);
            ret.setMessage("不能将条目移动到自身内部");
            return ret;
        }

        Set<String> names = repository.getNamesByIds(dto.getEntryIds(), companyId);

        if (names.isEmpty()) {
            throw new BizException("要移动的条目不存在或无权限访问");
        }

        //查找出目标条目下的重复项
        var matchNames = repository.matchNamesByParentId(names, dto.getTargetId(), companyId);
        
        if (!matchNames.isEmpty()) {
            ret.setCanMove(1);
            ret.setMessage("目标条目下存在同名条目.");
            ret.setConflictNames(matchNames);
            return ret;
        }

        ret.setCanMove(0);
        ret.setMessage("可以移动");
        return ret;
    }

    /**
     * 移动条目
     *
     * @param dto 移动条目
     */
    @Transactional(rollbackFor = Exception.class)
    public void moveEntry(MoveEntryDto dto) throws BizException, AuthException {

        var companyId = AuthService.requireCompanyId();
        var targetEntryPo = repository.getByIdAndCompanyId(dto.getTargetId(), companyId);

        if(targetEntryPo == null){
            throw new BizException("目标条目不存在或无权限访问.");
        }

        if(targetEntryPo.getKind() != 1){
            throw new BizException("目标条目必须是文件夹.");
        }

        if(dto.getEntryIds().contains(dto.getTargetId())){
            throw new BizException("不能将条目移动到自身内部.");
        }
        
        //查找被移动条目
        var moveEntryPos = repository.getByIdAndCompanyIds(dto.getEntryIds(), companyId);

        if(moveEntryPos.isEmpty()){
            throw new BizException("要移动的条目不存在或无权限访问.");
        }

        var moveEntryNames = new HashSet<String>();
        for(var entryPo : moveEntryPos){
            moveEntryNames.add(entryPo.getName());
        }


        //覆盖模式移动
        if(dto.getMode() == 0){

            //删除目标下全部重名条目
            repository.removeByNameAndParentId(moveEntryNames, dto.getTargetId(), companyId);

            //将被移动条目挂载到目标条目
            for(var entryPo : moveEntryPos){
                entryPo.setParent(targetEntryPo);
            }

            //保存被移动条目
            repository.saveAll(moveEntryPos);
            return;
        }

        //跳过模式移动

        //查找目标条目下的重复项
        var matchNames = repository.matchNamesByParentId(moveEntryNames, dto.getTargetId(), companyId);

        for(var entryPo : moveEntryPos){

            //不移动重名条目
            if(matchNames.contains(entryPo.getName())){
                continue;
            }

            entryPo.setParent(targetEntryPo);
        }

        //保存被移动条目
        repository.saveAll(moveEntryPos);
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


    public List<EntryPo> copyEntry(List<EntryPo> entryPos) throws BizException, AuthException {

        var companyId = AuthService.requireCompanyId();
        var result = new ArrayList<EntryPo>();

        for (var entryPo : entryPos) {
            var newEntry = copyEntryRecursive(entryPo, null, companyId);
            result.add(newEntry);
        }

        return result;
    }

    /**
     * 递归复制条目
     *
     * @param sourcePo  源条目
     * @param parentPo  父级条目
     * @param companyId 公司ID
     * @return 新条目
     */
    private EntryPo copyEntryRecursive(EntryPo sourcePo, EntryPo parentPo, Long companyId) throws BizException {

        var newEntry = new EntryPo();
        newEntry.setId(IdWorker.nextId());
        newEntry.setCompanyId(companyId);
        newEntry.setParent(parentPo);
        newEntry.setName(sourcePo.getName());
        newEntry.setKind(sourcePo.getKind());
        newEntry.setAttach(sourcePo.getAttach());
        newEntry.setAttachSize(sourcePo.getAttachSize());
        newEntry.setAttachSuffix(sourcePo.getAttachSuffix());
        newEntry.setAttachStatus(sourcePo.getAttachStatus());

        if (sourcePo.getChildren() != null && !sourcePo.getChildren().isEmpty()) {
            var children = new ArrayList<EntryPo>();
            for (var child : sourcePo.getChildren()) {
                var newChild = copyEntryRecursive(child, newEntry, companyId);
                children.add(newChild);
            }
            newEntry.setChildren(new HashSet<>(children));
        }

        return newEntry;
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
            if (entryPo.getAttach() != null) {
                attachIds.add(entryPo.getAttach().getId());
            }
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
                if (entryPo.getAttach() != null && Objects.equals(item.getId(), entryPo.getAttach().getId())) {
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