package com.springcore.ai.scaiplatform.dto.InkQuest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InkEntrySearchReq {
    private Long emId;
    private Long projectId;
    private Long chapterId;
    private Date dateFrom;
    private Date dateTo;
}
