package com.ksptooi.commons.aop;

import com.ksptooi.commons.exception.BizException;
import com.ksptooi.commons.utils.web.Result;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 @Valid 注解校验失败抛出的异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        String errorMessage = String.join(", ", errors);
        //log.error("参数校验失败: {}", errorMessage);
        printEnhancedRequestLog(ex);
        return Result.error(errorMessage);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BizException.class)
    public Result<Object> handleBizException(BizException ex) {
        log.error(ex.getMessage(),ex);
        return Result.error(ex);
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        printEnhancedRequestLog(ex);
        return Result.internalError("系统内部错误", ex.getMessage());
    }



    /**
     * 打印增强型请求日志
     * @param ex 异常
     */
    public void printEnhancedRequestLog(Exception ex){
        var requestInfo = RequestLogFilter.getRequestInfo();
        if(requestInfo != null){
            var appendMsg = "";
            if(StringUtils.isNotBlank(requestInfo.getGatewayRequestId())){
                appendMsg = " 网关请求ID:" + requestInfo.getGatewayRequestId() + " ";
            }
            log.error("请求处理失败=>{}[{}]{}载荷:{} 原因:{}",requestInfo.getUri(),requestInfo.getMethod(),appendMsg,requestInfo.getBodySupplier().get(),ex.getMessage(),ex);
        }
    }




}
