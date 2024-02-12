package com.upsmf.gcpcloudsdk.controller;


import com.upsmf.gcpcloudsdk.service.impl.FileStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileStorageController {

    @Autowired
    private FileStorageServiceImpl fileService;

    @PostMapping("/uploadFile")
    public String uploadFiletObject(@RequestParam MultipartFile file,
                                    @RequestParam("commentTreeId") String commentTreeId) throws IOException {
        log.info("FileStorageController :: uploadFiletoS3()");
        return fileService.uploadFileObject(file,commentTreeId);
    }
    
    @GetMapping("/health")
    public String healthCheck() {
        log.info("FileStorageController :: healthCheck()");
        return "Success";
    }
}
