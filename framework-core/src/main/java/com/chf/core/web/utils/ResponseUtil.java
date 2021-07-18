package com.chf.core.web.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.chf.core.utils.FileUtil;

public interface ResponseUtil {

    public static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse) {
        return wrapOrNotFound(maybeResponse, null);
    }

    public static <X> ResponseEntity<X> wrapOrNotFound(Optional<X> maybeResponse, HttpHeaders header) {
        return maybeResponse.map(response -> ResponseEntity.ok().headers(header).body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public static void downloadFile(File file, String fileName, HttpServletResponse response) {
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType(mediaType.toString());
        output(file, response);
    }

    public static void outputMedia(File file, String fileName, HttpServletResponse response) {
        MediaType mediaType = getMediaType(fileName);
        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        response.setContentType(mediaType.toString());
        output(file, response);
    }

    public static void output(File file, HttpServletResponse response) {
        try (OutputStream os = response.getOutputStream(); InputStream is = new FileInputStream(file);) {
            IOUtils.copy(is, os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MediaType getMediaType(String fileName) {
        String suffix = FileUtil.getSuffix(fileName);
        if (suffix != null) {
            switch (suffix) {
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "md":
                return MediaType.TEXT_MARKDOWN;
            case "mp4":
                return MediaType.valueOf("video/mp4");
            }
        }
        return null;
    }
}
