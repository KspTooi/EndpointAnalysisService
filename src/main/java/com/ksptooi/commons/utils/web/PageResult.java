package com.ksptooi.commons.utils.web;

import com.ksptooi.commons.exception.BizException;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.ksptool.entities.Entities.as;

@Data
public class PageResult<T> {

    // 0-正常，1-业务异常，2-内部服务器错误，3-请求异常
    private final int code;

    //描述信息
    private final String message;

    //返回数据集合
    private final Collection<T> data;

    //总记录数
    private final Integer total;

    protected PageResult(int code, String message, Collection<T> data, Integer total) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.total = total;
    }

    public PageResult(List<T> rows, Long count) {
        this.data = rows;
        this.total = Math.toIntExact(count);
        this.message = "success";
        this.code = 0;
    }

    public PageResult(Page<?> page, List<T> rows) {
        this.data = rows;
        this.total = Math.toIntExact(page.getTotalElements());
        this.message = "success";
        this.code = 0;
    }

    public PageResult(Page<T> page) {
        this.data = page.getContent();
        this.total = Math.toIntExact(page.getTotalElements());
        this.message = "success";
        this.code = 0;
    }

    public PageResult(Page<?> page, Class<T> targetClass) {
        this.data = as(page.getContent(), targetClass);
        this.total = Math.toIntExact(page.getTotalElements());
        this.message = "success";
        this.code = 0;
    }

    // 业务正常
    public static <T> PageResult<T> success(Collection<T> data, Integer total) {
        return new PageResult<>(0, "success", data, total);
    }

    public static <T> PageResult<T> success(Collection<T> data, Long total) {
        return new PageResult<>(0, "success", data, Integer.parseInt(total.toString()));
    }

    // 业务正常但无数据
    public static <T> PageResult<T> successWithEmpty() {
        return new PageResult<>(0, "无数据", Collections.emptyList(), 0);
    }

    public static <T> PageResult<T> success(String message, Collection<T> data, Integer total) {
        return new PageResult<>(0, message, data, total);
    }

    // 业务异常
    public static <T> PageResult<T> error(String message) {
        return new PageResult<>(1, message, Collections.emptyList(), 0);
    }

    // 业务异常
    public static <T> PageResult<T> error(BizException e) {
        return new PageResult<>(1, e.getMessage(), Collections.emptyList(), 0);
    }

    public static <T> PageResult<T> error(int code, String message) {
        return new PageResult<>(code, message, Collections.emptyList(), 0);
    }

    // 内部服务器错误
    public static <T> PageResult<T> internalError(String message, Object throwable) {
        return new PageResult<>(2, message, Collections.emptyList(), 0);
    }
}
