package com.springcore.ai.scaiplatform.service.api;

import com.springcore.ai.scaiplatform.dto.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    FileUploadResult uploadFile(MultipartFile file) throws IOException;
    byte[] downloadFile(String fileName) throws IOException;
    MultipartFile convertByteArrayToMultipartFile(byte[] byteArray, String fileName, String contentType) throws IOException;
}
