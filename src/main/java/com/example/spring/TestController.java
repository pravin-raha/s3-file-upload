package com.example.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class TestController {

    private final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private AmazonS3Service amazonClient;

    @PostMapping("/upload")
    public void uploadFile(HttpServletRequest request,
                           @RequestPart(value = "file") MultipartFile file) {
        try {
            this.amazonClient.uploadFile(file);
        } catch (IOException exception) {
            logger.error("Exception during upload : {}", exception.getMessage());
        }
    }
}
