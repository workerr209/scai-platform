package com.springcore.ai.scaiplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DocumentSearchReq {
    private Long emId;
    private String documentNo;
    private String documentType;
    private String documentStatus;
    private Date dateVF;
    private Date dateVT;
}
