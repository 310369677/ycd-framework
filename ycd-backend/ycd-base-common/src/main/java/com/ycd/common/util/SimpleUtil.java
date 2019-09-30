package com.ycd.common.util;

import com.ycd.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class SimpleUtil {


    private SimpleUtil() {
        throw new UnsupportedOperationException("this is a util");
    }


    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUtil.class);

    /**
     * 判断对象是否为空
     *
     * @param t   对象
     * @param <T> 泛型
     * @return 结果
     */
    public static <T> boolean isEmpty(T t) {
        if (t == null) {
            return true;
        }

        if (t instanceof String) {
            return ((String) t).trim().length() == 0;
        } else if (t instanceof Map) {
            return ((Map) t).isEmpty();
        } else if (t instanceof Collection) {
            return ((Collection) t).isEmpty();
        } else if (t.getClass().isArray()) {
            return Array.getLength(t) == 0;
        }
        return false;
    }


    /**
     * 判断一个对象不为空
     *
     * @param t   对象
     * @param <T> 泛型
     * @return 结果
     */
    public static <T> boolean isNotEmpty(T t) {
        return !isEmpty(t);
    }


    /**
     * 路径的拼接
     *
     * @param paths 路劲
     * @return 拼接好的路劲
     */
    public static String pathJoin(String... paths) {
        StringBuilder result = new StringBuilder("");
        for (String path : paths) {
            String copyPath = path;
            if (path == null) {
                continue;
            }
            path = path.replace("\\", "/");
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            if ((result.length() == 0) && copyPath.startsWith("/")) {
                result.append("/");
            }
            result.append(path).append("/");
        }
        return result.substring(0, result.length() - 1);  //去掉最后的斜杠
    }


    public static void trueAndThrows(boolean expres, String msg) {
        if (!expres) {
            return;
        }
        throw new BusinessException(msg);
    }

    public static <T> void assertNotEmpty(T t, String msg) {
        trueAndThrows(SimpleUtil.isEmpty(t), msg);
    }


    public static BusinessException newBusinessException(String param) {
        String message = "服务器内部错误";
        if (SimpleUtil.isNotEmpty(param)) {
            message = param;
        }
        return new BusinessException(message);
    }

    public static <T> T safeVal(T val,T defaultVal){
        if(SimpleUtil.isNotEmpty(val)){
            return val;
        }
        return defaultVal;
    }


}
