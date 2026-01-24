package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.dept.DeptPo;
import com.ksptooi.biz.core.model.dept.dto.AddDeptDto;
import com.ksptooi.biz.core.model.dept.dto.EditDeptDto;
import com.ksptooi.biz.core.model.dept.dto.GetDeptListDto;
import com.ksptooi.biz.core.model.dept.vo.GetDeptDetailsVo;
import com.ksptooi.biz.core.model.dept.vo.GetDeptListVo;
import com.ksptooi.biz.core.model.dept.vo.GetDeptTreeVo;
import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.core.repository.DeptRepository;
import com.ksptooi.biz.core.repository.UserRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class DeptService {

    @Autowired
    private DeptRepository repository;

    @Autowired
    private UserRepository userRepository;


    /**
     * 查询部门树
     *
     * @return 部门树
     */
    public Result<List<GetDeptTreeVo>> getDeptTree() {

        // 查询全量部门数据
        List<DeptPo> allDepts = repository.getDeptListOrderBySeq();

        if (allDepts.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        // 将PO转换为VO平面列表，初始化children字段
        List<GetDeptTreeVo> flatVos = new ArrayList<>();
        for (DeptPo po : allDepts) {
            GetDeptTreeVo vo = as(po, GetDeptTreeVo.class);
            vo.setChildren(new ArrayList<>());
            vo.setParentId(po.getParentId());
            flatVos.add(vo);
        }

        // 根节点集合
        List<GetDeptTreeVo> treeVos = new ArrayList<>();
        // 构建id -> vo的映射，用于快速查找父节点
        Map<Long, GetDeptTreeVo> map = new HashMap<>();

        for (GetDeptTreeVo vo : flatVos) {
            map.put(vo.getId(), vo);
        }

        // 遍历所有节点，按parentId挂载到父节点或作为根节点
        for (GetDeptTreeVo vo : flatVos) {
            // parentId为null表示根节点
            if (vo.getParentId() == null) {
                treeVos.add(vo);
                continue;
            }

            // 查找父节点并挂载
            GetDeptTreeVo parent = map.get(vo.getParentId());
            if (parent != null) {
                parent.getChildren().add(vo);
                continue;
            }
            // 父节点不存在时，也作为根节点处理
            treeVos.add(vo);
        }

        return Result.success(treeVos);
    }

    /**
     * 查询部门列表
     *
     * @param dto 查询条件
     * @return 部门列表
     */
    public PageResult<GetDeptListVo> getDeptList(GetDeptListDto dto) {
        DeptPo query = new DeptPo();
        assign(dto, query);

        Page<DeptPo> page = repository.getDeptList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetDeptListVo> vos = as(page.getContent(), GetDeptListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增部门
     *
     * @param dto 新增部门
     */
    @Transactional(rollbackFor = Exception.class)
    public void addDept(AddDeptDto dto) throws BizException {
        DeptPo insertPo = as(dto, DeptPo.class);

        if (dto.getPrincipalId() != null) {
            UserPo principal = userRepository.findById(dto.getPrincipalId())
                    .orElseThrow(() -> new BizException("负责人不存在"));
            insertPo.setPrincipalName(principal.getNickname());
        }

        repository.save(insertPo);
    }

    /**
     * 编辑部门
     *
     * @param dto 编辑部门
     */
    @Transactional(rollbackFor = Exception.class)
    public void editDept(EditDeptDto dto) throws BizException {
        DeptPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        assign(dto, updatePo);

        if (dto.getPrincipalId() != null) {
            UserPo principal = userRepository.findById(dto.getPrincipalId())
                    .orElseThrow(() -> new BizException("负责人不存在"));
            updatePo.setPrincipalName(principal.getNickname());
        }
        if (dto.getPrincipalId() == null) {
            updatePo.setPrincipalId(null);
            updatePo.setPrincipalName(null);
        }

        repository.save(updatePo);
    }

    /**
     * 查询部门详情
     *
     * @param dto 查询条件
     * @return 部门详情
     */
    public GetDeptDetailsVo getDeptDetails(CommonIdDto dto) throws BizException {
        DeptPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetDeptDetailsVo.class);
    }

    /**
     * 删除部门
     *
     * @param dto 删除条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeDept(CommonIdDto dto) throws BizException {

        //查询部门
        DeptPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("删除失败,数据不存在."));

        if (repository.countByParentId(po.getId()) > 0) {
            throw new BizException("该部门下存在子部门,删除操作失败!");
        }

        repository.delete(po);
    }

}
