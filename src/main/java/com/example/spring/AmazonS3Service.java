package com.example.spring;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
class AmazonS3Service {
  @Autowired
  private AmazonS3 s3client;

  @Value("${amazonProperties.bucketName}")
  private String bucketName;

  String uploadFile(MultipartFile multipartFile) throws IOException {
    File file = convertMultiPartToFile(multipartFile);
    String fileName = generateFileName(file.getName());
    uploadFileTos3bucket(fileName, file);
    file.deleteOnExit();
    return fileName;
  }

  private File convertMultiPartToFile(MultipartFile file) throws IOException {
    File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
    try (FileOutputStream fos = new FileOutputStream(convFile)) {
      fos.write(file.getBytes());
    }
    return convFile;
  }

  private String generateFileName(String fileName) {
    return "pre-" + fileName;
  }

  private void uploadFileTos3bucket(String fileName, File file) {
    s3client.putObject(new PutObjectRequest(bucketName, fileName, file));
  }
}
