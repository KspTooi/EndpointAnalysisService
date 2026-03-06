package com.ksptool.bio.biz.gen.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.gen.model.datsource.DataSourcePo;
import com.ksptool.bio.biz.gen.model.datsource.dto.AddDataSourceDto;
import com.ksptool.bio.biz.gen.model.datsource.dto.EditDataSourceDto;
import com.ksptool.bio.biz.gen.model.datsource.dto.GetDataSourceListDto;
import com.ksptool.bio.biz.gen.model.datsource.vo.GetDataSourceDetailsVo;
import com.ksptool.bio.biz.gen.model.datsource.vo.GetDataSourceListVo;
import com.ksptool.bio.biz.gen.repository.DataSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class DataSourceService {

    @Autowired
    private DataSourceRepository repository;

    /**
     * 查询数据源列表
     * @param dto 查询参数
     * @return 数据源列表
     */
    public PageResult<GetDataSourceListVo> getDataSourceList(GetDataSourceListDto dto) {
        DataSourcePo query = new DataSourcePo();
        assign(dto, query);

        Page<DataSourcePo> page = repository.getDataSourceList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetDataSourceListVo> vos = as(page.getContent(), GetDataSourceListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增数据源
     * @param dto 新增参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addDataSource(AddDataSourceDto dto) {
        DataSourcePo insertPo = as(dto, DataSourcePo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑数据源
     * @param dto 编辑参数
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void editDataSource(EditDataSourceDto dto) throws BizException {
        DataSourcePo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询数据源详情
     * @param dto 查询参数
     * @return 数据源详情
     * @throws BizException
     */
    public GetDataSourceDetailsVo getDataSourceDetails(CommonIdDto dto) throws BizException {
        DataSourcePo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetDataSourceDetailsVo.class);
    }

    /**
     * 删除数据源
     * @param dto 删除参数
     * @throws BizException
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeDataSource(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}