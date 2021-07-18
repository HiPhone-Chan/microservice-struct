package com.chf.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class CharsetUtil {

    public final static String UTF_8 = "UTF-8";
    public final static String ISO_8859_1 = "ISO-8859-1";

    public static String convert2iso_8859_1(String content) {
        try {
            return new String(content.getBytes(UTF_8), ISO_8859_1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String convertFromiso_8859_1(String content) {
        try {
            return new String(content.getBytes(ISO_8859_1), UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

}
