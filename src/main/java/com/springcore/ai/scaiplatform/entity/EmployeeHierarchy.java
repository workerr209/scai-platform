package com.springcore.ai.scaiplatform.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "am_employee_hierarchy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeHierarchy implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private EmployeeHierarchyId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ancestorid")
    @JoinColumn(name = "ancestorid")
    private Employee ancestor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("descendantid")
    @JoinColumn(name = "descendantid")
    private Employee descendant;

    private Integer depth; // 0 = ตัวเอง, 1 = ลูกน้องตรง...
}

