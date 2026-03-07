package com.ksptool.bio.biz.outmodelorigin.service;

import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.exception.BizException;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import java.util.Optional;
import com.ksptool.bio.biz.outmodelorigin.repository.OutModelOriginRepository;
import com.ksptool.bio.biz.outmodelorigin.model.OutModelOriginPo;
import com.ksptool.bio.biz.outmodelorigin.model.vo.GetOutModelOriginListVo;
import com.ksptool.bio.biz.outmodelorigin.model.dto.GetOutModelOriginListDto;
import com.ksptool.bio.biz.outmodelorigin.model.vo.GetOutModelOriginDetailsVo;
import com.ksptool.bio.biz.outmodelorigin.model.dto.EditOutModelOriginDto;
import com.ksptool.bio.biz.outmodelorigin.model.dto.AddOutModelOriginDto;


@Service
public class OutModelOriginService {

    @Autowired
    private OutModelOriginRepository repository;


    /**
     * 查询原始模型列表
     * @param dto 查询参数
     * @return 原始模型列表
     */
    public PageResult<GetOutModelOriginListVo> getOutModelOriginList(GetOutModelOriginListDto dto){
        OutModelOriginPo query = new OutModelOriginPo();
        assign(dto,query);

        Page<OutModelOriginPo> page = repository.getOutModelOriginList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetOutModelOriginListVo> vos = as(page.getContent(), GetOutModelOriginListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增原始模型
     * @param dto 新增参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOutModelOrigin(AddOutModelOriginDto dto){
        OutModelOriginPo insertPo = as(dto,OutModelOriginPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑原始模型
     * @param dto 编辑参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editOutModelOrigin(EditOutModelOriginDto dto) throws BizException {
        OutModelOriginPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询原始模型详情
     * @param dto 查询参数
     * @return 原始模型详情
     * @throws BizException 业务异常
     */
    public GetOutModelOriginDetailsVo getOutModelOriginDetails(CommonIdDto dto) throws BizException {
        OutModelOriginPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetOutModelOriginDetailsVo.class);
    }

    /**
     * 删除原始模型元素
     * @param dto 删除参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeOutModelOrigin(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}