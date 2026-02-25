package com.ksptool.bio.biz.drive.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.core.model.notice.dto.AddNoticeDto;
import com.ksptool.bio.biz.core.repository.OrgRepository;
import com.ksptool.bio.biz.core.repository.UserRepository;
import com.ksptool.bio.biz.core.service.NoticeService;
import com.ksptool.bio.biz.drive.model.drivespace.DriveSpacePo;
import com.ksptool.bio.biz.drive.model.drivespace.dto.AddDriveSpaceDto;
import com.ksptool.bio.biz.drive.model.drivespace.dto.EditDriveSpaceDto;
import com.ksptool.bio.biz.drive.model.drivespace.dto.EditDriveSpaceMembersDto;
import com.ksptool.bio.biz.drive.model.drivespace.dto.GetDriveSpaceListDto;
import com.ksptool.bio.biz.drive.model.drivespace.vo.GetDriveSpaceDetailsVo;
import com.ksptool.bio.biz.drive.model.drivespace.vo.GetDriveSpaceListVo;
import com.ksptool.bio.biz.drive.model.drivespacemember.DriveSpaceMemberPo;
import com.ksptool.bio.biz.drive.model.drivespacemember.vo.GetDriveSpaceMemberDetailsVo;
import com.ksptool.bio.biz.drive.repository.DriveSpaceMemberRepository;
import com.ksptool.bio.biz.drive.repository.DriveSpaceRepository;
import com.ksptool.text.PreparedPrompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ksptool.bio.biz.auth.service.SessionService.session;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class DriveSpaceService {

    @Autowired
    private DriveSpaceRepository repository;

    @Autowired
    private DriveSpaceMemberRepository driveSpaceMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private NoticeService noticeService;

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
    public void addDriveSpace(AddDriveSpaceDto dto) throws Exception {
        DriveSpacePo insertPo = as(dto, DriveSpacePo.class);
        insertPo.setQuotaUsed(0L);

        //先保存云盘空间 这样才可以获取到ID
        insertPo = repository.save(insertPo);

        //处理云盘成员列表
        var addDsMemberPos = new ArrayList<DriveSpaceMemberPo>();

        for (var member : dto.getMembers()) {

            //处理用户
            if (member.getMemberKind() == 0) {
                userRepository.findById(member.getMemberId()).orElseThrow(() -> new BizException("用户 [ " + member.getMemberId() + " ] 不存在,无法添加到云盘空间!"));
                var dsmPo = new DriveSpaceMemberPo();
                dsmPo.setDriveSpaceId(insertPo.getId());
                dsmPo.setMemberId(member.getMemberId());
                dsmPo.setMemberKind(member.getMemberKind());
                dsmPo.setRole(member.getRole());
                addDsMemberPos.add(dsmPo);
                continue;
            }

            //处理部门
            if (member.getMemberKind() == 1) {
                orgRepository.findById(member.getMemberId()).orElseThrow(() -> new BizException("部门 [ " + member.getMemberId() + " ] 不存在,无法添加到云盘空间!"));
                var dsmPo = new DriveSpaceMemberPo();
                dsmPo.setDriveSpaceId(insertPo.getId());
                dsmPo.setMemberId(member.getMemberId());
                dsmPo.setMemberKind(member.getMemberKind());
                dsmPo.setRole(member.getRole());
                addDsMemberPos.add(dsmPo);
                continue;
            }

            throw new BizException("无法处理新增请求,成员类型 [" + member.getMemberKind() + "] 不支持.");
        }

        //处理主管理员
        var currentUserId = session().getUserId();

        var findMainAdmin = false;

        for (var member : dto.getMembers()) {
            //如果用户在创建的时候已经把自己添加了,则强制设置为主管理员
            if (Objects.equals(member.getMemberId(), currentUserId)) {
                findMainAdmin = true;
                member.setRole(0);
                break;
            }
        }

        //如果创建的时候没有把自己添加为成员,则将当前用户设置为主管理员
        if (!findMainAdmin) {
            var maMember = new DriveSpaceMemberPo();
            maMember.setDriveSpaceId(insertPo.getId());
            maMember.setMemberKind(0);
            maMember.setMemberId(currentUserId);
            maMember.setRole(0);
            addDsMemberPos.add(maMember);
        }

        //保存云盘成员
        driveSpaceMemberRepository.saveAll(addDsMemberPos);

        //发送系统消息 搜集要接收消息的用户ID列表和部门ID列表 按云盘空间的Role分组 分组后每个组单独发送消息
        var targetUserIdsMap = new HashMap<Integer, ArrayList<Long>>();
        var targetDeptIdsMap = new HashMap<Integer, ArrayList<Long>>();

        for (var member : addDsMemberPos) {
            if (member.getMemberKind() == 0) {
                targetUserIdsMap.computeIfAbsent(member.getRole(), k -> new ArrayList<>()).add(member.getMemberId());
            }
            if (member.getMemberKind() == 1) {
                targetDeptIdsMap.computeIfAbsent(member.getRole(), k -> new ArrayList<>()).add(member.getMemberId());
            }
        }

        //先准备好要发送的消息
        var and = new AddNoticeDto();
        and.setTitle("云盘空间邀请 : " + insertPo.getName());
        and.setKind(2);

        var content = PreparedPrompt.prepare("""
                您已被 [#{inviterName}] 邀请加入云盘空间 [#{spaceName}] ,您在空间中的角色为 [#{role}] .
                """);
        content.setParameter("inviterName", session().getUsername());
        content.setParameter("spaceName", insertPo.getName());
        content.setParameter("role", "未知角色");

        and.setPriority(2);
        and.setCategory("团队云盘");
        and.setTargetKind(1);

        var roleName = "未知角色";

        //遍历分组好的用户ID列表和部门ID列表 单独发送消息
        for (var role : targetUserIdsMap.keySet()) {

            //如果用户ID列表为空 则跳过
            if (targetUserIdsMap.get(role).isEmpty()) {
                continue;
            }

            roleName = role == 0 ? "主管理员" : role == 1 ? "行政管理员" : role == 2 ? "编辑者" : "查看者";
            content.setParameter("role", roleName);
            and.setTargetKind(2);
            and.setContent(content.execute());
            and.setTargetIds(targetUserIdsMap.get(role));
            noticeService.addNotice(and);
        }
        for (var role : targetDeptIdsMap.keySet()) {

            //如果部门ID列表为空 则跳过
            if (targetDeptIdsMap.get(role).isEmpty()) {
                continue;
            }

            roleName = role == 0 ? "主管理员" : role == 1 ? "行政管理员" : role == 2 ? "编辑者" : "查看者";
            content.setParameter("role", roleName);
            and.setTargetKind(1);
            and.setContent(content.execute());
            and.setTargetIds(targetDeptIdsMap.get(role));
            noticeService.addNotice(and);
        }

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
     * 编辑云盘空间成员
     *
     * @param dto 编辑条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editDriveSpaceMembers(EditDriveSpaceMembersDto dto) throws Exception {

        //查云盘空间
        var driveSpace = repository.findById(dto.getDriveSpaceId())
                .orElseThrow(() -> new BizException("云盘空间 [ " + dto.getDriveSpaceId() + " ] 不存在,无法编辑云盘空间成员."));

        //先查当前人在这个空间里面是不是主管理员
        var currentUserId = session().getUserId();

        var currentUserDsm = driveSpaceMemberRepository.getByDriveSpaceIdAndMemberId(dto.getDriveSpaceId(), currentUserId);

        if (currentUserDsm == null) {
            throw new BizException("当前用户不是云盘空间成员,无法编辑云盘空间成员.");
        }

        if (currentUserDsm.getRole() != 0) {
            throw new BizException("当前用户不是主管理员,无法编辑云盘空间成员.");
        }

        //先准备好要发送的消息
        var and = new AddNoticeDto();
        and.setTitle("云盘空间 [ " + driveSpace.getName() + " ] 成员变更");
        and.setKind(1);
        and.setPriority(2);
        and.setCategory("团队云盘");
        and.setTargetKind(2);


        //处理加/改成员
        if (dto.getAction() == 0) {

            //先查是否存在
            var existingDsm = driveSpaceMemberRepository.getByDriveSpaceIdAndMemberId(dto.getDriveSpaceId(), dto.getMemberId());

            //不能把成员加/改成主管理员
            if (dto.getRole() == 0) {
                throw new BizException("不能把成员加/改成主管理员.");
            }

            //存在就改(只能改角色,不能改成员类型和成员ID)
            if (existingDsm != null) {
                existingDsm.setRole(dto.getRole());
                driveSpaceMemberRepository.save(existingDsm);

                //给成员发消息
                and.setTitle("您在云盘空间 [ " + driveSpace.getName() + " ] 中的角色已被修改");
                and.setKind(2);
                var content = PreparedPrompt.prepare("""
                        您在云盘空间 [#{spaceName}] 中的角色已被 [#{operatorName}] 修改为 [#{role}] .
                        """);
                content.setParameter("operatorName", session().getUsername());
                content.setParameter("spaceName", driveSpace.getName());
                content.setParameter("role", existingDsm.getRole() == 0 ? "主管理员" : existingDsm.getRole() == 1 ? "行政管理员" : existingDsm.getRole() == 2 ? "编辑者" : "查看者");
                and.setContent(content.execute());

                if (existingDsm.getMemberKind() == 0) {
                    and.setTargetKind(2); //发给用户
                    and.setTargetIds(Collections.singletonList(existingDsm.getMemberId()));
                }
                if (existingDsm.getMemberKind() == 1) {
                    and.setTargetKind(1); //发给部门
                    and.setTargetIds(Collections.singletonList(existingDsm.getMemberId()));
                }
                noticeService.addNotice(and);
                return;
            }

            //不存在就加 先处理加成员
            if (dto.getMemberKind() == 0) {
                userRepository.findById(dto.getMemberId()).orElseThrow(() -> new BizException("用户 [ " + dto.getMemberId() + " ] 不存在,无法添加到云盘空间!"));
                var newDsmPo = new DriveSpaceMemberPo();
                newDsmPo.setDriveSpaceId(dto.getDriveSpaceId());
                newDsmPo.setMemberId(dto.getMemberId());
                newDsmPo.setMemberKind(dto.getMemberKind());
                newDsmPo.setRole(dto.getRole());
                driveSpaceMemberRepository.save(newDsmPo);

                //给成员发消息
                and.setTitle("您已被添加到云盘空间 [ " + driveSpace.getName() + " ]");
                and.setKind(2);
                var content = PreparedPrompt.prepare("""
                        您已被 [#{operatorName}] 添加到云盘空间 [#{spaceName}] ,您在空间中的角色为 [#{role}] .
                        """);
                content.setParameter("operatorName", session().getUsername());
                content.setParameter("spaceName", driveSpace.getName());
                content.setParameter("role", dto.getRole() == 0 ? "主管理员" : dto.getRole() == 1 ? "行政管理员" : dto.getRole() == 2 ? "编辑者" : "查看者");
                and.setContent(content.execute());
                and.setTargetKind(2); //发给用户
                and.setTargetIds(Collections.singletonList(newDsmPo.getMemberId()));
                noticeService.addNotice(and);
                return;
            }

            //处理加部门
            if (dto.getMemberKind() == 1) {
                orgRepository.findById(dto.getMemberId()).orElseThrow(() -> new BizException("部门 [ " + dto.getMemberId() + " ] 不存在,无法添加到云盘空间!"));
                var newDsmPo = new DriveSpaceMemberPo();
                newDsmPo.setDriveSpaceId(dto.getDriveSpaceId());
                newDsmPo.setMemberId(dto.getMemberId());
                newDsmPo.setMemberKind(dto.getMemberKind());
                newDsmPo.setRole(dto.getRole());
                driveSpaceMemberRepository.save(newDsmPo);

                //给部门发消息
                and.setTitle("您已被添加到云盘空间 [ " + driveSpace.getName() + " ]");
                and.setKind(2);
                var content = PreparedPrompt.prepare("""
                        您已被 [#{operatorName}] 添加到云盘空间 [#{spaceName}] ,您在空间中的角色为 [#{role}] .
                        """);
                content.setParameter("operatorName", session().getUsername());
                content.setParameter("spaceName", driveSpace.getName());
                content.setParameter("role", dto.getRole() == 0 ? "主管理员" : dto.getRole() == 1 ? "行政管理员" : dto.getRole() == 2 ? "编辑者" : "查看者");
                and.setContent(content.execute());
                and.setTargetKind(1); //发给部门
                and.setTargetIds(Collections.singletonList(newDsmPo.getMemberId()));
                noticeService.addNotice(and);
                return;
            }

            throw new BizException("无法处理新增请求,成员类型 [" + dto.getMemberKind() + "] 不支持.");
        }

        //处理删成员 先查要这个空间里要被删的成员
        var dsmToDelete = driveSpaceMemberRepository.getByDriveSpaceIdAndMemberId(dto.getDriveSpaceId(), dto.getMemberId());

        if (dsmToDelete == null) {
            throw new BizException("要删除的成员不存在,无法删除.");
        }

        //不能删主管理员
        if (dsmToDelete.getRole() == 0) {
            throw new BizException("不能删除主管理员.");
        }

        //删除成员
        driveSpaceMemberRepository.delete(dsmToDelete);

        //给成员发消息
        and.setTitle("您已被从云盘空间 [ " + driveSpace.getName() + " ] 中移除");
        and.setKind(1);
        var content = PreparedPrompt.prepare("""
                您已被 [#{operatorName}] 从云盘空间 [#{spaceName}] 中移除.
                """);
        content.setParameter("operatorName", session().getUsername());
        content.setParameter("spaceName", driveSpace.getName());
        and.setContent(content.execute());
        and.setTargetKind(dsmToDelete.getMemberKind() == 0 ? 2 : 1); //发给用户或部门 1:部门 2:用户
        and.setTargetIds(Collections.singletonList(dsmToDelete.getMemberId()));
        noticeService.addNotice(and);
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

        //查询云盘空间下的成员
        var members = driveSpaceMemberRepository.getByDriveSpaceId(po.getId());

        //分开处理部门和用户
        var deptMembers = new ArrayList<GetDriveSpaceMemberDetailsVo>();
        var userMembers = new ArrayList<GetDriveSpaceMemberDetailsVo>();

        var deptIds = new ArrayList<Long>();
        var userIds = new ArrayList<Long>();

        //先分别搜集部门和用户ID
        for (var member : members) {
            if (member.getMemberKind() == 1) {
                deptIds.add(member.getMemberId());
            }
            if (member.getMemberKind() == 0) {
                userIds.add(member.getMemberId());
            }
        }

        //批量查询部门和用户
        var deptPos = orgRepository.findAllById(deptIds);
        var userPos = userRepository.findAllById(userIds);

        //渲染查询结果为VO
        for (var dsMemberPo : members) {

            //渲染用户
            if (dsMemberPo.getMemberKind() == 0) {

                for (var userPo : userPos) {
                    if (Objects.equals(userPo.getId(), dsMemberPo.getMemberId())) {
                        var userVo = new GetDriveSpaceMemberDetailsVo();
                        userVo.setId(dsMemberPo.getId());
                        userVo.setDriveSpaceId(dsMemberPo.getDriveSpaceId());
                        userVo.setMemberName(userPo.getUsername());
                        userVo.setMemberKind(0);
                        userVo.setMemberId(dsMemberPo.getMemberId());
                        userVo.setRole(dsMemberPo.getRole());
                        userMembers.add(userVo);
                        break;
                    }
                }

            }

            //渲染部门
            if (dsMemberPo.getMemberKind() == 1) {
                for (var deptPo : deptPos) {
                    if (Objects.equals(deptPo.getId(), dsMemberPo.getMemberId())) {
                        var deptVo = new GetDriveSpaceMemberDetailsVo();
                        deptVo.setId(dsMemberPo.getId());
                        deptVo.setDriveSpaceId(dsMemberPo.getDriveSpaceId());
                        deptVo.setMemberName(deptPo.getName());
                        deptVo.setMemberKind(1);
                        deptVo.setMemberId(dsMemberPo.getMemberId());
                        deptVo.setRole(dsMemberPo.getRole());
                        deptMembers.add(deptVo);
                        break;
                    }
                }
            }

        }

        //合并部门和用户
        var allMembers = new ArrayList<GetDriveSpaceMemberDetailsVo>();

        //在第一条插入主管理员
        for (var member : userMembers) {
            if (member.getRole() == 0) {
                allMembers.add(0, member);
                break;
            }
        }

        //在主管理员后插入部门
        allMembers.addAll(deptMembers);

        //在部门后插入其他用户
        for (var member : userMembers) {
            if (member.getRole() != 0) {
                allMembers.add(member);
            }
        }

        //渲染VO
        var vo = new GetDriveSpaceDetailsVo();
        vo.setId(po.getId());
        vo.setName(po.getName());
        vo.setRemark(po.getRemark());
        vo.setQuotaLimit(po.getQuotaLimit());
        vo.setStatus(po.getStatus());
        vo.setMembers(allMembers);
        return vo;
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