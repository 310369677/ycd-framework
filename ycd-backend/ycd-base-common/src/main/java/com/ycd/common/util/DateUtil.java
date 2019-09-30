package com.ycd.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private DateUtil() {
        throw new UnsupportedOperationException("this is util");
    }

    public static String getNowStr(String paramPattern) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        if (SimpleUtil.isNotEmpty(pattern)) {
            pattern = paramPattern;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.format(new Date());
        } catch (Exception e) {
            //ignore
            return "";
        }

    }
}
