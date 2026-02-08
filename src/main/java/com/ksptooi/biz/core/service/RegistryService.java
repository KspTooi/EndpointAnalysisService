package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.registry.RegistryPo;
import com.ksptooi.biz.core.model.registry.dto.AddRegistryDto;
import com.ksptooi.biz.core.model.registry.dto.EditRegistryDto;
import com.ksptooi.biz.core.model.registry.dto.GetRegistryListDto;
import com.ksptooi.biz.core.model.registry.vo.GetRegistryDetailsVo;
import com.ksptooi.biz.core.model.registry.vo.GetRegistryListVo;
import com.ksptooi.biz.core.repository.RegistryRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class RegistryService {

    @Autowired
    private RegistryRepository repository;

    /**
     * 获取注册表节点列表
     *
     * @param dto 查询条件
     * @return 注册表节点列表
     */
    public PageResult<GetRegistryListVo> getRegistryNodeList(GetRegistryListDto dto) {
        RegistryPo query = new RegistryPo();
        assign(dto, query);

        Page<RegistryPo> page = repository.getRegistryNodeList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetRegistryListVo> vos = as(page.getContent(), GetRegistryListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增注册表节点
     *
     * @param dto 新增注册表节点
     */
    @Transactional(rollbackFor = Exception.class)
    public void addRegistryNode(AddRegistryDto dto) throws BizException {

        RegistryPo insertPo = as(dto, RegistryPo.class);

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

        repository.save(insertPo);
    }

    /**
     * 编辑注册表节点
     *
     * @param dto 编辑注册表节点
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editRegistryNode(EditRegistryDto dto) throws BizException {
        RegistryPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 获取注册表节点详情
     *
     * @param dto 查询条件
     * @return 注册表节点详情
     * @throws BizException 业务异常
     */
    public GetRegistryDetailsVo getRegistryNodeDetails(CommonIdDto dto) throws BizException {
        RegistryPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetRegistryDetailsVo.class);
    }

    /**
     * 删除注册表节点
     *
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeRegistryNode(CommonIdDto dto) throws BizException {

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