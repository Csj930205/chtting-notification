package com.example.ssetest.service;

import com.example.ssetest.domain.Attach;
import com.example.ssetest.repository.AttachRepository;
import com.example.ssetest.util.AttachUtil;
import com.example.ssetest.util.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.DateFormatter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * @author sjChoi
 * @since 3/22/24
 */
@Service
@RequiredArgsConstructor
public class AttachService {

    private final AttachRepository attachRepository;

    private final AttachUtil attachUtil;

    @Transactional(readOnly = true)
    public void viewAttach(String uid) {
        Attach attach = attachRepository.findAttachByUid(Long.valueOf(uid));

        File file = new File(attachUtil.getUploadPath() + attach.getSavedDir() + "/" + attach.getSavedName());
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();
        response.setContentType(attach.getType());
        response.setContentLength((int) file.length());
        FileInputStream fileInputStream = null;
        FileChannel fileChannel = null;
        WritableByteChannel writableByteChannel = null;

        try {
            fileInputStream = new FileInputStream(file);
            fileChannel = fileInputStream.getChannel();
            writableByteChannel = Channels.newChannel(response.getOutputStream());
            fileChannel.transferTo(0, fileInputStream.available(), writableByteChannel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (fileChannel != null) fileChannel.close();
                if (writableByteChannel != null) writableByteChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public String insertAttach(MultipartFile uploadFile, Long uid, String tableType) {
        attachUtil.createDirectory(attachUtil.getSaveDir(tableType));
        String saveName = UUID.randomUUID().toString().replace("-", "");
        String displayName = uploadFile.getOriginalFilename();
        String createdBy = SecurityUtil.getCurrentMember().getUsername();
        String size = String.valueOf(uploadFile.getSize());
        String type = uploadFile.getContentType();

        Attach attach = Attach.builder()
                .tableId(uid)
                .size(size)
                .displayName(displayName)
                .savedName(saveName)
                .savedDir(attachUtil.getSaveDirDB(tableType))
                .type(type)
                .tableType(tableType)
                .createdBy(createdBy)
                .build();
        saveFile(attachUtil.getSaveDir(tableType), saveName, uploadFile);
        Attach insertAttach = attachRepository.save(attach);
        String url = "/apis/attaches/view/" + insertAttach.getUid();
        return url;
    }

    @Transactional
    public void saveFile(String uploadPath, String saveName, MultipartFile multipartFile) {
        File file = new File(uploadPath, saveName);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
