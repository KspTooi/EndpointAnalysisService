package com.ksptool.bio.commons.aop;

import com.ksptooi.biz.audit.service.AuditErrorRcdService;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static com.ksptooi.biz.auth.service.SessionService.getSession;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 是否记录系统错误记录
     */
    @Value("${audit.record-error-rcd:true}")
    private boolean recordErrorRcd;

    @Autowired
    private AuditErrorRcdService auditErrorRcdService;

    /**
     * 处理 @Valid 注解校验失败抛出的异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        String errorMessage = String.join(", ", errors);
        // log.error("参数校验失败: {}", errorMessage);
        printEnhancedRequestLog(ex);

        if (recordErrorRcd) {
            // 审计模块记录系统错误记录
            var session = getSession();
            var userId = 0L;
            var userName = "无法获取";
            var errorCode = auditErrorRcdService.nextErrorCode("PARAM");

            if (session != null) {
                userId = session.getUserId();
            }

            auditErrorRcdService.addAuditErrorRcdAsync(errorCode, request.getRequestURI(), userId, userName, ex);
            return Result.error("处理该请求失败，请稍后重试，该错误已被审计系统记录。 错误代码: " + errorCode);
        }

        return Result.error(errorMessage);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BizException.class)
    public Result<Object> handleBizException(BizException ex) {
        log.error(ex.getMessage(), ex);
        return Result.error(ex);
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception ex, HttpServletRequest request) {
        log.error(ex.getMessage(), ex);
        printEnhancedRequestLog(ex);

        if (recordErrorRcd) {
            // 审计模块记录系统错误记录
            var session = getSession();
            var userId = 0L;
            var userName = "无法获取";
            var errorCode = auditErrorRcdService.nextErrorCode("INTERNAL");

            if (session != null) {
                userId = session.getUserId();
            }

            auditErrorRcdService.addAuditErrorRcdAsync(errorCode, request.getRequestURI(), userId, userName, ex);
            return Result.error("在处理您的请求时发生了一个内部错误，该错误已被审计系统记录，错误代码: " + errorCode);
        }

        return Result.internalError("系统内部错误", ex.getMessage());
    }

    /**
     * 打印增强型请求日志
     *
     * @param ex 异常
     */
    public void printEnhancedRequestLog(Exception ex) {
        var requestInfo = RequestLogFilter.getRequestInfo();
        if (requestInfo != null) {
            var appendMsg = "";
            if (StringUtils.isNotBlank(requestInfo.getGatewayRequestId())) {
                appendMsg = " 网关请求ID:" + requestInfo.getGatewayRequestId() + " ";
            }
            log.error("请求处理失败=>{}[{}]{}载荷:{} 原因:{}", requestInfo.getUri(), requestInfo.getMethod(), appendMsg,
                    requestInfo.getBodySupplier().get(), ex.getMessage(), ex);
        }
    }

}
