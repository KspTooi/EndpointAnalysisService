package com.ksptool.bio.biz.assembly.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.assembly.model.opschema.OpSchemaPo;
import com.ksptool.bio.biz.assembly.model.polymodel.PolyModelPo;
import com.ksptool.bio.biz.assembly.model.polymodel.dto.AddPolyModelDto;
import com.ksptool.bio.biz.assembly.model.polymodel.dto.EditPolyModelDto;
import com.ksptool.bio.biz.assembly.model.polymodel.dto.GetPolyModelListDto;
import com.ksptool.bio.biz.assembly.model.polymodel.vo.GetPolyModelDetailsVo;
import com.ksptool.bio.biz.assembly.model.polymodel.vo.GetPolyModelListVo;
import com.ksptool.bio.biz.assembly.model.tymschemafield.TymSchemaFieldPo;
import com.ksptool.bio.biz.assembly.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class PolyModelService {

    @Autowired
    private PolyModelRepository repository;

    @Autowired
    private RawModelRepository omoRepository;

    @Autowired
    private TymSchemaRepository tymSchemaRepository;

    @Autowired
    private TymSchemaFieldRepository tymSfRepository;

    @Autowired
    private OpSchemaRepository opSchemaRepository;

    /**
     * 查询聚合模型列表
     *
     * @param dto 查询参数
     * @return 聚合模型列表
     */
    public PageResult<GetPolyModelListVo> getPolyModelList(GetPolyModelListDto dto) {
        PolyModelPo query = new PolyModelPo();
        assign(dto, query);

        List<PolyModelPo> list = repository.getPolyModelList(query);
        if (list.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetPolyModelListVo> vos = as(list, GetPolyModelListVo.class);
        return PageResult.success(vos, list.size());
    }

    /**
     * 新增聚合模型
     *
     * @param dto 新增参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addPolyModel(AddPolyModelDto dto) {
        PolyModelPo insertPo = as(dto, PolyModelPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑聚合模型
     *
     * @param dto 编辑参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editPolyModel(EditPolyModelDto dto) throws BizException {
        PolyModelPo updatePo = repository.findById(dto.getId())
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
    public GetPolyModelDetailsVo getPolyModelDetails(CommonIdDto dto) throws BizException {
        PolyModelPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetPolyModelDetailsVo.class);
    }

    /**
     * 删除聚合模型元素
     *
     * @param dto 删除参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removePolyModel(CommonIdDto dto) throws BizException {
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
    public void syncPolyModelFromOriginBySchema(CommonIdDto dto) throws BizException {

        //先查询输出方案
        OpSchemaPo opSchemaPo = opSchemaRepository.findById(dto.getId()).orElseThrow(() -> new BizException("输出方案不存在"));

        //检查输出方案是否具有类型映射方案
        if (opSchemaPo.getTypeSchemaId() == null) {
            throw new BizException("输出方案未绑定类型映射方案,请先绑定类型映射方案");
        }

        //检查输出方案是否具有原始模型
        if (omoRepository.getRawModelByOutputSchemaId(opSchemaPo.getId()).isEmpty()) {
            throw new BizException("输出方案下没有原始模型");
        }

        //获取类型映射方案ID
        var tymSid = opSchemaPo.getTypeSchemaId();

        //获取类型映射方案
        var tymsPo = tymSchemaRepository.findById(tymSid).orElseThrow(() -> new BizException("类型映射方案不存在"));

        //查找输出方案下全部的原始模型
        var omoPos = omoRepository.getRawModelByOutputSchemaId(opSchemaPo.getId());

        //查找输出方案下绑定的全部映射方案
        var tymSfPos = tymSfRepository.getTymSfByTymSid(tymSid);

        //聚合模型
        var ompPos = new ArrayList<PolyModelPo>();

        //先清空输出方案下全部的聚合模型
        repository.clearPolyModelByOutputSchemaId(opSchemaPo.getId());

        //遍历原始模型
        for (var omoPo : omoPos) {

            //查找原始模型匹配的映射方案
            TymSchemaFieldPo mat = null;
            var targetType = tymsPo.getDefaultType();

            for (var tymSfPo : tymSfPos) {
                if (tymSfPo.getSource().equals(omoPo.getDataType())) {
                    mat = tymSfPo;
                    break;
                }
            }

            //如果匹配到tymSf 则使用tymSf的target类型
            if (mat != null) {
                targetType = mat.getTarget();
            }

            //组装聚合模型
            var ompPo = new PolyModelPo();
            ompPo.setOutputSchemaId(opSchemaPo.getId());
            ompPo.setName(omoPo.getName());
            ompPo.setDataType(targetType);
            ompPo.setLength(omoPo.getLength());
            ompPo.setRequire(omoPo.getRequire());
            ompPo.setPolicyCrudJson(Set.of("AD", "ED", "DV", "LD", "LV"));
            ompPo.setPolicyQuery(0); //0:等于 1:模糊
            ompPo.setPolicyView(0); //0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT
            ompPo.setRemark(omoPo.getRemark());
            ompPo.setSeq(omoPo.getSeq());
            ompPos.add(ompPo);
        }

        //批量保存聚合模型
        repository.saveAll(ompPos);
    }

}