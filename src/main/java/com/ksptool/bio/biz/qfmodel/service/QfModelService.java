package com.ksptool.bio.biz.qfmodel.service;

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
import com.ksptool.bio.biz.qfmodel.repository.QfModelRepository;
import com.ksptool.bio.biz.qfmodel.model.QfModelPo;
import com.ksptool.bio.biz.qfmodel.model.vo.GetQfModelListVo;
import com.ksptool.bio.biz.qfmodel.model.dto.GetQfModelListDto;
import com.ksptool.bio.biz.qfmodel.model.vo.GetQfModelDetailsVo;
import com.ksptool.bio.biz.qfmodel.model.dto.EditQfModelDto;
import com.ksptool.bio.biz.qfmodel.model.dto.AddQfModelDto;


@Service
public class QfModelService {

    @Autowired
    private QfModelRepository repository;

    /**
     * 查询流程模型列表
     * @param dto 查询参数
     * @return 模型列表
     */
    public PageResult<GetQfModelListVo> getQfModelList(GetQfModelListDto dto){
        QfModelPo query = new QfModelPo();
        assign(dto,query);

        Page<QfModelPo> page = repository.getQfModelList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetQfModelListVo> vos = as(page.getContent(), GetQfModelListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增流程模型
     * @param dto 新增参数
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void addQfModel(AddQfModelDto dto){
        QfModelPo insertPo = as(dto,QfModelPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑流程模型
     * @param dto 编辑参数
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void editQfModel(EditQfModelDto dto) throws BizException {
        QfModelPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询流程模型详情
     * @param dto 查询参数
     * @return 模型详情
     * @throws BizException
     */
    public GetQfModelDetailsVo getQfModelDetails(CommonIdDto dto) throws BizException {
        QfModelPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetQfModelDetailsVo.class);
    }

    /**
     * 删除流程模型
     * @param dto 删除参数
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeQfModel(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}