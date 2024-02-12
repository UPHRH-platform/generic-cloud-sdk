package com.upsmf.gcpcloudsdk.repository;

import com.upsmf.gcpcloudsdk.entity.FileUploadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUploadEntity,String> {
}
