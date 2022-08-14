package com.zhaoyg.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author zhao
 * @date 2022/8/12
 */
class MinioTest {

    @Test
    void testUpload() throws Exception {
        MinioClient minioClient =
                MinioClient.builder()
                        .httpClient(new OkHttpClient())
                        .endpoint("http://127.0.0.1:9000/")
                        .credentials("abcd", "zhao1215")
                        .build();
    }
}
