package com.chf.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import net.coobird.thumbnailator.tasks.UnsupportedFormatException;

public class ImageUtil {

    public static void compressFile(File file) {
        compressFiles(file);
    }

    public static void compressFiles(File... files) {
        try {
            Thumbnails.of(files).size(100, 50).toFiles(new Rename() {
                @Override
                public String apply(String fileName, ThumbnailParameter param) {
                    return appendSuffix(fileName, "-mini");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFormatName(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            ImageInputStream iis = ImageIO.createImageInputStream(fis);

            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                throw new UnsupportedFormatException(UnsupportedFormatException.UNKNOWN,
                        "No suitable ImageReader found for source data.");
            }

            ImageReader reader = readers.next();
            reader.setInput(iis);
            return reader.getFormatName();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

}
