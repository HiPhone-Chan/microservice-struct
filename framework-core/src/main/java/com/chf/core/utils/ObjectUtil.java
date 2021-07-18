package com.chf.core.utils;

public class ObjectUtil {

    public static Integer getInteger(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        if (obj instanceof String) {
            return Integer.valueOf((String) obj);
        }
        return (Integer) obj;

    }

}
