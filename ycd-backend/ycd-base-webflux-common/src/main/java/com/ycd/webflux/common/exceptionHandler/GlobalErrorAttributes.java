package com.ycd.webflux.common.exceptionHandler;


import com.ycd.common.Result;
import com.ycd.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.LinkedHashMap;
import java.util.Map;


public class GlobalErrorAttributes extends DefaultErrorAttributes {

    private static final Logger logger = LoggerFactory.getLogger(GlobalErrorAttributes.class);

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        return assembleError(request, includeStackTrace);
    }

    private Map<String, Object> assembleError(ServerRequest request, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        Throwable error = getError(request);
        if (error instanceof BusinessException) {
            logger.debug("业务异常", error);
            errorAttributes.putAll(BeanMap.create(Result.error(error.getMessage())));
            return errorAttributes;
        }
        return super.getErrorAttributes(request, includeStackTrace);
    }
    //...有省略
}
