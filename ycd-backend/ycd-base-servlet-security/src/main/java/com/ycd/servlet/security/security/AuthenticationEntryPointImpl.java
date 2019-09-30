package com.ycd.servlet.security.security;


import com.ycd.common.Result;
import com.ycd.common.constant.StatusCode;
import com.ycd.servlet.common.util.HttpServletResponseUtil;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Result result = convertException(authException);
        HttpServletResponseUtil.writeEntityInfo(response, result);
    }

    private Result convertException(AuthenticationException e) {
        Result result = Result.error("未转换异常");
        if (e instanceof AuthenticationCredentialsNotFoundException) {
            result = Result.result(StatusCode.NO_AUTHTICATION);
        } else if (e instanceof InsufficientAuthenticationException) {
            result = Result.result(StatusCode.NO_AUTHTICATION);
        }
        return result;
    }
}
