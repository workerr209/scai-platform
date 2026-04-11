package com.springcore.ai.scaiplatform.entity;

import com.springcore.ai.scaiplatform.domain.extend.GenericPersistentObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Table(name = "am_trafts", indexes = {
        @Index(name = "idx_traft_employee", columnList = "employeeid"),
        @Index(name = "idx_traft_manager", columnList = "managerid"),
        @Index(name = "idx_traft_iscurrent", columnList = "iscurrent")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Trafts extends GenericPersistentObject {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeid", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managerid")
    private Employee manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentid")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "positionid")
    private Position position;

    @ManyToMany
    @JoinTable(
            name = "am_traft_jobroles",
            joinColumns = @JoinColumn(name = "traftid"),
            inverseJoinColumns = @JoinColumn(name = "jobroleid")
    )
    private Set<JobRole> jobRoles;

    private LocalDateTime effectivedate;

    private LocalDateTime enddate;

    @Column(name = "iscurrent")
    private Boolean iscurrent;

    private String trafttype;
}
