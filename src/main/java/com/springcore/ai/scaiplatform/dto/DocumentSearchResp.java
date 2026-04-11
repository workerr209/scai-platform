package com.springcore.ai.scaiplatform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springcore.ai.scaiplatform.domain.constant.DocumentStatus;
import com.springcore.ai.scaiplatform.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DocumentSearchResp {
    private Long id;
    private String documentNo;
    private String documentType;
    private DocumentStatus documentStatus;
    private Employee emId;
    private Date documentDate;

    public String getId() {
        return id.toString();
    }

    @JsonProperty("documentStatusLabel")
    public String getDocumentStatusLabel() {
        return documentStatus.getLabel();
    }

    @JsonProperty("documentStatusSeverity")
    public String getDocumentStatusSeverity() {
        return documentStatus.getSeverity();
    }
}

