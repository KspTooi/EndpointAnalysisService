package com.ksptool.bio.biz.drive.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.core.repository.OrgRepository;
import com.ksptool.bio.biz.core.repository.UserRepository;
import com.ksptool.bio.biz.drive.model.drivespace.DriveSpacePo;
import com.ksptool.bio.biz.drive.model.drivespace.dto.AddDriveSpaceDto;
import com.ksptool.bio.biz.drive.model.drivespace.dto.EditDriveSpaceDto;
import com.ksptool.bio.biz.drive.model.drivespace.dto.GetDriveSpaceListDto;
import com.ksptool.bio.biz.drive.model.drivespace.vo.GetDriveSpaceDetailsVo;
import com.ksptool.bio.biz.drive.model.drivespace.vo.GetDriveSpaceListVo;
import com.ksptool.bio.biz.drive.model.drivespacemember.DriveSpaceMemberPo;
import com.ksptool.bio.biz.drive.model.drivespacemember.vo.GetDriveSpaceMemberDetailsVo;
import com.ksptool.bio.biz.drive.repository.DriveSpaceMemberRepository;
import com.ksptool.bio.biz.drive.repository.DriveSpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        for(var member : dto.getMembers()){
            //如果用户在创建的时候已经把自己添加了,则强制设置为主管理员
            if(Objects.equals(member.getMemberId(), currentUserId)){
                findMainAdmin = true;
                member.setRole(0);
                break;
            }
        }

        //如果创建的时候没有把自己添加为成员,则将当前用户设置为主管理员
        if(!findMainAdmin){
            var maMember = new DriveSpaceMemberPo();
            maMember.setDriveSpaceId(insertPo.getId());
            maMember.setMemberKind(0);
            maMember.setMemberId(currentUserId);
            maMember.setRole(0);
            addDsMemberPos.add(maMember);
        }

        //保存云盘成员
        driveSpaceMemberRepository.saveAll(addDsMemberPos);
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
        for(var dsMemberPo : members){

            //渲染用户
            if(dsMemberPo.getMemberKind() == 0){

                for(var userPo : userPos){
                    if(Objects.equals(userPo.getId(), dsMemberPo.getMemberId())){
                        var userVo = new GetDriveSpaceMemberDetailsVo();
                        userVo.setId(dsMemberPo.getId());
                        userVo.setDriveSpaceId(dsMemberPo.getDriveSpaceId());
                        userVo.setMemberName(userPo.getUsername());
                        userVo.setMemberKind(dsMemberPo.getMemberKind());
                        userVo.setMemberId(dsMemberPo.getMemberId());
                        userVo.setRole(dsMemberPo.getRole());
                        userMembers.add(userVo);
                        break;
                    }
                }

            }

            //渲染部门
            if(dsMemberPo.getMemberKind() == 1){
                for(var deptPo : deptPos){
                    if(Objects.equals(deptPo.getId(), dsMemberPo.getMemberId())){
                        var deptVo = new GetDriveSpaceMemberDetailsVo();
                        deptVo.setId(dsMemberPo.getId());
                        deptVo.setDriveSpaceId(dsMemberPo.getDriveSpaceId());
                        deptVo.setMemberName(deptPo.getName());
                        deptVo.setMemberKind(dsMemberPo.getMemberKind());
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
        for(var member : userMembers){
            if(member.getRole() == 0){
                allMembers.add(0, member);
                break;
            }
        }

        //在主管理员后插入部门
        allMembers.addAll(deptMembers);

        //在部门后插入其他用户
        for(var member : userMembers){
            if(member.getRole() != 0){
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