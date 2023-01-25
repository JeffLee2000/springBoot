package com.example.chapter6.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UploadFileVO {

    private int id;
    // 파일 명
    private String name;
    // 저장 경로
    private String filePath;
    // 파일 타입
    private String contentType;
    // 저장된 파일명
    private String saveFileName;
    private LocalDateTime redDate;

}
