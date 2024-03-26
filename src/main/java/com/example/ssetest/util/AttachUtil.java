package com.example.ssetest.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author sjChoi
 * @since 3/22/24
 */
@Component
@Data
@Slf4j
@ConfigurationProperties(prefix = "spring.file-upload")
public class AttachUtil {

    /**
     * 파일업로드 경로
     */
    private String uploadPath;

    public String getSubDir() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        String dir = sdf.format(calendar.getTime());
        return dir;
    }

    public String getSaveDir(String tableName) {
        if (tableName == null || tableName.equals("")) {
            return null;
        }
        String saveDir = getUploadPath() + "/" + tableName + "/" + getSubDir();
        return saveDir;
    }

    public String getSaveDirDB(String tableName) {
        if (tableName == null || tableName.equals("")) {
            return null;
        }
        String saveDirDB = "/" + tableName + "/" + getSubDir();
        return saveDirDB;
    }

    public void createDirectory(String saveDir) {
        Path path = Paths.get(saveDir);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getFileExt(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        String contentType = null;

        switch (ext) {
            case "jpg" :
                contentType = "image/jpeg";
                break;
            case "png" :
                contentType = "image/png";
                break;
            case "gif" :
                contentType = "image/gif";
                break;
        }
        return contentType;
    }

}
