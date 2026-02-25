package com.ksptool.bio.biz.drivespace.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptool.bio.biz.drivespace.service.DriveSpaceService;
import com.ksptool.bio.biz.drivespace.model.dto.AddDriveSpaceDto;
import com.ksptool.bio.biz.drivespace.model.dto.EditDriveSpaceDto;
import com.ksptool.bio.biz.drivespace.model.dto.GetDriveSpaceListDto;
import com.ksptool.bio.biz.drivespace.model.vo.GetDriveSpaceListVo;
import com.ksptool.bio.biz.drivespace.model.vo.GetDriveSpaceDetailsVo;


@RestController
@RequestMapping("/driveSpace")
@Tag(name = "driveSpace", description = "云盘空间")
@Slf4j
public class DriveSpaceController {

    @Autowired
    private DriveSpaceService driveSpaceService;

    @PostMapping("/getDriveSpaceList")
    @Operation(summary ="列表查询")
    public PageResult<GetDriveSpaceListVo> getDriveSpaceList(@RequestBody @Valid GetDriveSpaceListDto dto) throws Exception{
        return driveSpaceService.getDriveSpaceList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addDriveSpace")
    public Result<String> addDriveSpace(@RequestBody @Valid AddDriveSpaceDto dto) throws Exception{
		driveSpaceService.addDriveSpace(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editDriveSpace")
    public Result<String> editDriveSpace(@RequestBody @Valid EditDriveSpaceDto dto) throws Exception{
		driveSpaceService.editDriveSpace(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getDriveSpaceDetails")
    public Result<GetDriveSpaceDetailsVo> getDriveSpaceDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetDriveSpaceDetailsVo details = driveSpaceService.getDriveSpaceDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeDriveSpace")
    public Result<String> removeDriveSpace(@RequestBody @Valid CommonIdDto dto) throws Exception{
        driveSpaceService.removeDriveSpace(dto);
        return Result.success("操作成功");
    }

}
