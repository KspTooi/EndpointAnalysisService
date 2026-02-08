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
     * 获取注册表列表
     *
     * @param dto 查询条件
     * @return 注册表列表
     */
    public PageResult<GetRegistryListVo> getRegistryList(GetRegistryListDto dto) {
        RegistryPo query = new RegistryPo();
        assign(dto, query);

        Page<RegistryPo> page = repository.getRegistryList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetRegistryListVo> vos = as(page.getContent(), GetRegistryListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增注册表
     *
     * @param dto 新增注册表
     */
    @Transactional(rollbackFor = Exception.class)
    public void addRegistry(AddRegistryDto dto) throws BizException {

        RegistryPo insertPo = as(dto, RegistryPo.class);

        //如果父级ID不为空，则需要查询父级是否存在
        if (dto.getParentId() != null) {

            var parentPo = repository.findById(dto.getParentId())
                    .orElseThrow(() -> new BizException("无法处理新增请求,父级项不存在 ID:" + dto.getParentId()));
            insertPo.setParentId(parentPo.getId());

            //如果配置了父级 需要处理KEY的全路径
            var pathNKeys = new ArrayList<String>();
            pathNKeys.add(insertPo.getNkey());
            pathNKeys.add(parentPo.getNkey());

            var _parentPo = parentPo;

            while (_parentPo.getParentId() != null) {

                var parentId = _parentPo.getParentId();
                _parentPo = repository.findById(parentId)
                        .orElseThrow(() -> new BizException("无法处理新增请求,父级项不存在 ID:" + parentId));

                pathNKeys.add(_parentPo.getNkey());

                if(_parentPo.getParentId() == null){
                    break;
                }

            }

            insertPo.setKeyPath(String.join(".", pathNKeys));
        }

        repository.save(insertPo);
    }

    /**
     * 编辑注册表
     *
     * @param dto 编辑注册表
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editRegistry(EditRegistryDto dto) throws BizException {
        RegistryPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 获取注册表详情
     *
     * @param dto 查询条件
     * @return 注册表详情
     * @throws BizException 业务异常
     */
    public GetRegistryDetailsVo getRegistryDetails(CommonIdDto dto) throws BizException {
        RegistryPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetRegistryDetailsVo.class);
    }

    /**
     * 删除注册表
     *
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeRegistry(CommonIdDto dto) throws BizException {

        var ids = dto.toIds();

        //验证这些ID是否存在
        if (repository.countByIds(ids) != ids.size()) {
            throw new BizException("删除失败,数据不存在或无权限访问.");
        }

        var safeRemoveIds = new ArrayList<Long>();

        for (var id : ids) {

            //如果该项下有子项，则不能删除
            if (repository.countByParentId(id) > 0) {

                //如果只是单个删除 则直接返回错误 如果为多个删除 则跳过该项目 静默删除其他项
                if (!dto.isBatch()) {
                    throw new BizException("无法处理删除请求,该项下有子项,不能删除.");
                }
                continue;
            }

            safeRemoveIds.add(id);
        }

        if (safeRemoveIds.isEmpty()) {
            throw new BizException("删除失败,没有查找到合法的项以供进行操作!");
        }

        //删除合法的项
        repository.deleteAllById(safeRemoveIds);
    }

}