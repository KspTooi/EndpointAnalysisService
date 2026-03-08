package com.ksptool.bio.biz.gen.service;

import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.exception.BizException;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;

import com.ksptool.bio.biz.gen.repository.OutModelOriginRepository;
import com.ksptool.bio.biz.gen.repository.OutModelPolyRepository;
import com.ksptool.bio.biz.gen.model.outmodelorigin.OutModelOriginPo;
import com.ksptool.bio.biz.gen.model.outmodelpoly.OutModelPolyPo;
import com.ksptool.bio.biz.gen.model.outmodelpoly.vo.GetOutModelPolyListVo;
import com.ksptool.bio.biz.gen.model.outschema.OutSchemaPo;
import com.ksptool.bio.biz.gen.model.tymschema.TymSchemaPo;
import com.ksptool.bio.biz.gen.model.tymschemafield.TymSchemaFieldPo;
import com.ksptool.bio.biz.gen.model.outmodelpoly.dto.GetOutModelPolyListDto;
import com.ksptool.bio.biz.gen.model.outmodelpoly.vo.GetOutModelPolyDetailsVo;
import com.ksptool.bio.biz.gen.model.outmodelpoly.dto.EditOutModelPolyDto;
import com.ksptool.bio.biz.gen.model.outmodelpoly.dto.AddOutModelPolyDto;
import com.ksptool.bio.biz.gen.repository.TymSchemaFieldRepository;
import com.ksptool.bio.biz.gen.repository.TymSchemaRepository;
import com.ksptool.bio.biz.gen.repository.OutSchemaRepository;

@Service
public class OutModelPolyService {

    @Autowired
    private OutModelPolyRepository repository;

    @Autowired
    private OutModelOriginRepository omoRepository;

    @Autowired
    private TymSchemaRepository tymSchemaRepository;

    @Autowired
    private TymSchemaFieldRepository tymSfRepository;

    @Autowired
    private OutSchemaRepository outSchemaRepository;

    /**
     * 查询聚合模型列表
     *
     * @param dto 查询参数
     * @return 聚合模型列表
     */
    public PageResult<GetOutModelPolyListVo> getOutModelPolyList(GetOutModelPolyListDto dto) {
        OutModelPolyPo query = new OutModelPolyPo();
        assign(dto, query);

        Page<OutModelPolyPo> page = repository.getOutModelPolyList(query);
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetOutModelPolyListVo> vos = as(page.getContent(), GetOutModelPolyListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增聚合模型
     *
     * @param dto 新增参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOutModelPoly(AddOutModelPolyDto dto) {
        OutModelPolyPo insertPo = as(dto, OutModelPolyPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑聚合模型
     *
     * @param dto 编辑参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editOutModelPoly(EditOutModelPolyDto dto) throws BizException {
        OutModelPolyPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询聚合模型详情
     *
     * @param dto 查询参数
     * @return 聚合模型详情
     * @throws BizException 业务异常
     */
    public GetOutModelPolyDetailsVo getOutModelPolyDetails(CommonIdDto dto) throws BizException {
        OutModelPolyPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetOutModelPolyDetailsVo.class);
    }

    /**
     * 删除聚合模型元素
     *
     * @param dto 删除参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeOutModelPoly(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

    /**
     * 从原始模型同步聚合模型(输出方案ID)
     *
     * @param dto 同步参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncFromOriginBySchema(CommonIdDto dto) throws BizException {

        //先查询输出方案
        OutSchemaPo outSchemaPo = outSchemaRepository.findById(dto.getId()).orElseThrow(() -> new BizException("输出方案不存在"));

        //检查输出方案是否具有类型映射方案
        if (outSchemaPo.getTypeSchemaId() == null) {
            throw new BizException("输出方案未绑定类型映射方案,请先绑定类型映射方案");
        }

        //检查输出方案是否具有原始模型
        if (omoRepository.getOmoByOutputSchemaId(outSchemaPo.getId()).isEmpty()) {
            throw new BizException("输出方案下没有原始模型");
        }

        //获取类型映射方案ID
        var tymSid = outSchemaPo.getTypeSchemaId();

        //获取类型映射方案
        var tymsPo = tymSchemaRepository.findById(tymSid).orElseThrow(() -> new BizException("类型映射方案不存在"));

        //查找输出方案下全部的原始模型
        var omoPos = omoRepository.getOmoByOutputSchemaId(outSchemaPo.getId());

        //查找输出方案下绑定的全部映射方案
        var tymSfPos = tymSfRepository.getTymSfByTymSid(tymSid);

        //聚合模型
        var ompPos = new ArrayList<OutModelPolyPo>();
 
        //遍历原始模型
        for (var omoPo : omoPos) {

            //查找原始模型匹配的映射方案
            TymSchemaFieldPo mat = null;
            var targetType = tymsPo.getDefaultType();

            for (var tymSfPo : tymSfPos) {
                if (tymSfPo.getSource().equals(omoPo.getKind())) {
                    mat = tymSfPo;
                    break;
                }
            }

            //组装聚合模型
            var ompPo = new OutModelPolyPo();
            ompPo.setOutputSchemaId(outSchemaPo.getId());
            ompPo.setOutputModelOriginId(omoPo.getId());
            ompPo.setName(omoPo.getRemark());
            ompPo.setKind(targetType);
            ompPo.setLength(omoPo.getLength());
            ompPo.setRequire(omoPo.getRequire());
            //ompPo.setPolicyCrudJson(omoPo.getPolicyCrudJson());
            //ompPo.setPolicyQuery(omoPo.getPolicyQuery());
            //ompPo.setPolicyView(omoPo.getPolicyView());
            //ompPo.setPlaceholder(omoPo.getPlaceholder());
            ompPo.setSeq(omoPo.getSeq());
            ompPos.add(ompPo);
        }


    }

}