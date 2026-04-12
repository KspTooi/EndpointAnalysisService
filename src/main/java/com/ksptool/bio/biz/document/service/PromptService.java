package com.ksptool.bio.biz.document.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.document.model.prompt.PromptPo;
import com.ksptool.bio.biz.document.model.prompt.dto.AddPromptDto;
import com.ksptool.bio.biz.document.model.prompt.dto.EditPromptDto;
import com.ksptool.bio.biz.document.model.prompt.dto.GetPromptListDto;
import com.ksptool.bio.biz.document.model.prompt.vo.GetPromptDetailsVo;
import com.ksptool.bio.biz.document.model.prompt.vo.GetPromptListVo;
import com.ksptool.bio.biz.document.repository.PromptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class PromptService {

    @Autowired
    private PromptRepository repository;

    /**
     * 查询提示词列表
     *
     * @param dto 查询条件
     * @return 查询结果
     */
    public PageResult<GetPromptListVo> getPromptList(GetPromptListDto dto) {
        PromptPo query = new PromptPo();
        assign(dto, query);

        Page<PromptPo> page = repository.getPromptList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetPromptListVo> vos = as(page.getContent(), GetPromptListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增提示词
     *
     * @param dto 新增条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void addPrompt(AddPromptDto dto) {
        PromptPo insertPo = as(dto, PromptPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑提示词
     *
     * @param dto 编辑条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editPrompt(EditPromptDto dto) throws BizException {
        PromptPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询提示词详情
     *
     * @param dto 查询条件
     * @return 查询结果
     * @throws BizException 业务异常
     */
    public GetPromptDetailsVo getPromptDetails(CommonIdDto dto) throws BizException {
        PromptPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetPromptDetailsVo.class);
    }

    /**
     * 删除提示词
     *
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removePrompt(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}
