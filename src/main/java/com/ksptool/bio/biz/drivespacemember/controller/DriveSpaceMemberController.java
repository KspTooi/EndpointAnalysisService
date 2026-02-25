package com.ksptool.bio.biz.drivespacemember.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptool.bio.biz.drivespacemember.service.DriveSpaceMemberService;
import com.ksptool.bio.biz.drivespacemember.model.dto.AddDriveSpaceMemberDto;
import com.ksptool.bio.biz.drivespacemember.model.dto.EditDriveSpaceMemberDto;
import com.ksptool.bio.biz.drivespacemember.model.dto.GetDriveSpaceMemberListDto;
import com.ksptool.bio.biz.drivespacemember.model.vo.GetDriveSpaceMemberListVo;
import com.ksptool.bio.biz.drivespacemember.model.vo.GetDriveSpaceMemberDetailsVo;


@RestController
@RequestMapping("/driveSpaceMember")
@Tag(name = "driveSpaceMember", description = "云盘空间成员")
@Slf4j
public class DriveSpaceMemberController {

    @Autowired
    private DriveSpaceMemberService driveSpaceMemberService;

    @PostMapping("/getDriveSpaceMemberList")
    @Operation(summary ="列表查询")
    public PageResult<GetDriveSpaceMemberListVo> getDriveSpaceMemberList(@RequestBody @Valid GetDriveSpaceMemberListDto dto) throws Exception{
        return driveSpaceMemberService.getDriveSpaceMemberList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addDriveSpaceMember")
    public Result<String> addDriveSpaceMember(@RequestBody @Valid AddDriveSpaceMemberDto dto) throws Exception{
		driveSpaceMemberService.addDriveSpaceMember(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editDriveSpaceMember")
    public Result<String> editDriveSpaceMember(@RequestBody @Valid EditDriveSpaceMemberDto dto) throws Exception{
		driveSpaceMemberService.editDriveSpaceMember(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getDriveSpaceMemberDetails")
    public Result<GetDriveSpaceMemberDetailsVo> getDriveSpaceMemberDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetDriveSpaceMemberDetailsVo details = driveSpaceMemberService.getDriveSpaceMemberDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removeDriveSpaceMember")
    public Result<String> removeDriveSpaceMember(@RequestBody @Valid CommonIdDto dto) throws Exception{
        driveSpaceMemberService.removeDriveSpaceMember(dto);
        return Result.success("操作成功");
    }

}
