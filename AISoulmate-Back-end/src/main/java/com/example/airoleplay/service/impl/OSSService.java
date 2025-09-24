package com.example.airoleplay.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectResult;

@Service
public class OSSService {

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    /**
     * Upload base64 encoded image to OSS
     * @param base64Image Base64 encoded image string (without prefix like "data:image/jpeg;base64,")
     * @return The URL of the uploaded image
     */
    public String uploadImage(String base64Image) {
        try {
            // Decode base64 string to binary data
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Generate a unique file name
            String fileName = "avatars/" + UUID.randomUUID().toString() + ".jpg";

            // Upload to OSS
            PutObjectResult result = ossClient.putObject(bucketName, fileName, new ByteArrayInputStream(imageBytes));

            // Return the publicly accessible URL
            return "https://" + bucketName + ".oss-cn-beijing.aliyuncs.com/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to OSS: " + e.getMessage(), e);
        }
    }
}
