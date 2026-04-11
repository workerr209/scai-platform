package com.springcore.ai.scaiplatform.controller;


import com.springcore.ai.scaiplatform.dto.FileUploadResult;
import com.springcore.ai.scaiplatform.entity.DocumentFile;
import com.springcore.ai.scaiplatform.service.api.FileService;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileUploadResult uploadFileResult = fileService.uploadFile(file);
            DocumentFile documentFile = new DocumentFile();
            documentFile.setFileName(uploadFileResult.getFileName());
            documentFile.setFileLabel(uploadFileResult.getFileLabel());
            documentFile.setFileType(uploadFileResult.getFileType());
            documentFile.setFilePath(uploadFileResult.getFilePath());
            return ResponseEntity.ok(documentFile);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileName}")
    @ResponseBody
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            byte[] bytes = fileService.downloadFile(fileName);
            if (bytes == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(bytes.length)
                    .body(bytes);
        } catch (ValidationException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
