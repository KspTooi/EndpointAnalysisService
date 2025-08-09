package com.ksptooi.commons.utils.web;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.ksptool.entities.Entities.as;

/**
 * 通用分页视图响应对象
 * @param <T> 数据行的类型
 */
@Data
public class RestPageableView<T> {

    /**
     * 数据行列表
     */
    private List<T> data;

    /**
     * 总记录数
     */
    private Integer total;


    public RestPageableView() {
    }

    public RestPageableView(List<T> data, Long total) {
        this.data = data;
        this.total = Math.toIntExact(total);
    }

    public RestPageableView(Page<?> page, List<T> data) {
        this.data = data;
        this.total = Math.toIntExact(page.getTotalElements());
    }

    public RestPageableView(Page<T> page) {
        this.data = page.getContent();
        this.total = Math.toIntExact(page.getTotalElements());
    }

    public RestPageableView(Page<?> page, Class<T> targetClass) {
        this.data = as(page.getContent(), targetClass);
        this.total = Math.toIntExact(page.getTotalElements());
    }


}
