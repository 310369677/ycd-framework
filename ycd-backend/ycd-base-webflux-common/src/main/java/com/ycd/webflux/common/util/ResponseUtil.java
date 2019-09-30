package com.ycd.webflux.common.util;

import com.alibaba.fastjson.JSON;

import com.ycd.common.Result;
import com.ycd.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ResponseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseUtil.class);

    public static Mono<Void> writeException(ServerHttpResponse response, Throwable e) {
        String message = "服务器内部异常";
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().set("Content-type", "text/html;charset=UTF-8");
        if (e instanceof BusinessException) {
            message = e.getMessage();
            byte[] resultBytes = JSON.toJSONString(Result.error(message)).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(resultBytes);
            return response.writeWith(Mono.just(buffer));
        }
        //这是严重的异常
        LOGGER.error("服务端发生了非业务异常", e);
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        byte[] resultBytes = JSON.toJSONString(Result.error(message)).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(resultBytes);
        return response.writeWith(Mono.just(buffer));
    }


    public static Mono<Void> writeEntityInfo(ServerHttpResponse response, Object data) {
        Objects.requireNonNull(data);
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        byte[] resultBytes = JSON.toJSONString(data).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(resultBytes);
        return response.writeWith(Mono.just(buffer));
    }
}
