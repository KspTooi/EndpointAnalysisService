package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.registry.RegistryPo;
import com.ksptooi.biz.core.model.registry.dto.AddRegistryDto;
import com.ksptooi.biz.core.model.registry.dto.EditRegistryDto;
import com.ksptooi.biz.core.model.registry.dto.GetRegistryListDto;
import com.ksptooi.biz.core.model.registry.vo.GetRegistryDetailsVo;
import com.ksptooi.biz.core.model.registry.vo.GetRegistryEntryListVo;
import com.ksptooi.biz.core.model.registry.vo.GetRegistryNodeTreeVo;
import com.ksptooi.biz.core.repository.RegistryRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class RegistryService {

    @Autowired
    private RegistryRepository repository;

    /**
     * 获取注册表条目列表
     *
     * @return 注册表条目列表
     */
    public List<GetRegistryNodeTreeVo> getRegistryNodeTree() {

        List<RegistryPo> nodePos = repository.getRegistryAllNodes();

        List<GetRegistryNodeTreeVo> flatNodeVos = new ArrayList<>();
        List<GetRegistryNodeTreeVo> treeNodeVos = new ArrayList<>();

        //处理PO为平面节点
        for (RegistryPo item : nodePos) {
            GetRegistryNodeTreeVo vo = as(item, GetRegistryNodeTreeVo.class);
            vo.setChildren(new ArrayList<>());
            flatNodeVos.add(vo);
        }

        //将平面节点转换为树结构
        Map<Long, GetRegistryNodeTreeVo> idToNode = new HashMap<>();

        //先将所有节点添加到映射中
        for (GetRegistryNodeTreeVo node : flatNodeVos) {
            idToNode.put(node.getId(), node);
        }

        //构建树结构
        for (GetRegistryNodeTreeVo node : flatNodeVos) {
            Long parentId = node.getParentId();
            if (parentId == null) {
                treeNodeVos.add(node);
                continue;
            }
            GetRegistryNodeTreeVo parent = idToNode.get(parentId);
            if (parent == null) {
                treeNodeVos.add(node);
                continue;
            }
            parent.getChildren().add(node);
        }

        return treeNodeVos;
    }

    /**
     * 获取注册表条目列表
     *
     * @return 注册表条目列表
     */
    public List<GetRegistryEntryListVo> getRegistryEntryList(GetRegistryListDto dto) throws BizException {

        //先根据全路径查询到节点
        RegistryPo nodePo = repository.getRegistryNodeByKeyPath(dto.getKeyPath());

        if (nodePo == null) {
            throw new BizException("查询注册表条目列表失败,节点不存在: " + dto.getKeyPath());
        }

        //查询该节点下全部子项
        List<RegistryPo> entryPos = repository.getRegistryEntryList(dto);
        return as(entryPos, GetRegistryEntryListVo.class);
    }


    /**
     * 新增注册表条目
     *
     * @param dto 新增注册表条目
     */
    @Transactional(rollbackFor = Exception.class)
    public void addRegistry(AddRegistryDto dto) throws BizException {

        RegistryPo insertPo = as(dto, RegistryPo.class);

        //处理新增节点 0:节点 1:条目
        if (dto.getKind() == 0) {

            // 如果是顶级节点 KEY_PATH为自身
            if (dto.getParentId() == null) {
                insertPo.setKeyPath(insertPo.getNkey());
            }

            // 如果父级ID不为空，则需要查询父级是否存在
            if (dto.getParentId() != null) {

                var parentPo = repository.findById(dto.getParentId())
                        .orElseThrow(() -> new BizException("无法处理新增请求,父级项不存在 ID:" + dto.getParentId()));
                insertPo.setParentId(parentPo.getId());

                // 如果配置了父级 需要处理KEY的全路径
                insertPo.setKeyPath(parentPo.getKeyPath() + "." + insertPo.getNkey());
            }

            // 校验keypath是否唯一
            if (repository.countByKeyPath(insertPo.getKeyPath()) > 0) {
                throw new BizException("新增失败,KEY的全路径已存在: " + insertPo.getKeyPath());
            }

            insertPo.setKind(0);
            insertPo.setNvalueKind(null);
            insertPo.setNvalue(null);
            insertPo.setMetadata(null);
            insertPo.setIsSystem(0);
            insertPo.setStatus(0);
        }

        //处理新增条目 1:条目
        if (dto.getKind() == 1) {

            //条目必须有父级
            var parentPo = repository.findById(dto.getParentId())
                    .orElseThrow(() -> new BizException("无法处理新增条目请求,父级节点不存在 ID:" + dto.getParentId()));
            insertPo.setParentId(parentPo.getId());

            //条目的父级必须是节点
            if (parentPo.getKind() != 0) {
                throw new BizException("无法处理新增条目请求,父级节点必须是节点 ID:" + dto.getParentId());
            }

            //处理条目Key的全路径
            insertPo.setKeyPath(parentPo.getKeyPath() + "." + insertPo.getNkey());

            //校验keypath是否唯一
            if (repository.countByKeyPath(insertPo.getKeyPath()) > 0) {
                throw new BizException("新增失败,KEY的全路径已存在: " + insertPo.getKeyPath());
            }

            //设置固定字段
            insertPo.setIsSystem(0);
        }

        repository.save(insertPo);
    }

    /**
     * 编辑注册表条目
     *
     * @param dto 编辑注册表条目
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editRegistry(EditRegistryDto dto) throws BizException {
        RegistryPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        //校验数值真实性
        if(updatePo.getKind() != dto.getKind()){
            throw new BizException("无法处理编辑请求,输入类型与实际类型不一致. 输入类型:" + dto.getKind() + ",实际类型:" + updatePo.getKind());
        }

        if(updatePo.getNvalueKind() != dto.getNvalueKind()){
            throw new BizException("无法处理编辑请求,输入数据类型与实际数据类型不一致. 输入数据类型:" + dto.getNvalueKind() + ",实际数据类型:" + updatePo.getNvalueKind());
        }

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 获取注册表条目详情
     *
     * @param dto 查询条件
     * @return 注册表条目详情
     * @throws BizException 业务异常
     */
    public GetRegistryDetailsVo getRegistryDetails(CommonIdDto dto) throws BizException {
        RegistryPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetRegistryDetailsVo.class);
    }

    /**
     * 删除注册表条目
     *
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeRegistry(CommonIdDto dto) throws BizException {

        List<Long> ids = dto.toIds();
        if (ids == null || ids.isEmpty())
            return;

        // 查出实际存在的ID，避免报错
        List<RegistryPo> existPos = repository.findAllById(ids);

        if (existPos.isEmpty()) {
            throw new BizException("删除失败, 数据不存在.");
        }

        List<Long> safeRemoveIds = new ArrayList<>();

        for (RegistryPo po : existPos) {
            // 检查是否有子节点
            long childCount = repository.countByParentId(po.getId());

            if (childCount > 0) {
                // 如果是单删，必须报错提示
                if (!dto.isBatch()) {
                    throw new BizException("无法删除 [" + po.getLabel() + "], 请先删除其下级节点.");
                }
                // 如果是批量，跳过该节点(静默失败)
                continue;
            }

            safeRemoveIds.add(po.getId());
        }

        if (safeRemoveIds.isEmpty()) {
            throw new BizException("删除失败, 所选节点均包含子节点或不可删除.");
        }

        repository.deleteAllById(safeRemoveIds);
    }

}