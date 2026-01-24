package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.dept.dto.AddDeptDto;
import com.ksptooi.biz.core.model.dept.dto.EditDeptDto;
import com.ksptooi.biz.core.model.dept.dto.GetDeptListDto;
import com.ksptooi.biz.core.model.dept.vo.GetDeptDetailsVo;
import com.ksptooi.biz.core.model.dept.vo.GetDeptListVo;
import com.ksptooi.biz.core.service.DeptService;
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
@RequestMapping("/dept")
@Tag(name = "Dept", description = "部门表")
@Slf4j
public class DeptController {

    @Autowired
    private DeptService deptService;

    @PostMapping("/getDeptList")
    @Operation(summary = "列表查询")
    public PageResult<GetDeptListVo> getDeptList(@RequestBody @Valid GetDeptListDto dto) throws Exception {
        return deptService.getDeptList(dto);
    }

    @Operation(summary = "新增")
    @PostMapping("/addDept")
    public Result<String> addDept(@RequestBody @Valid AddDeptDto dto) throws Exception {
        deptService.addDept(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑")
    @PostMapping("/editDept")
    public Result<String> editDept(@RequestBody @Valid EditDeptDto dto) throws Exception {
        deptService.editDept(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询详情")
    @PostMapping("/getDeptDetails")
    public Result<GetDeptDetailsVo> getDeptDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetDeptDetailsVo details = deptService.getDeptDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除")
    @PostMapping("/removeDept")
    public Result<String> removeDept(@RequestBody @Valid CommonIdDto dto) throws Exception {
        deptService.removeDept(dto);
        return Result.success("操作成功");
    }

}
