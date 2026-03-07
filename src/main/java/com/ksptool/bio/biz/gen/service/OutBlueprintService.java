package com.ksptool.bio.biz.gen.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.gen.model.outblueprint.GenOutBlueprintPo;
import com.ksptool.bio.biz.gen.model.outblueprint.dto.AddOutBlueprintDto;
import com.ksptool.bio.biz.gen.model.outblueprint.dto.EditOutBlueprintDto;
import com.ksptool.bio.biz.gen.model.outblueprint.dto.GetOutBlueprintListDto;
import com.ksptool.bio.biz.gen.model.outblueprint.vo.GetOutBlueprintDetailsVo;
import com.ksptool.bio.biz.gen.model.outblueprint.vo.GetOutBlueprintListVo;
import com.ksptool.bio.biz.gen.repository.OutBlueprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class OutBlueprintService {

    @Autowired
    private OutBlueprintRepository repository;

    /**
     * 获取输出蓝图列表
     * @param dto 查询条件
     * @return 输出蓝图列表
     */
    public PageResult<GetOutBlueprintListVo> getOutBlueprintList(GetOutBlueprintListDto dto) {
        GenOutBlueprintPo query = new GenOutBlueprintPo();
        assign(dto, query);

        Page<GenOutBlueprintPo> page = repository.getOutBlueprintList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetOutBlueprintListVo> vos = as(page.getContent(), GetOutBlueprintListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增输出蓝图
     * @param dto 新增数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOutBlueprint(AddOutBlueprintDto dto) {
        GenOutBlueprintPo insertPo = as(dto, GenOutBlueprintPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑输出蓝图
     * @param dto 编辑数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void editOutBlueprint(EditOutBlueprintDto dto) throws BizException {
        GenOutBlueprintPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 获取输出蓝图详情
     * @param dto 查询条件
     * @return
     */
    public GetOutBlueprintDetailsVo getOutBlueprintDetails(CommonIdDto dto) throws BizException {
        GenOutBlueprintPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetOutBlueprintDetailsVo.class);
    }

    /**
     * 删除输出蓝图
     * @param dto 删除条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeOutBlueprint(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}