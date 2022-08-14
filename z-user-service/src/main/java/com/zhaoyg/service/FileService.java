package com.zhaoyg.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author zhao
 * @date 2022/8/12
 */
@Service
public interface FileService {


    void uploadFile(String bucketName, String fileName, InputStream inputStream);


}
