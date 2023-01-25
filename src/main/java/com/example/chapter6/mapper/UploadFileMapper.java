package com.example.chapter6.mapper;

import com.example.chapter6.model.UploadFileVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UploadFileMapper {

    void insertUploadFile(UploadFileVO uploadFileVO);

}
