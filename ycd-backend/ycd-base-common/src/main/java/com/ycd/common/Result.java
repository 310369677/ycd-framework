package com.ycd.common;


import com.ycd.common.constant.StatusCode;

import java.io.Serializable;

public class Result<T> implements Serializable {


    private static final long serialVersionUID = -2162723948599455786L;

    private Result() {

    }

    public Result(String code, T data) {
        this.code = code;
        this.data = data;
    }


    private String code;

    private T data;


    public static Result<String> ok() {
        return result(StatusCode.OK);
    }


    public static <T> Result<T> ok(T data) {
        return new Result<T>(StatusCode.OK.getCode(), data);
    }

    public static Result<String> error() {
        return result(StatusCode.ERROR);
    }

    public static <T> Result<T> error(T data) {
        return new Result<T>(StatusCode.ERROR.getCode(), data);
    }

    public static Result<String> result(StatusCode statusCode) {
        return new Result<>(statusCode.getCode(), statusCode.getDesc());
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "Result{" +
                "code='" + code + '\'' +
                ", data=" + data +
                '}';
    }

    public static void main(String[] args) {
        System.out.println(Result.error("你好"));
    }
}
