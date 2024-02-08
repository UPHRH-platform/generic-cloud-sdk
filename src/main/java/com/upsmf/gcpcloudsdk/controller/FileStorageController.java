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
    public String uploadFiletObject(@RequestParam MultipartFile file) throws IOException {
        log.info("FileStorageController :: uploadFiletoS3()");
        return fileService.uploadFileObject(file);
    }
    @GetMapping("/downloadFile")
    public String downloadFile(@RequestParam String fileName) throws IOException {
        return fileService.downloadFile(fileName);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@RequestParam String fileName) throws IOException {
        return new ResponseEntity<>(fileService.deleteFile(fileName), HttpStatus.OK);
    }
    @GetMapping("/health")
    public String healthCheck() {
        log.info("FileStorageController :: healthCheck()");
        return "Success";
    }
}
