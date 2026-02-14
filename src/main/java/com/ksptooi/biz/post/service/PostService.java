package com.ksptooi.biz.post.service;

import com.ksptool.assembly.entity.web.PageResult;
import com.ksptool.assembly.entity.web.CommonIdDto;
import com.ksptool.assembly.entity.exception.BizException;
import static com.ksptool.entities.Entities.as;
import static com.ksptool.entities.Entities.assign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import java.util.Optional;
import com.ksptooi.biz.post.repository.PostRepository;
import com.ksptooi.biz.post.model.PostPo;
import com.ksptooi.biz.post.model.vo.GetPostListVo;
import com.ksptooi.biz.post.model.dto.GetPostListDto;
import com.ksptooi.biz.post.model.vo.GetPostDetailsVo;
import com.ksptooi.biz.post.model.dto.EditPostDto;
import com.ksptooi.biz.post.model.dto.AddPostDto;


@Service
public class PostService {

    @Autowired
    private PostRepository repository;

    public PageResult<GetPostListVo> getPostList(GetPostListDto dto){
        PostPo query = new PostPo();
        assign(dto,query);

        Page<PostPo> page = repository.getPostList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetPostListVo> vos = as(page.getContent(), GetPostListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addPost(AddPostDto dto){
        PostPo insertPo = as(dto,PostPo.class);
        repository.save(insertPo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void editPost(EditPostDto dto) throws BizException {
        PostPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    public GetPostDetailsVo getPostDetails(CommonIdDto dto) throws BizException {
        PostPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetPostDetailsVo.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removePost(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}