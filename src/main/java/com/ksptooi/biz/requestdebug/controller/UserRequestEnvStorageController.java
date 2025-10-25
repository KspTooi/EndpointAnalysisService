package com.ksptooi.biz.requestdebug.controller;

import com.ksptooi.biz.requestdebug.model.userrequestenvstorage.dto.AddUserRequestEnvStorageDto;
import com.ksptooi.biz.requestdebug.model.userrequestenvstorage.dto.EditUserRequestEnvStorageDto;
import com.ksptooi.biz.requestdebug.model.userrequestenvstorage.dto.GetUserRequestEnvStorageListDto;
import com.ksptooi.biz.requestdebug.model.userrequestenvstorage.vo.GetUserRequestEnvStorageDetailsVo;
import com.ksptooi.biz.requestdebug.model.userrequestenvstorage.vo.GetUserRequestEnvStorageListVo;
import com.ksptooi.biz.requestdebug.service.UserRequestEnvStorageService;
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
@RequestMapping("/userRequestEnvStorage")
@Tag(name = "UserRequestEnvStorage", description = "环境共享存储")
@Slf4j
public class UserRequestEnvStorageController {

    @Autowired
    private UserRequestEnvStorageService userRequestEnvStorageService;

    @PostMapping("/getUserRequestEnvStorageList")
    @Operation(summary = "查询环境共享存储列表")
    public PageResult<GetUserRequestEnvStorageListVo> getUserRequestEnvStorageList(@RequestBody @Valid GetUserRequestEnvStorageListDto dto) throws Exception {
        return userRequestEnvStorageService.getUserRequestEnvStorageList(dto);
    }

    @Operation(summary = "新增环境共享存储")
    @PostMapping("/addUserRequestEnvStorage")
    public Result<String> addUserRequestEnvStorage(@RequestBody @Valid AddUserRequestEnvStorageDto dto) throws Exception {
        userRequestEnvStorageService.addUserRequestEnvStorage(dto);
        return Result.success("新增成功");
    }

    @Operation(summary = "编辑环境共享存储")
    @PostMapping("/editUserRequestEnvStorage")
    public Result<String> editUserRequestEnvStorage(@RequestBody @Valid EditUserRequestEnvStorageDto dto) throws Exception {
        userRequestEnvStorageService.editUserRequestEnvStorage(dto);
        return Result.success("修改成功");
    }

    @Operation(summary = "查询环境共享存储详情")
    @PostMapping("/getUserRequestEnvStorageDetails")
    public Result<GetUserRequestEnvStorageDetailsVo> getUserRequestEnvStorageDetails(@RequestBody @Valid CommonIdDto dto) throws Exception {
        GetUserRequestEnvStorageDetailsVo details = userRequestEnvStorageService.getUserRequestEnvStorageDetails(dto);
        if (details == null) {
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary = "删除环境共享存储")
    @PostMapping("/removeUserRequestEnvStorage")
    public Result<String> removeUserRequestEnvStorage(@RequestBody @Valid CommonIdDto dto) throws Exception {
        userRequestEnvStorageService.removeUserRequestEnvStorage(dto);
        return Result.success("操作成功");
    }

}
