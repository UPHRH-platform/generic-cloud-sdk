package com.upsmf.gcpcloudsdk.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;
import com.upsmf.gcpcloudsdk.entity.FileUploadEntity;
import com.upsmf.gcpcloudsdk.exception.FileStorageException;
import com.upsmf.gcpcloudsdk.repository.FileUploadRepository;
import com.upsmf.gcpcloudsdk.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${gcp.client.id}")
    private String gcpClientId;
    @Value("${gcp.client.email}")
    private String gcpClientEmail;
    @Value("${gcp.pkcs.key}")
    private String gcpPkcsKey;
    @Value("${gcp.private.key.id}")
    private String gcpPrivateKeyId;
    @Value("${gcp.project.id}")
    private String gcpProjectId;
    @Value("${gcp.bucket.folder.name}")
    private String gcpFolderName;
    @Value("${gcp.bucket.name}")
    private String gcpBucketName;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private FileUploadRepository fileUploadRepository;

    public String uploadFileObject(MultipartFile file, String commentTreeId) throws IOException {
        Path filePath = null;
        try {
            // validate file
            String fileName = file.getOriginalFilename();
            filePath = Files.createTempFile(fileName.split("\\.")[0], fileName.split("\\.")[1]);
            file.transferTo(filePath);
            validateFile(filePath);
            // create credentials
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromPkcs8(gcpClientId, gcpClientEmail,
                    gcpPkcsKey, gcpPrivateKeyId, new ArrayList<String>());
            log.info("credentials created");
            Storage storage = StorageOptions.newBuilder().setProjectId(gcpProjectId).setCredentials(credentials).build().getService();
            log.info("storage object created");
            String gcpFileName = gcpFolderName + "/" + Calendar.getInstance().getTimeInMillis() + "_" + fileName;

            byte[] fileData = FileUtils.readFileToByteArray(convertFile(file));
            String contentType = Files.probeContentType(new File(file.getOriginalFilename()).toPath());

            Bucket bucket = storage.get(gcpBucketName, Storage.BucketGetOption.fields());
            Blob blob = bucket.create(gcpFileName, fileData, contentType);

            /*log.info(blob.toString());
            URL url = blob.signUrl(30, TimeUnit.DAYS);
            log.info("URL - {}", url);
            String urlString = url.toURI().toString();
            ObjectNode urlNode = mapper.createObjectNode();
            urlNode.put("url", urlString);
            ObjectNode node = mapper.createObjectNode();
            node.put("result", urlNode);*/

            String fileLocationPath = gcpBucketName + "/"+ blob.getName();
            log.info("fileLocationPath: {}",fileLocationPath);
            ObjectNode node = mapper.createObjectNode();
            node.put("FileLocationPath",fileLocationPath);
            // Store the URL in the database
            List<String> listOfFilePath= new ArrayList<>();
            listOfFilePath.add(fileLocationPath);
            FileUploadEntity entity = new FileUploadEntity();
            entity.setCommentTreeId(commentTreeId);
            entity.setFilePaths(listOfFilePath);
            fileUploadRepository.save(entity);
            return mapper.writeValueAsString(fileLocationPath);
        } catch (Exception e) {
            log.error("Error while uploading attachment", e);
            throw new FileStorageException("ERROR",e.getMessage());
        } finally {
            if (filePath != null) {
                try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    log.error("Unable to delete temp file", e);
                }
            }
        }
    }

    private boolean validateFile(Path path) throws IOException {
        if (Files.isExecutable(path)) {
            throw new RuntimeException("Invalid file");
        }
        return Boolean.TRUE;
    }

    private File convertFile(MultipartFile file) {
        FileOutputStream outputStream = null;
        try {
            if (file.getOriginalFilename() == null) {
                throw new RuntimeException("Original file name is null");
            }
            File convertedFile = new File(file.getOriginalFilename());
            outputStream = new FileOutputStream(convertedFile);
            outputStream.write(file.getBytes());
            log.debug("Converting multipart file : {}", convertedFile);
            return convertedFile;
        } catch (Exception e) {
            throw new RuntimeException("An error has occurred while converting the file");
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }

    }

    public String downloadFile(String gcpPathUrl) throws IOException {
        ServiceAccountCredentials credentials = ServiceAccountCredentials.fromPkcs8(gcpClientId, gcpClientEmail,
                gcpPkcsKey, gcpPrivateKeyId, new ArrayList<String>());
        Storage storage = StorageOptions.newBuilder().setProjectId(gcpProjectId).setCredentials(credentials).build().getService();
        URL signedUrl = storage.signUrl(
                com.google.cloud.storage.BlobInfo.newBuilder(gcpBucketName, gcpPathUrl).build(),
                15, TimeUnit.MINUTES,
                Storage.SignUrlOption.withV4Signature(),
                Storage.SignUrlOption.httpMethod(HttpMethod.GET)
        );

        // Return the pre-signed URL as a string
        log.info("Generated pre-signed URL: " + signedUrl.toString());
        return signedUrl.toString();
    }

    public String deleteFile(String fileName) throws IOException {
        log.info("FileStorageServiceImpl :: deleteFile");
        ServiceAccountCredentials credentials = ServiceAccountCredentials.fromPkcs8(gcpClientId, gcpClientEmail,
                gcpPkcsKey, gcpPrivateKeyId, new ArrayList<String>());
        Storage storage = StorageOptions.newBuilder().setProjectId(gcpProjectId).setCredentials(credentials).build().getService();
        Bucket bucket = storage.get(gcpBucketName);
        if (bucket != null) {
            Blob blob = bucket.get(fileName);
            if (blob != null) {
                blob.delete();
                return fileName + " removed from bucket";
            } else {
                return "Blob not found in bucket: " + fileName;
            }
        } else {
            return "Bucket not found: " + bucket;
        }
    }
}
