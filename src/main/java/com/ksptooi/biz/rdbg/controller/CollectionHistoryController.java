package com.ksptooi.biz.rdbg.controller;

import com.ksptooi.biz.rdbg.model.collectionhistory.dto.AddCollectionHistoryDto;
import com.ksptooi.biz.rdbg.model.collectionhistory.dto.EditCollectionHistoryDto;
import com.ksptooi.biz.rdbg.model.collectionhistory.dto.GetCollectionHistoryListDto;
import com.ksptooi.biz.rdbg.model.collectionhistory.vo.GetCollectionHistoryDetailsVo;
import com.ksptooi.biz.rdbg.model.collectionhistory.vo.GetCollectionHistoryListVo;
import com.ksptooi.biz.rdbg.service.CollectionHistoryService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.utils.web.CommonIdDto;
import com.ksptooi.commons.utils.web.PageResult;
import com.ksptooi.commons.utils.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PrintLog
@RestController
@RequestMapping("/collectionHistory")
@Tag(name = "CollectionHistory", description = "")
@Slf4j
public class CollectionHistoryController {

    @Autowired
    private CollectionHistoryService collectionHistoryService;

    @PostMapping("/getCollectionHistoryList")
    @Operation(summary = "列表查询")
    public PageResult<GetCollectionHistoryListVo> getCollectionHistoryList(@RequestBody @Valid GetCollectionHistoryListDto dto) throws Exception {
        return collectionHistoryService.getCollectionHistoryList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addCollectionHistory")
    public Result<String> addCollectionHistory(@RequestBody @Valid AddCollectionHistoryDto dto) throws Exception {
        collectionHistoryService.addCollectionHistory(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editCollectionHistory")
    public Result<String> editCollectionHistory(@RequestBody @Valid EditCollectionHistoryDto dto) throws Exception {
        collectionHistoryService.editCollectionHistory(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getCollectionHistoryDetails")
    public Result<GetCollectionHistoryDetailsVo> getCollectionHistoryDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetCollectionHistoryDetailsVo details = collectionHistoryService.getCollectionHistoryDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeCollectionHistory")
    public Result<String> removeCollectionHistory(@RequestBody @Valid CommonIdDto dto) throws Exception {
        collectionHistoryService.removeCollectionHistory(dto);
        return Result.success("操作成功");
    }

}
