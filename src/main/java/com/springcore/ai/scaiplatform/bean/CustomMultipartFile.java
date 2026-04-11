package com.springcore.ai.scaiplatform.bean;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class CustomMultipartFile implements MultipartFile {

    private final byte[] fileContent;
    private final String fileName;
    private final String contentType;

    public CustomMultipartFile(byte[] fileContent, String fileName, String contentType) {
        this.fileContent = fileContent;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return fileContent == null || fileContent.length == 0;
    }

    @Override
    public long getSize() {
        return fileContent.length;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public byte[] getBytes() {
        return fileContent;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(fileContent);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void transferTo(java.io.File dest) throws IllegalStateException {
        throw new UnsupportedOperationException("This operation is not supported.");
    }
}
