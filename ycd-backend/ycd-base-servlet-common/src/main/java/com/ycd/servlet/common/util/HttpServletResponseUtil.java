package com.ycd.servlet.common.util;

import com.alibaba.fastjson.JSON;
import com.ycd.common.Result;
import com.ycd.common.constant.StatusCode;
import com.ycd.common.exception.BusinessException;
import com.ycd.common.exception.TokenInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HttpServletResponseUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpServletResponseUtil.class);

    private HttpServletResponseUtil() {
        throw new UnsupportedOperationException("this is a util");
    }

    public static void writeEntityInfo(HttpServletResponse response, Object data) {
        Objects.requireNonNull(data);
        response.setStatus(HttpStatus.OK.value());
        response.addHeader("Content-type", "text/html;charset=UTF-8");
        byte[] resultBytes = JSON.toJSONString(data).getBytes(StandardCharsets.UTF_8);
        try {
            response.getOutputStream().write(resultBytes);
        } catch (IOException e) {
            log.error("写入消息到客户端失败", e);
        }
    }

    public static void writeException(HttpServletResponse response, Throwable e) {
        String message = "服务器内部异常";
        response.setStatus(HttpStatus.OK.value());
        response.addHeader("Content-type", "text/html;charset=UTF-8");
        if (e instanceof BusinessException) {
            message = e.getMessage();
            byte[] resultBytes = JSON.toJSONString(Result.error(message)).getBytes(StandardCharsets.UTF_8);
            writeBytes2Response(resultBytes, response);
            return;
        } else if (e instanceof TokenInvalidException) {
            byte[] resultBytes = JSON.toJSONString(Result.result(StatusCode.TOKEN_INVALID)).getBytes(StandardCharsets.UTF_8);
            writeBytes2Response(resultBytes, response);
            return;
        }
        //这是严重的异常
        log.error("服务端发生了非业务异常", e);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        byte[] resultBytes = JSON.toJSONString(Result.error(message)).getBytes(StandardCharsets.UTF_8);
        writeBytes2Response(resultBytes, response);
    }

    private static void writeBytes2Response(byte[] content, HttpServletResponse response) {
        try {
            response.getOutputStream().write(content);
        } catch (IOException ex) {
            log.error("获取输出流失败", ex);
            ;
        }
    }

}
