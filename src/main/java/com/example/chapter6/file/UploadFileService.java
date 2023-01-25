package com.example.chapter6.file;

import com.example.chapter6.mapper.FileMapMapper;
import com.example.chapter6.mapper.UploadFileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class UploadFileService {

    @Autowired
    private UploadFileMapper uploadFileMapper;
    @Autowired
    private FileMapMapper fileMapMapper;

    private final Path rootLocation;

    public UploadFileService(String uploadPath) {
        this.rootLocation = Paths.get(uploadPath);
    }

    public void store(MultipartFile file) throws Exception {

        if (file.isEmpty()) throw new Exception("파일 저장 실패");

        String saveFileName = UploadFile.fileSave(rootLocation.toString(), file);
        log.info("saveFileName - {}", saveFileName);

    }
}
