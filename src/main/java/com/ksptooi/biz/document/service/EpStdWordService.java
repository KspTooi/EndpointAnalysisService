package com.ksptooi.biz.document.service;

import com.ksptooi.biz.document.model.epstdword.EpStdWordPo;
import com.ksptooi.biz.document.model.epstdword.dto.AddEpStdWordDto;
import com.ksptooi.biz.document.model.epstdword.dto.EditEpStdWordDto;
import com.ksptooi.biz.document.model.epstdword.dto.GetEpStdWordListDto;
import com.ksptooi.biz.document.model.epstdword.dto.ImportEpStdWordDto;
import com.ksptooi.biz.document.model.epstdword.vo.GetEpStdWordDetailsVo;
import com.ksptooi.biz.document.model.epstdword.vo.GetEpStdWordListVo;
import com.ksptooi.biz.document.repository.EpStdWordRepository;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;

@Service
public class EpStdWordService {

    @Autowired
    private EpStdWordRepository repository;

    /**
     * 查询标准词列表
     *
     * @param dto 查询参数
     * @return 标准词列表
     */
    public PageResult<GetEpStdWordListVo> getEpStdWordList(GetEpStdWordListDto dto) {
        EpStdWordPo query = new EpStdWordPo();
        assign(dto, query);

        Page<EpStdWordPo> page = repository.getEpStdWordList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetEpStdWordListVo> vos = as(page.getContent(), GetEpStdWordListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增标准词
     *
     * @param dto 新增参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void addEpStdWord(AddEpStdWordDto dto) throws BizException {
        if (StringUtils.isBlank(dto.getTargetName())) {
            throw new BizException("英文简称不能为空");
        }

        // 检查英文简称是否重复
        EpStdWordPo existingWord = repository.getStdWordByTargetName(dto.getTargetName());
        if (existingWord != null) {
            throw new BizException("英文简称已存在，请使用其他名称");
        }

        EpStdWordPo insertPo = as(dto, EpStdWordPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑标准词
     *
     * @param dto 编辑参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editEpStdWord(EditEpStdWordDto dto) throws BizException {
        EpStdWordPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));

        if (StringUtils.isBlank(dto.getTargetName())) {
            throw new BizException("英文简称不能为空");
        }

        // 检查英文简称是否被其他记录使用
        EpStdWordPo existingWord = repository.getStdWordByTargetNameExcludeId(dto.getTargetName(), dto.getId());
        if (existingWord != null) {
            throw new BizException("英文简称已存在，请使用其他名称");
        }

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询标准词详情
     *
     * @param dto 查询参数
     * @return 标准词详情
     * @throws BizException 业务异常
     */
    public GetEpStdWordDetailsVo getEpStdWordDetails(CommonIdDto dto) throws BizException {
        EpStdWordPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在."));
        return as(po, GetEpStdWordDetailsVo.class);
    }

    /**
     * 删除标准词
     *
     * @param dto 删除参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeEpStdWord(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
        }
        if (!dto.isBatch()) {
            repository.deleteById(dto.getId());
        }
    }

    /**
     * 导入标准词
     *
     * @param data 导入数据
     * @return 导入结果
     * @throws BizException 业务异常
     */
    public int importEpStdWord(List<ImportEpStdWordDto> data) throws BizException {

        var addPos = new ArrayList<EpStdWordPo>();
        var removePos = new ArrayList<EpStdWordPo>();

        for (var item : data) {

            var addPo = as(item, EpStdWordPo.class);

            //检查英文简称是否重复
            var existingWord = repository.getStdWordByTargetName(addPo.getTargetName());

            if (existingWord != null) {
                removePos.add(existingWord);
            }

            addPos.add(addPo);
        }

        //保存新增标准词
        repository.saveAll(addPos);
        //删除重复标准词
        repository.deleteAll(removePos);

        return addPos.size();
    }
}