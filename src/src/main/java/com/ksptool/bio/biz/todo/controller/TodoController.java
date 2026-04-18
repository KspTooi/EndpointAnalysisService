package com.ksptool.bio.biz.todo.controller;

import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import com.ksptool.bio.commons.annotation.PrintLog;

import com.ksptool.bio.biz.todo.service.TodoService;
import com.ksptool.bio.biz.todo.model.dto.AddTodoDto;
import com.ksptool.bio.biz.todo.model.dto.EditTodoDto;
import com.ksptool.bio.biz.todo.model.dto.GetTodoListDto;
import com.ksptool.bio.biz.todo.model.vo.GetTodoListVo;
import com.ksptool.bio.biz.todo.model.vo.GetTodoDetailsVo;

@PrintLog
@RestController
@RequestMapping("/todo")
@Tag(name = "待办事项", description = "待办事项")
@Slf4j
public class TodoController {

    @Autowired
    private TodoService todoService;

    @PreAuthorize("@auth.hasCode('qf:todo:view')")
    @PostMapping("/getTodoList")
    @Operation(summary ="查询待办事项列表")
    public PageResult<GetTodoListVo> getTodoList(@RequestBody @Valid GetTodoListDto dto) throws Exception{
        return todoService.getTodoList(dto);
    }

    @PreAuthorize("@auth.hasCode('qf:todo:add')")
    @Operation(summary ="新增待办事项")
    @PostMapping("/addTodo")
    public Result<String> addTodo(@RequestBody @Valid AddTodoDto dto) throws Exception{
		todoService.addTodo(dto);
        return Result.success("新增成功");
    }

    @PreAuthorize("@auth.hasCode('qf:todo:edit')")
    @Operation(summary ="编辑待办事项")
    @PostMapping("/editTodo")
    public Result<String> editTodo(@RequestBody @Valid EditTodoDto dto) throws Exception{
		todoService.editTodo(dto);
        return Result.success("修改成功");
    }

    @PreAuthorize("@auth.hasCode('qf:todo:view')")
    @Operation(summary ="查询待办事项详情")
    @PostMapping("/getTodoDetails")
    public Result<GetTodoDetailsVo> getTodoDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetTodoDetailsVo details = todoService.getTodoDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @PreAuthorize("@auth.hasCode('qf:todo:remove')")
    @Operation(summary ="删除待办事项")
    @PostMapping("/removeTodo")
    public Result<String> removeTodo(@RequestBody @Valid CommonIdDto dto) throws Exception{
        todoService.removeTodo(dto);
        return Result.success("操作成功");
    }

}
