package com.ksptooi.biz.core.service;


import com.ksptooi.biz.core.model.org.OrgPo;
import com.ksptooi.biz.core.model.org.dto.AddOrgDto;
import com.ksptooi.biz.core.model.org.dto.EditOrgDto;
import com.ksptooi.biz.core.model.org.dto.GetOrgListDto;
import com.ksptooi.biz.core.model.org.dto.GetOrgTreeDto;
import com.ksptooi.biz.core.model.org.vo.GetOrgDetailsVo;
import com.ksptooi.biz.core.model.org.vo.GetOrgListVo;
import com.ksptooi.biz.core.model.org.vo.GetOrgTreeVo;
import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.core.repository.OrgRepository;
import com.ksptooi.biz.core.repository.UserRepository;
import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class OrgService {

    @Autowired
    private OrgRepository repository;

    @Autowired
    private UserRepository userRepository;


    /**
     * 查询组织架构树
     *
     * @param dto
     * @return
     */
    public List<GetOrgTreeVo> getOrgTree(GetOrgTreeDto dto) {

        //全量查询组织 按排序排序
        List<OrgPo> pos = repository.findAll(Sort.by(Sort.Direction.ASC, "seq"));

        List<GetOrgTreeVo> flatTreeVos = as(pos, GetOrgTreeVo.class);
        List<GetOrgTreeVo> treeVos = new ArrayList<>();

        if (flatTreeVos.isEmpty()) {
            return treeVos;
        }

        Map<Long, GetOrgTreeVo> voMap = new HashMap<>();
        for (GetOrgTreeVo vo : flatTreeVos) {
            vo.setChildren(new ArrayList<>());
            voMap.put(vo.getId(), vo);
        }

        for (GetOrgTreeVo vo : flatTreeVos) {
            if (vo.getParentId() == null) {
                treeVos.add(vo);
                continue;
            }

            GetOrgTreeVo parentVo = voMap.get(vo.getParentId());
            if (parentVo == null) {
                continue;
            }

            if (parentVo.getChildren() == null) {
                parentVo.setChildren(new ArrayList<>());
            }
            parentVo.getChildren().add(vo);
        }

        return treeVos;
    }

    /**
     * 获取组织机构列表
     *
     * @param dto
     * @return
     */
    public PageResult<GetOrgListVo> getOrgList(GetOrgListDto dto) {
        OrgPo query = new OrgPo();
        assign(dto, query);

        Page<OrgPo> page = repository.getOrgList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetOrgListVo> vos = as(page.getContent(), GetOrgListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增组织机构
     *
     * @param dto
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOrg(AddOrgDto dto) throws BizException {

        OrgPo parentPo = null;

        if (dto.getParentId() != null) {

            parentPo = repository.findById(dto.getParentId())
                    .orElseThrow(() -> new BizException("无法处理新增请求,父级组织不存在."));

            //父级组织下只能添加部门
            if (dto.getKind() != 0) {
                throw new BizException("无法处理新增请求,父级组织下只能添加部门.");
            }

        }

        //处理企业新增 0:部门 1:企业
        if (dto.getKind() == 1) {

            //校验企业名称是否唯一
            if (repository.countRootByName(dto.getName()) > 0) {
                throw new BizException("无法处理新增请求,企业名称 [" + dto.getName() + "] 已存在.");
            }

            var id = IdWorker.nextId();
            OrgPo addPo = as(dto, OrgPo.class);
            addPo.setId(id);
            addPo.setRootId(id);
            addPo.setParentId(null);
            repository.saveAndFlush(addPo);
            return;
        }

        //处理部门新增
        if(parentPo == null){
            throw new BizException("无法处理新增请求,部门必须有上级组织.");
        }

        //校验部门名称是否唯一
        if (repository.countDeptByNameAndParentId(dto.getName(), parentPo.getId()) > 0) {
            throw new BizException("无法处理新增请求,上级组织 [" + parentPo.getName() + "] 下已有同名部门 [" + dto.getName() + "].");
        }

        var addPo = as(dto, OrgPo.class);
        addPo.setRootId(parentPo.getRootId());
        addPo.setParentId(parentPo.getId());


        //如果有主管则需要处理主管(查询该公司下的主管)
        if (dto.getPrincipalId() != null) {

            UserPo userPo = userRepository.findById(dto.getPrincipalId())
                    .orElseThrow(() -> new BizException("无法处理新增请求,主管不存在."));

            if (userPo == null) {
                throw new BizException("无法处理新增请求,主管不存在或不属于该组织!");
            }

            addPo.setPrincipalId(userPo.getId());
            addPo.setPrincipalName(userPo.getUsername());
        }

        var _parent = parentPo;
        var pathIds = new ArrayList<String>();
        pathIds.add(_parent.getId() + "");

        //处理组织路径ID
        while (_parent.getParentId() != null) {

            _parent = repository.findById(_parent.getParentId())
                    .orElseThrow(() -> new BizException("无法处理新增请求,父级组织不存在,导致组织路径ID处理失败."));

            pathIds.add(_parent.getId() + "");
        }

        addPo.setOrgPathIds(String.join(",", pathIds));
        repository.save(addPo);
    }

    /**
     * 编辑组织机构
     *
     * @param dto
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void editOrg(EditOrgDto dto) throws BizException {

        OrgPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));


        OrgPo parentPo = null;

        if (dto.getParentId() != null) {
            parentPo = repository.findById(dto.getParentId())
                    .orElseThrow(() -> new BizException("无法处理编辑请求,父级组织不存在."));

            //父级组织不能是自身
            if (Objects.equals(updatePo.getId(), parentPo.getId())) {
                throw new BizException("无法处理编辑请求,父级组织不能是自身.");
            }

            //父级组织下只能添加部门
            if (updatePo.getKind() != 0) {
                throw new BizException("无法处理编辑请求,父级组织下只能添加部门.");
            }

            //父级组织的父级不能修改为自身下级

        }

        //0:部门 1:企业
        if (updatePo.getKind() == 1) {

            //校验企业名称是否唯一
            if (repository.countRootByNameExcludeId(dto.getName(), updatePo.getId()) > 0) {
                throw new BizException("无法处理编辑请求,企业名称 [" + dto.getName() + "] 已存在.");
            }

            if (dto.getPrincipalId() != null) {
                throw new BizException("无法处理编辑请求,企业不允许填主管ID.");
            }

            assign(dto, updatePo);
            updatePo.setParentId(null);
            updatePo.setPrincipalId(null);
            updatePo.setPrincipalName(null);
        }

        //处理部门
        if (updatePo.getKind() == 0) {

            if (parentPo == null) {
                throw new BizException("无法处理编辑请求,部门必须有父级组织.");
            }

            //校验部门名称是否唯一
            if (repository.countDeptByNameAndParentIdExcludeId(dto.getName(), parentPo.getId(), updatePo.getId()) > 0) {
                throw new BizException("无法处理编辑请求,上级组织 [" + parentPo.getName() + "] 下已有同名部门 [" + dto.getName() + "].");
            }

            //部门无法跨组织移动
            if (!Objects.equals(updatePo.getRootId(), parentPo.getRootId())) {
                throw new BizException("无法处理编辑请求,部门无法跨组织移动.");
            }

            assign(dto, updatePo);

            //处理主管
            if (dto.getPrincipalId() != null) {
                UserPo userPo = userRepository.findById(dto.getPrincipalId())
                        .orElseThrow(() -> new BizException("无法处理编辑请求,主管不存在."));
                if (userPo == null) {
                    throw new BizException("无法处理编辑请求,主管不存在或不属于该组织!");
                }
                updatePo.setPrincipalId(userPo.getId());
                updatePo.setPrincipalName(userPo.getUsername());
            }

            //处理组织路径ID
            var _parent = parentPo;
            var pathIds = new ArrayList<String>();
            pathIds.add(_parent.getId() + "");

            while (_parent.getParentId() != null) {

                _parent = repository.findById(_parent.getParentId())
                        .orElseThrow(() -> new BizException("无法处理编辑请求,父级组织不存在,导致组织路径ID处理失败."));

                pathIds.add(_parent.getId() + "");
            }
            updatePo.setOrgPathIds(String.join(",", pathIds));

        }

        repository.save(updatePo);
    }

    /**
     * 获取组织机构详情
     *
     * @param dto
     * @return
     * @throws BizException
     */
    public GetOrgDetailsVo getOrgDetails(CommonIdDto dto) throws BizException {
        OrgPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetOrgDetailsVo.class);
    }

    /**
     * 删除组织机构
     *
     * @param dto
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeOrg(CommonIdDto dto) throws BizException {

        var ids = dto.toIds();

        //根据IDS查询
        List<OrgPo> pos = repository.findAllById(ids);

        for (var po : pos) {

            //查询是否有子组织
            if (repository.countByParentId(po.getId()) > 0) {
                throw new BizException("无法处理删除请求,该组织下有子组织,不能删除.");
            }

            //查询是否还有人员
            if (repository.countUserByDeptIds(Collections.singletonList(po.getId())) > 0) {
                throw new BizException("无法处理删除请求,该部门下有用户,请先移除用户后再删除.");
            }

            repository.delete(po);
        }

    }

}