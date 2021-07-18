package com.chf.core.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public final class XmlUtil {

    private static final Logger log = LoggerFactory.getLogger(XmlUtil.class);

    private static ObjectMapper mapper;

    private XmlUtil() {
    }

    static {
        JacksonXmlModule xmlmodule = new JacksonXmlModule();
        xmlmodule.setDefaultUseWrapper(false);
        mapper = new XmlMapper(xmlmodule);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String fromObject(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Cannot convert to String", e);
        }
        return "";
    }

    public static <T> T readValue(String str, Class<T> cls) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        if (str.startsWith("%3C") || str.startsWith("%3c")) {
            str = CharsetUtil.urlDecode(str);
        }

        try {
            return mapper.readValue(str, cls);
        } catch (IOException e) {
            log.warn("Cannot convert to Object", e);
        }
        return null;
    }

    @SuppressWarnings({ "unchecked" })
    public static Map<Object, Object> readMapValue(String json) {
        return readValue(json, Map.class);
    }

    public static Element readXml(String xml) {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            return doc.getRootElement();
        } catch (DocumentException | UnsupportedEncodingException e) {
            log.warn("Cannot parse xml", e);
        }
        return null;
    }

}
