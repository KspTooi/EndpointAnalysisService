package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.exceltemplate.ExcelTemplatePo;
import com.ksptooi.biz.core.model.exceltemplate.dto.AddExcelTemplateDto;
import com.ksptooi.biz.core.model.exceltemplate.dto.EditExcelTemplateDto;
import com.ksptooi.biz.core.model.exceltemplate.dto.GetExcelTemplateListDto;
import com.ksptooi.biz.core.model.exceltemplate.vo.GetExcelTemplateDetailsVo;
import com.ksptooi.biz.core.model.exceltemplate.vo.GetExcelTemplateListVo;
import com.ksptooi.biz.core.repository.ExcelTemplateRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class ExcelTemplateService {

    @Autowired
    private ExcelTemplateRepository repository;

    @Autowired
    private AttachService attachService;

    /**
     * 查询Excel模板列表
     *
     * @param dto 查询条件
     * @return 模板列表
     */
    public PageResult<GetExcelTemplateListVo> getExcelTemplateList(GetExcelTemplateListDto dto) {
        ExcelTemplatePo query = new ExcelTemplatePo();
        assign(dto, query);

        Page<ExcelTemplatePo> page = repository.getExcelTemplateList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetExcelTemplateListVo> vos = as(page.getContent(), GetExcelTemplateListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增Excel模板
     *
     * @param dto 新增模板
     */
    @Transactional(rollbackFor = Exception.class)
    public void addExcelTemplate(AddExcelTemplateDto dto) {
        ExcelTemplatePo insertPo = as(dto, ExcelTemplatePo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑Excel模板
     *
     * @param dto 编辑模板
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editExcelTemplate(EditExcelTemplateDto dto) throws BizException {
        ExcelTemplatePo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        //查询code是否被占用
        var existPo = repository.getExcelTemplateByCode(dto.getCode());

        if (existPo != null && existPo.getId() != updatePo.getId()) {
            throw new BizException("模板标识已存在:[" + existPo.getCode() + "]");
        }

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询Excel模板详情
     *
     * @param dto 查询条件
     * @return 模板详情
     * @throws BizException 业务异常
     */
    public GetExcelTemplateDetailsVo getExcelTemplateDetails(CommonIdDto dto) throws BizException {
        ExcelTemplatePo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetExcelTemplateDetailsVo.class);
    }

    /**
     * 删除Excel模板
     *
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeExcelTemplate(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

    /**
     * 上传Excel模板
     *
     * @param file 上传文件
     * @throws BizException 业务异常
     */
    public void uploadExcelTemplate(MultipartFile[] file) throws BizException {

        var addPos = new ArrayList<ExcelTemplatePo>();

        for (var f : file) {
            var po = new ExcelTemplatePo();
            parseTemplateName(f, po);
            po.setStatus(0); //默认启用 0:启用 1:禁用

            //查询code是否被占用
            var existPo = repository.getExcelTemplateByCode(po.getCode());

            if (existPo != null) {
                repository.delete(existPo);

                //复制状态、描述到新模板
                po.setStatus(existPo.getStatus());
                po.setRemark(existPo.getRemark());
            }

            //处理附件上传
            var attachId = attachService.uploadAttach(f, "core_excel_template");
            po.setAttachId(attachId);
            addPos.add(po);
        }

        repository.saveAll(addPos);
    }


    /**
     * 解析模板名称
     * <p>
     * 模板命名规则
     * 模板名称-唯一标识符.xlsx
     *
     * @param file 上传文件
     * @param po   模板实体
     * @throws BizException 业务异常
     */
    public void parseTemplateName(MultipartFile file, ExcelTemplatePo po) throws BizException {

        var name = file.getOriginalFilename();

        if (name == null) {
            throw new BizException("文件名不能为空");
        }

        var suffix = name.substring(name.lastIndexOf(".") + 1);

        if (suffix == null) {
            throw new BizException("文件后缀不能为空");
        }

        //截取模板名称
        var tName = name.substring(0, name.lastIndexOf("-"));
        var tCode = name.substring(name.lastIndexOf("-") + 1).replace(".xlsx", "").toLowerCase();

        if (StringUtils.isBlank(tName) || StringUtils.isBlank(tCode)) {
            throw new BizException("模板名称或唯一标识符不能为空");
        }

        if (tName.length() > 32) {
            throw new BizException("模板名称不能超过32个字符");
        }

        if (tCode.length() > 32) {
            throw new BizException("唯一标识符不能超过32个字符");
        }

        po.setName(tName);
        po.setCode(tCode);
    }


}