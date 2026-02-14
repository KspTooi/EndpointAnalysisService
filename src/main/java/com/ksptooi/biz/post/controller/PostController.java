package com.ksptooi.biz.post.controller;

import com.ksptooi.commons.annotation.PrintLog;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.Result;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.ksptooi.biz.post.service.PostService;
import com.ksptooi.biz.post.model.dto.AddPostDto;
import com.ksptooi.biz.post.model.dto.EditPostDto;
import com.ksptooi.biz.post.model.dto.GetPostListDto;
import com.ksptooi.biz.post.model.vo.GetPostListVo;
import com.ksptooi.biz.post.model.vo.GetPostDetailsVo;


@RestController
@RequestMapping("/post")
@Tag(name = "post", description = "岗位表")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/getPostList")
    @Operation(summary ="列表查询")
    public PageResult<GetPostListVo> getPostList(@RequestBody @Valid GetPostListDto dto) throws Exception{
        return postService.getPostList(dto);
    }

    @Operation(summary ="新增")
    @PostMapping("/addPost")
    public Result<String> addPost(@RequestBody @Valid AddPostDto dto) throws Exception{
		postService.addPost(dto);
        return Result.success("新增成功");
    }

    @Operation(summary ="编辑")
    @PostMapping("/editPost")
    public Result<String> editPost(@RequestBody @Valid EditPostDto dto) throws Exception{
		postService.editPost(dto);
        return Result.success("修改成功");
    }

    @Operation(summary ="查询详情")
    @PostMapping("/getPostDetails")
    public Result<GetPostDetailsVo> getPostDetails(@RequestBody @Valid CommonIdDto dto) throws Exception{
        GetPostDetailsVo details = postService.getPostDetails(dto);
        if(details == null){
            return Result.error("无数据");
        }
        return Result.success(details);
    }

    @Operation(summary ="删除")
    @PostMapping("/removePost")
    public Result<String> removePost(@RequestBody @Valid CommonIdDto dto) throws Exception{
        postService.removePost(dto);
        return Result.success("操作成功");
    }

}
