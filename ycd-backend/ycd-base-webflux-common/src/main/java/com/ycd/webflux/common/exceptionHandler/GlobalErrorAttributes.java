package com.ycd.webflux.common.exceptionHandler;


import com.ycd.common.Result;
import com.ycd.common.exception.BusinessException;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.LinkedHashMap;
import java.util.Map;


public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        return assembleError(request, includeStackTrace);
    }

    private Map<String, Object> assembleError(ServerRequest request, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        Throwable error = getError(request);
        if (error instanceof BusinessException) {
            errorAttributes.putAll(BeanMap.create(Result.error(error.getMessage())));
            return errorAttributes;
        }
        return super.getErrorAttributes(request, includeStackTrace);
    }
    //...有省略
}
