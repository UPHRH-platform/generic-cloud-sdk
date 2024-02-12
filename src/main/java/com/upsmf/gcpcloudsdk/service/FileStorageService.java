package com.upsmf.gcpcloudsdk.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface FileStorageService {
    String uploadFileObject(MultipartFile file,String commentTreeId) throws IOException;

}
