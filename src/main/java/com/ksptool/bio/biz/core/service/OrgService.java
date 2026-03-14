package com.ksptool.bio.biz.core.service;


import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.core.model.org.OrgPo;
import com.ksptool.bio.biz.core.model.org.dto.AddOrgDto;
import com.ksptool.bio.biz.core.model.org.dto.EditOrgDto;
import com.ksptool.bio.biz.core.model.org.dto.GetOrgListDto;
import com.ksptool.bio.biz.core.model.org.dto.GetOrgTreeDto;
import com.ksptool.bio.biz.core.model.org.vo.GetOrgDetailsVo;
import com.ksptool.bio.biz.core.model.org.vo.GetOrgListVo;
import com.ksptool.bio.biz.core.model.org.vo.GetOrgTreeVo;
import com.ksptool.bio.biz.core.model.user.UserPo;
import com.ksptool.bio.biz.core.repository.OrgRepository;
import com.ksptool.bio.biz.core.repository.UserRepository;
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

    @Autowired
    private UserService userService;


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
     * @param dto 新增组织机构参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOrg(AddOrgDto dto) throws BizException {

        //是否是租户新增
        var isRootAdd = false;
        var isSubOrgAdd = false;

        //判断现在是在新增租户还是新增租户下的子机构 0:部门 1:企业(租户)
        if (dto.getKind() == 1) {
            isRootAdd = true;
        }

        if (dto.getKind() == 0) {
            isSubOrgAdd = true;
        }

        //先处理租户新增
        if (isRootAdd) {

            //租户不能有父级
            if (dto.getParentId() != null) {
                throw new BizException("无法处理新增请求,租户不能有父级.");
            }

            //校验租户名称是否唯一
            if (repository.countRootByName(dto.getName()) > 0) {
                throw new BizException("无法处理新增请求,企业名称 [" + dto.getName() + "] 已存在.");
            }

            //复制基本信息
            var addPo = as(dto, OrgPo.class);
            addPo.setRootId(-1L);
            addPo.setParentId(null);
            addPo.setOrgPathIds("");

            //先保存租户 以获取回填的ID
            addPo = repository.saveAndFlush(addPo);

            //后处理
            addPo.setRootId(addPo.getId());
            addPo.setOrgPathIds(addPo.getId().toString());
            repository.saveAndFlush(addPo);
            return;
        }

        //接下来处理所有非租户的普通机构新增
        if (isSubOrgAdd) {

            //普通机构必须有父级
            if (dto.getParentId() == null) {
                throw new BizException("无法处理新增请求,普通子机构必须有父级.");
            }

            //查询父级组织
            var parentPo = repository.findById(dto.getParentId())
                    .orElseThrow(() -> new BizException("无法处理新增请求,父级组织不存在. ID: " + dto.getParentId()));

            //校验子机构名称是否唯一
            if (repository.countDeptByNameAndParentId(dto.getName(), parentPo.getId()) > 0) {
                throw new BizException("无法处理新增请求,上级组织 [" + parentPo.getName() + "] 下已有同名子机构 [" + dto.getName() + "].");
            }

            //复制基本信息
            var addPo = as(dto, OrgPo.class);
            addPo.setRootId(parentPo.getRootId());
            addPo.setParentId(parentPo.getId());
            addPo.setOrgPathIds("");

            //如果有主管则需要处理主管(查询该子机构所在公司下的主管)
            if (dto.getPrincipalId() != null) {

                UserPo userPo = userRepository.findById(dto.getPrincipalId())
                        .orElseThrow(() -> new BizException("无法处理新增请求,主管不存在."));

                addPo.setPrincipalId(userPo.getId());
                addPo.setPrincipalName(userPo.getUsername());
            }

            //先保存子机构以获取回填的ID
            addPo = repository.saveAndFlush(addPo);

            //后处理路径(这里直接获取上级节点的路径，并在末尾拼接当前节点的ID)
            var orgPathIds = parentPo.getOrgPathIds() + "," + addPo.getId().toString();
            addPo.setOrgPathIds(orgPathIds);
            repository.saveAndFlush(addPo);

            //所有非租户的普通机构新增完成后，给该企业/租户下的全部在线用户加版本
            userService.increaseDvByRootId(parentPo.getRootId());
        }

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

        //是否是租户编辑
        var isRootEdit = false;
        var isSubOrgEdit = false;

        //判断现在是在编辑租户还是编辑租户下的子机构 0:部门 1:企业(租户)
        if (updatePo.getKind() == 1) {
            isRootEdit = true;
        }

        if (updatePo.getKind() == 0) {
            isSubOrgEdit = true;
        }

        //先处理租户编辑
        if (isRootEdit) {

            //租户不能有父级
            if (dto.getParentId() != null) {
                throw new BizException("无法处理编辑请求,租户不能有父级.");
            }

            //租户不能填写主管ID
            if (dto.getPrincipalId() != null) {
                throw new BizException("无法处理编辑请求,租户不能填写主管ID.");
            }

            //校验租户名称是否唯一
            if (repository.countRootByNameExcludeId(dto.getName(), updatePo.getId()) > 0) {
                throw new BizException("无法处理编辑请求,企业名称 [" + dto.getName() + "] 已存在.");
            }

            //复制基本信息
            assign(dto, updatePo);

            //处理路径
            updatePo.setOrgPathIds(updatePo.getId().toString());

            //保存租户
            repository.saveAndFlush(updatePo);
            return;
        }

        //接下来处理所有非租户的普通机构编辑
        if (isSubOrgEdit) {

            //普通机构必须有父级
            if (dto.getParentId() == null) {
                throw new BizException("无法处理编辑请求,普通子机构必须有父级.");
            }

            //查询父级组织
            var parentPo = repository.findById(dto.getParentId())
                    .orElseThrow(() -> new BizException("无法处理编辑请求,父级组织不存在. ID: " + dto.getParentId()));

            //父级组织不能是自身
            if (Objects.equals(updatePo.getId(), parentPo.getId())) {
                throw new BizException("无法处理编辑请求,父级组织不能是自身.");
            }

            //子机构不能跨租户移动
            if (!Objects.equals(updatePo.getRootId(), parentPo.getRootId())) {
                throw new BizException("无法处理编辑请求,子机构不能跨租户移动.");
            }

            //校验名称是否唯一
            if (repository.countDeptByNameAndParentIdExcludeId(dto.getName(), parentPo.getId(), updatePo.getId()) > 0) {
                throw new BizException("无法处理编辑请求,上级组织 [" + parentPo.getName() + "] 下已有同名子机构 [" + dto.getName() + "].");
            }

            //校验父级组织不能是当前组织的子孙节点
            var currentIdFlag = "," + updatePo.getId() + ",";
            var parentPathIds = "," + parentPo.getOrgPathIds() + ",";
            if (parentPathIds.contains(currentIdFlag)) {
                throw new BizException("无法处理编辑请求,父级组织不能是当前组织的子孙节点.");
            }

            //处理主管 先删除原主管
            updatePo.setPrincipalId(null);
            updatePo.setPrincipalName(null);

            if (dto.getPrincipalId() != null) {
                UserPo userPo = userRepository.findById(dto.getPrincipalId())
                        .orElseThrow(() -> new BizException("无法处理编辑请求,主管不存在."));
                updatePo.setPrincipalId(userPo.getId());
                updatePo.setPrincipalName(userPo.getUsername());
            }

            //处理子机构自身路径 先向上找直到Root为止 以此构建一个BasePathIds
            var basePathIds = new ArrayList<String>();
            basePathIds.add(updatePo.getId().toString());

            var parentId = parentPo.getId();

            while (parentId != null) {
                var parent = repository.findById(parentId)
                        .orElseThrow(() -> new BizException("无法处理编辑请求,父级组织不存在."));

                basePathIds.add(parent.getId().toString());
                
                //如果父级是NULL，则停止向上查找
                if (parent.getParentId() == null) {
                    break;
                }

                parentId = parent.getParentId();
            }
            //反转数组 因为向上查找时是反向的
            Collections.reverse(basePathIds);

            updatePo.setOrgPathIds(String.join(",", basePathIds));

            //递归处理子孙机构 需要重建这个机构以及这个机构下的全部子孙机构的组织路径ID(orgPathIds) 这里为了安全起见直接通过递归重建这个企业下全部子孙机构的orgPathIds
            var updatedPos = new ArrayList<OrgPo>();
            rebuildOrgPathIds(updatePo, basePathIds, updatedPos);
            //保存所有更新后的机构
            repository.saveAll(updatedPos);

            //复制基础信息
            assign(dto, updatePo);

            //保存当前修改的机构
            repository.save(updatePo);

            //给该企业/租户下的全部在线用户加版本
            userService.increaseDvByRootId(updatePo.getRootId());
        }

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


    /**
     * 递归重建组织路径ID
     *
     * @param po          当前机构
     * @param basePathIds 基础路径ID列表
     * @param updatedPos  更新后的机构列表
     */
    public void rebuildOrgPathIds(OrgPo po, List<String> basePathIds, List<OrgPo> updatedPos) {

        var currentBasePath = String.join(",", basePathIds);

        //查询当前机构下的子机构
        var subtree = repository.getByParentId(po.getId());

        if (subtree.isEmpty()) {
            return;
        }

        //处理每个子机构
        for (var childPo : subtree) {

            //构建新的基础路径ID列表
            var newBasePathIds = new ArrayList<String>();
            newBasePathIds.addAll(basePathIds);
            newBasePathIds.add(childPo.getId().toString());
            rebuildOrgPathIds(childPo, newBasePathIds, updatedPos);

            childPo.setOrgPathIds(currentBasePath + "," + childPo.getId().toString());
            updatedPos.add(childPo);
        }

    }

}