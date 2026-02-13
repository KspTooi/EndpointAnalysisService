package com.ksptooi.biz.core.controller;

import com.ksptooi.biz.core.model.user.dto.*;
import com.ksptooi.biz.core.model.user.vo.GetUserDetailsVo;
import com.ksptooi.biz.core.model.user.vo.GetUserListVo;
import com.ksptooi.biz.core.service.MenuService;
import com.ksptooi.biz.core.service.UserService;
import com.ksptooi.commons.annotation.PrintLog;
import com.ksptooi.commons.dataprocess.ImportWizard;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@PrintLog
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "用户管理")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private MenuService menuService;

    @Operation(summary = "获取用户列表")
    @PostMapping("getUserList")
    public PageResult<GetUserListVo> getUserList(@RequestBody @Valid GetUserListDto dto) {
        return service.getUserList(dto);
    }

    @Operation(summary = "获取用户详情")
    @PostMapping("getUserDetails")
    public Result<GetUserDetailsVo> getUserDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        return Result.success(service.getUserDetails(dto.getId()));
    }

    @Operation(summary = "新增用户")
    @PrintLog(sensitiveFields = "password")
    @PostMapping("addUser")
    public Result<String> addUser(@RequestBody @Valid AddUserDto dto) throws Exception {
        service.addUser(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑用户")
    @PrintLog(sensitiveFields = "password")
    @PostMapping("editUser")
    public Result<String> editUser(@RequestBody @Valid EditUserDto dto) throws Exception {
        service.editUser(dto);
        menuService.clearUserMenuTreeCacheByUserId(dto.getId());
        return Result.success("修改成功");
    }

    @Operation(summary = "删除用户")
    @PostMapping("removeUser")
    public Result<String> removeUser(@RequestBody @Valid CommonIdDto dto) throws Exception {
        service.removeUser(dto.getId());
        menuService.clearUserMenuTreeCacheByUserId(dto.getId());
        return Result.success("success");
    }


    @Operation(summary = "导入用户")
    @PostMapping("importUser")
    public Result<String> importUser(@RequestParam("file") MultipartFile file) throws Exception {

        var iw = new ImportWizard<>(file, ImportUserDto.class);
        iw.transfer();

        var errors = iw.validate();

        if (StringUtils.isNotBlank(errors)) {
            return Result.error(errors);
        }

        var data = iw.getData();
        var count = service.importUser(data);
        return Result.success("导入成功,已导入数据:" + count + "条", null);
    }

    @Operation(summary = "批量编辑用户")
    @PostMapping("batchEditUser")
    public Result<String> batchEditUser(@RequestBody @Valid BatchEditUserDto dto) throws Exception {

        //校验变更部门ID
        if (dto.getKind() == 3 && dto.getDeptId() == null) {
            return Result.error("变更部门ID不能为空");
        }

        var count = service.batchEditUser(dto);

        //清除用户菜单缓存
        for (Long uid : dto.getIds()) {
            menuService.clearUserMenuTreeCacheByUserId(uid);
        }

        return Result.success("批量操作成功,已操作数据:" + count + "条");
    }


}
