package com.chf.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.chf.core.constants.ErrorCodeContants;
import com.chf.core.exception.ServiceException;

@SuppressWarnings("unchecked")
public class BeanUtil {

    public static <T> T castObject(Object obj, Class<T> cls) {
        if (cls.equals(obj.getClass())) {
            return (T) obj;
        }
        if (obj instanceof String) {
            return JsonUtil.readValue((String) obj, cls);
        }
        try {
            return JsonUtil.convertValue(obj, cls);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(ErrorCodeContants.DATA_FORMAT, "Format not right.");
        }
    }

    public static String sortString(Object pojo) {
        Map<String, Object> map = new TreeMap<>();
        Class<?> clz = pojo.getClass();

        if (pojo instanceof Map) {
            ((Map<String, Object>) pojo).forEach((k, v) -> {
                if (!ObjectUtils.isEmpty(k) && !ObjectUtils.isEmpty(v)) {
                    map.put(k, v);
                }
            });
        } else {
            for (Field field : clz.getDeclaredFields()) {
                String fieldName = field.getName();
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                String name = fieldName;
                if (jsonProperty != null) {
                    name = jsonProperty.value();
                }

                try {
                    String methodName = "get" + StringUtils.capitalize(fieldName);
                    Method getMethod = clz.getMethod(methodName);
                    Object value = getMethod.invoke(pojo);
                    if (!ObjectUtils.isEmpty(value)) {
                        map.put(name, value);
                    }
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value != null) {
                if (sb.length() > 0) {
                    sb.append('&');
                }
                sb.append(key).append('=').append(value);
            }
        }
        return sb.toString();
    }
}
