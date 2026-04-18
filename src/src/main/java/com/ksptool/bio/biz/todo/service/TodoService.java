package com.ksptool.bio.biz.todo.service;

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
import com.ksptool.bio.biz.todo.repository.TodoRepository;
import com.ksptool.bio.biz.todo.model.TodoPo;
import com.ksptool.bio.biz.todo.model.vo.GetTodoListVo;
import com.ksptool.bio.biz.todo.model.dto.GetTodoListDto;
import com.ksptool.bio.biz.todo.model.vo.GetTodoDetailsVo;
import com.ksptool.bio.biz.todo.model.dto.EditTodoDto;
import com.ksptool.bio.biz.todo.model.dto.AddTodoDto;


@Service
public class TodoService {

    @Autowired
    private TodoRepository repository;

    /**
     * 查询待办事项列表
     * @param dto 查询条件
     * @return 查询结果
     */
    public PageResult<GetTodoListVo> getTodoList(GetTodoListDto dto){
        TodoPo query = new TodoPo();
        assign(dto,query);

        Page<TodoPo> page = repository.getTodoList(query, dto.pageRequest());
        if (page.isEmpty()) {
            return PageResult.successWithEmpty();
        }

        List<GetTodoListVo> vos = as(page.getContent(), GetTodoListVo.class);
        return PageResult.success(vos, (int) page.getTotalElements());
    }

    /**
     * 新增待办事项
     * @param dto 新增条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void addTodo(AddTodoDto dto){
        TodoPo insertPo = as(dto,TodoPo.class);
        repository.save(insertPo);
    }

    /**
     * 编辑待办事项
     * @param dto 编辑条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void editTodo(EditTodoDto dto) throws BizException {
        TodoPo updatePo = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("更新失败,数据不存在或无权限访问."));

        assign(dto,updatePo);
        repository.save(updatePo);
    }

    /**
     * 查询待办事项详情
     * @param dto 查询条件
     * @return 查询结果
     * @throws BizException 业务异常
     */
    public GetTodoDetailsVo getTodoDetails(CommonIdDto dto) throws BizException {
        TodoPo po = repository.findById(dto.getId())
            .orElseThrow(()-> new BizException("查询详情失败,数据不存在或无权限访问."));
        return as(po,GetTodoDetailsVo.class);
    }

    /**
     * 删除待办事项
     * @param dto 删除条件
     * @throws BizException 业务异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeTodo(CommonIdDto dto) throws BizException {
        if (dto.isBatch()) {
            repository.deleteAllById(dto.getIds());
            return;
        }
        repository.deleteById(dto.getId());
    }

}
