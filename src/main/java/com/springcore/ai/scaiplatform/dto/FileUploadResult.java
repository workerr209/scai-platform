package com.springcore.ai.scaiplatform.dto;

import lombok.Data;

@Data
public class FileUploadResult {
    private String fileName;
    private String fileLabel;
    private String fileType;
    private String filePath;
}
