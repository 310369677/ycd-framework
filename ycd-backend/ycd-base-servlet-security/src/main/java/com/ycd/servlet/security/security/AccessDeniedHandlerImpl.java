package com.ycd.servlet.security.security;


import com.ycd.common.Result;
import com.ycd.common.constant.StatusCode;
import com.ycd.servlet.common.util.HttpServletResponseUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Result result = convertException(accessDeniedException);
        HttpServletResponseUtil.writeEntityInfo(response, result);
    }

    private Result convertException(AccessDeniedException denied) {
        Result result = Result.error("未转换异常");
        if (denied instanceof AuthorizationServiceException) {
            result = Result.result(StatusCode.NO_PRIVILEGE);
        }
        return result;
    }
}
