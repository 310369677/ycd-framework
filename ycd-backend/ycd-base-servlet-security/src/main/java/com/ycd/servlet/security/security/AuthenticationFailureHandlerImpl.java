package com.ycd.servlet.security.security;

import com.ycd.common.Result;
import com.ycd.servlet.common.util.HttpServletResponseUtil;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Result result = convertException2Result(exception);
        HttpServletResponseUtil.writeEntityInfo(response, result);
    }

    private Result convertException2Result(AuthenticationException e) {
        Result result = Result.error("授权未捕获异常");
        if (e instanceof UsernameNotFoundException) {
            result = Result.error("用户名或密码错误");
        } else if (e instanceof AuthenticationCredentialsNotFoundException) {
            result = Result.error("密码不能为空");
        } else if (e instanceof BadCredentialsException) {
            result = Result.error("用户名或密码错误");
        } else if (e instanceof InternalAuthenticationServiceException) {
            result = Result.error(e.getMessage());
        }
        return result;
    }
}
