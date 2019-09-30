package com.ycd.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtil {

    private ReflectUtil() {

    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                result.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }


    public static List<Method> getAllMethods(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        while (clazz != null) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                method.setAccessible(true);
                result.add(method);
            }
            clazz = clazz.getSuperclass();
        }
        return result;
    }

    public static List<Class<?>> getAllClass(Class<?> clazz) {
        List<Class<?>> result = new ArrayList<>();
        while (clazz != null) {
            result.add(clazz);
            clazz = clazz.getSuperclass();
        }
        return result;
    }
}
