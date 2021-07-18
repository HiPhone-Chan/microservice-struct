package com.chf.core.utils;

import org.apache.commons.lang3.RandomStringUtils;

public final class RandomUtil {

    private static final int DEF_COUNT = 20;

    private RandomUtil() {
    }

    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
    }

    public static String generateValidateKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }
    
    public static String generateResetKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }
    
    public static String generateSmsCode() {
        return RandomStringUtils.randomNumeric(6);
    }
     
}
