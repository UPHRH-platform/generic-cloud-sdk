package com.upsmf.gcpcloudsdk.validate;

import com.upsmf.gcpcloudsdk.config.AppConfig;
import com.upsmf.gcpcloudsdk.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class FileValidator {

    @Autowired
    private AppConfig config;

    public void validate(MultipartFile file){
        log.info("FileValidator::validate");
        if(file.isEmpty()){
            throw new FileStorageException("Error","File not found to upload");
        }
        DataSize maxFileSize = DataSize.parse(config.getFileSize()); // Maximum file size allowed (configured value)
        DataSize fileSize = DataSize.ofBytes(file.getSize());

        if (fileSize.compareTo(maxFileSize) > 0) {
            throw new FileStorageException("Error", "File size exceeds the allowed limit");
        }
    }
}
