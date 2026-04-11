package com.springcore.ai.scaiplatform.service.impl;

import com.springcore.ai.scaiplatform.bean.CustomMultipartFile;
import com.springcore.ai.scaiplatform.dto.FileUploadResult;
import com.springcore.ai.scaiplatform.properties.ApplicationProperties;
import com.springcore.ai.scaiplatform.service.api.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    private final Path uploadDir;

    @Autowired
    public FileServiceImpl(ApplicationProperties applicationProperties) {
        final ApplicationProperties.Upload upload = applicationProperties.getUpload();
        this.uploadDir = Paths.get(upload.getPath());
    }

    @Override
    public FileUploadResult uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty() || StringUtils.isEmpty(file.getOriginalFilename())) {
            throw new IOException("Failed to Upload empty file.");
        }

        // Ensure upload directory exists
        Path pathUploadDir = this.uploadDir;
        if (!Files.exists(pathUploadDir)) {
            Files.createDirectories(pathUploadDir);
        }

        // Extract original filename and extension
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        extension = StringUtils.isNotEmpty(extension) ? "." + extension : "";

        // Generate a unique filename with UUID + timestamp
        String uniqueId = UUID.randomUUID().toString().replace("-", "").substring(0, 10); // 10-character UUID
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String newFilename = uniqueId + "_" + timestamp + extension;

        Path destinationFile = this.uploadDir.resolve(newFilename).normalize().toAbsolutePath();
        String filePath = destinationFile.toString();

        // Ensure file stays within the designated upload directory
        if (!destinationFile.getParent().equals(this.uploadDir.toAbsolutePath())) {
            throw new IOException("Cannot Upload File outside current directory.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | SecurityException e) {
            log.error("Upload failed: {}", e.getMessage());
            log.error("FileName : {} length : {}", destinationFile.getFileName().toString(), destinationFile.getFileName().toString().getBytes(StandardCharsets.UTF_8).length);  // File name length
            log.error("FilePath : {} length : {}", destinationFile, destinationFile.toString().getBytes(StandardCharsets.UTF_8).length);  // Full path length
            throw new IOException(e.getMessage(), e);
        }

        FileUploadResult uploadFileResult = new FileUploadResult();
        String flowDocFileName = (StringUtils.isNotEmpty(newFilename)) ? newFilename : originalFilename;
        uploadFileResult.setFileName(flowDocFileName);
        uploadFileResult.setFileLabel(originalFilename);
        uploadFileResult.setFileType(extension);
        uploadFileResult.setFilePath(filePath);
        log.debug("Upload File Success  : FileName {} FileLabel {} ", flowDocFileName, originalFilename);
        return uploadFileResult;
    }

    @Override
    public byte[] downloadFile(String fileName) throws IOException {
        try {
            Path file = uploadDir.resolve(fileName);
            log.debug("downloading file: {}", file);
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IOException("File not found: " + fileName);
            }

            Path normalize = file.normalize();
            return Files.readAllBytes(normalize);
        } catch (IOException e) {
            throw new IOException("Download Failed : " + e.getMessage(), e);
        }
    }

    @Override
    public MultipartFile convertByteArrayToMultipartFile(byte[] byteArray, String fileName, String contentType) {
        return new CustomMultipartFile(byteArray, fileName, contentType);
    }
}
