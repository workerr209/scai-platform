package com.springcore.ai.scaiplatform.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeHierarchyId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long ancestorid;
    private Long descendantid;
}
