package com.ksptooi.biz.requestdebug.controller;

import com.ksptooi.biz.requestdebug.model.collection.dto.AddCollectionDto;
import com.ksptooi.biz.requestdebug.model.collection.dto.EditCollectionDto;
import com.ksptooi.biz.requestdebug.model.collection.dto.GetCollectionListDto;
import com.ksptooi.biz.requestdebug.model.collection.vo.GetCollectionDetailsVo;
import com.ksptooi.biz.requestdebug.model.collection.vo.GetCollectionListVo;
import com.ksptooi.biz.requestdebug.service.CollectionService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
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
@RequestMapping("/collection")
@Tag(name = "Collection", description = "")
@Slf4j
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @PostMapping("/getCollectionList")
    @Operation(summary = "列表查询")
    public PageResult<GetCollectionListVo> getCollectionList(@RequestBody @Valid GetCollectionListDto dto) throws Exception {
        return collectionService.getCollectionList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addCollection")
    public Result<String> addCollection(@RequestBody @Valid AddCollectionDto dto) throws Exception {
        collectionService.addCollection(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editCollection")
    public Result<String> editCollection(@RequestBody @Valid EditCollectionDto dto) throws Exception {
        collectionService.editCollection(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getCollectionDetails")
    public Result<GetCollectionDetailsVo> getCollectionDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetCollectionDetailsVo details = collectionService.getCollectionDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeCollection")
    public Result<String> removeCollection(@RequestBody @Valid CommonIdDto dto) throws Exception {
        collectionService.removeCollection(dto);
        return Result.success("操作成功");
    }

}
