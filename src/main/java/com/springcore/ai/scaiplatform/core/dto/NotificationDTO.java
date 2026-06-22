package com.springcore.ai.scaiplatform.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String message;

    private String type;

    private Long parentId;

    private Date timestamp;

    private String payload;

    private String url;

}