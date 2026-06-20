package com.springcore.ai.scaiplatform.core.dto;

import lombok.Data;

@Data
public class MoveEmployeeRequest {
    private Long employeeId;
    private Long newManagerId;
}
