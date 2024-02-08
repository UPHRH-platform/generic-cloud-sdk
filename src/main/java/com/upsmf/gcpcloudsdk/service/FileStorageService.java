package com.upsmf.gcpcloudsdk.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface FileStorageService {

    String downloadFile(String filePath) throws IOException;
    String uploadFileObject(MultipartFile file) throws IOException;
    String deleteFile(String fileName) throws IOException;
}
