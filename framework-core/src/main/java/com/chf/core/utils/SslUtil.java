package com.chf.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

public class SslUtil {

    public static HttpComponentsClientHttpRequestFactory sslRequestFactory(String certPath, String pwd) {
        try (FileInputStream instream = new FileInputStream(new File(certPath))) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(instream, pwd.toCharArray());
            SSLContext sslcontext = SSLContextBuilder.create().loadKeyMaterial(keyStore, pwd.toCharArray()).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" },
                    null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            return new HttpComponentsClientHttpRequestFactory(httpclient);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
