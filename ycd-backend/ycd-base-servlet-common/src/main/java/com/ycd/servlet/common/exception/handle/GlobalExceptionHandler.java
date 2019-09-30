package com.ycd.servlet.common.exception.handle;


import com.ycd.common.Result;
import com.ycd.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(code = HttpStatus.OK)
    public Result handleCustomException(Exception e) {
        StringBuilder result = new StringBuilder();
        if (e instanceof BusinessException) {
            return Result.error(e.getMessage());
        } else if (e instanceof BindException) {
            BindException bindException = (BindException) e;
            int count = 1;
            for (FieldError fieldErro : bindException.getBindingResult().getFieldErrors()) {
                //result.append("【").append(fieldErro.getField()).append(":").append(fieldErro.getDefaultMessage()).append("】");
                result.append(count++).append(" : ").append(fieldErro.getDefaultMessage());
            }
        } else if (e instanceof WebExchangeBindException) {
            WebExchangeBindException bindException = (WebExchangeBindException) e;
            int count = 1;
            for (FieldError fieldErro : bindException.getBindingResult().getFieldErrors()) {
                //result.append("【").append(fieldErro.getField()).append(":").append(fieldErro.getDefaultMessage()).append("】");
                result.append(count++).append(" : ").append(fieldErro.getDefaultMessage()).append("\n");
            }
        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) e;
            for (FieldError fieldErro : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
                result.append("【").append(fieldErro.getField()).append(":").append(fieldErro.getDefaultMessage()).append("】");
            }
        }
        if (result.length() > 0) {
            return Result.error(result.toString());
        }
        logger.error("服务器产生了非业务异常", e);
        return Result.error("服务器内部异常");
    }
}
