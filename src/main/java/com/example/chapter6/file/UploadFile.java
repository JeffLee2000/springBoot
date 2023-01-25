package com.example.chapter6.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Slf4j
public class UploadFile {

    public static String fileSave(String uploadPath, MultipartFile file) {
        File uploadPathDir = new File(uploadPath);

        if (!uploadPathDir.exists()) uploadPathDir.mkdir();

        // 임의 생성된 파일명
        String generatedId = UUID.randomUUID().toString();

        // 파일명과 확장명
        String originalFileName = file.getOriginalFilename();
        String fileExtension = extractFileExtension(originalFileName);
        String saveFileName = generatedId + "." + fileExtension;

        log.info(saveFileName);

        return "";

    }

    // 확장자 추출
    public static String extractFileExtension(String fileName) {
        int dot = fileName.lastIndexOf(".");
        if (-1 != dot && fileName.length() - 1 > dot) {
            return fileName.substring(dot + 1);
        } else {
            return "";
        }
    }
}
