package com.ksptool.bio.biz.assembly.service;

import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.bio.biz.assembly.model.datsource.DataSourcePo;
import com.ksptool.bio.biz.assembly.model.rawmodel.RawModelPo;
import com.ksptool.bio.biz.assembly.model.rawmodel.dto.AddRawModelDto;
import com.ksptool.bio.biz.assembly.model.rawmodel.dto.EditRawModelDto;
import com.ksptool.bio.biz.assembly.model.rawmodel.dto.GetRawModelDto;
import com.ksptool.bio.biz.assembly.model.rawmodel.vo.GetRawModelDetailsVo;
import com.ksptool.bio.biz.assembly.model.rawmodel.vo.GetRawModelListVo;
import com.ksptool.bio.biz.assembly.repository.OutModelOriginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;


@Service
public class OutModelOriginService {

    @Autowired
    private OutModelOriginRepository repository;


    /**
     * 查询原始模型列表
     *
     * @param dto 查询参数
     * @return 原始模型列表
     */
    public PageResult<GetRawModelListVo> getOutModelOriginList(GetRawModelDto dto) {
        RawModelPo query = new RawModelPo();
        assign(dto, query);

        List<RawModelPo> list = repository.getOutModelOriginList(query);
        if (list.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetRawModelListVo> vos = as(list, GetRawModelListVo.class);
        return PageResult.success(vos, list.size());
    }

    /**
     * 新增原始模型
     *
     * @param dto 新增参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOutModelOrigin(AddRawModelDto dto) {
        RawModelPo insertPo = as(dto, RawModelPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑原始模型
     *
     * @param dto 编辑参数
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editOutModelOrigin(EditRawModelDto dto) throws BizException {
        RawModelPo updatePo = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto, updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询原始模型详情
     *
     * @param dto 查询参数
     * @return 原始模型详情
     * @throws BizException 业务异常
     */
    public GetRawModelDetailsVo getOutModelOriginDetails(CommonIdDto dto) throws BizException {
        RawModelPo po = repository.findById(dto.getId())
                .orElseThrow(() -> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po, GetRawModelDetailsVo.class);
    }

    /**
     * 删除原始模型元素
     *
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


    /**
     * 查询数据源表字段
     *
     * @param dataSource 数据源
     * @return 数据源表字段 获取异常时直接返回null
     */
    public List<RawModelPo> getDataSourceTableFields(DataSourcePo dataSource, String tableName) {

        var drive = dataSource.getDrive();
        var url = dataSource.getUrl();
        var username = dataSource.getUsername();
        var password = dataSource.getPassword();
        var dbSchema = dataSource.getDbSchema();

        try {
            Class.forName(drive);
        } catch (ClassNotFoundException e) {
            return null;
        }

        if (tableName == null || tableName.isBlank()) {
            return null;
        }

        List<RawModelPo> result = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            DatabaseMetaData metaData = conn.getMetaData();

            // 查询主键列表
            Set<String> pkColumns = new HashSet<>();
            try (ResultSet pkRs = metaData.getPrimaryKeys(dbSchema, dbSchema, tableName)) {
                while (pkRs.next()) {
                    pkColumns.add(pkRs.getString("COLUMN_NAME"));
                }
            }

            try (ResultSet rs = metaData.getColumns(dbSchema, dbSchema, tableName, "%")) {
                int seq = 1;
                while (rs.next()) {
                    RawModelPo po = new RawModelPo();
                    po.setName(rs.getString("COLUMN_NAME"));
                    po.setKind(rs.getString("TYPE_NAME"));
                    int columnSize = rs.getInt("COLUMN_SIZE");
                    po.setLength(columnSize > 0 ? String.valueOf(columnSize) : null);
                    // IS_NULLABLE: YES 表示可为空, 主键或非空则 require=1
                    String nullable = rs.getString("IS_NULLABLE");
                    boolean isPk = pkColumns.contains(po.getName());
                    po.setRequire((isPk || "NO".equalsIgnoreCase(nullable)) ? 1 : 0);
                    po.setRemark(rs.getString("REMARKS"));
                    po.setSeq(seq++);
                    result.add(po);
                }
            }
        } catch (SQLException e) {
            return null;
        }

        return result;
    }


}