package com.chf.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MessageDigestUtils {

    private MessageDigestUtils() {
    }

    public static String md5Encode(String message) {
        return message == null ? null : byte2Hexstring(md5Encode(message.getBytes()));
    }

    public static byte[] md5Encode(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(data);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String sha1Encode(String data) {
        return data == null ? null : byte2Hexstring(sha1Encode(data.getBytes()));
    }

    public static byte[] sha1Encode(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(data);

            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static String byte2Hexstring(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int i = b & 0XFF;
            if (i < 0x10) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(i));
        }
        return sb.toString();
    }
}
