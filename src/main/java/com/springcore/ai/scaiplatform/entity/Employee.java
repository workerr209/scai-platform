package com.springcore.ai.scaiplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "am_employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    private String namealt;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    private Long inactive;

    private String cardno;

    private Long persid;

    private Long companyid;

    private Long estid;

    private LocalDateTime startjobd;

    private LocalDateTime endjobd;

    private LocalDateTime servicedate;

    private LocalDateTime companysenda;

    private LocalDateTime actendprobdat;

    private Long quitreason;

    private String newemssotype;

    private String lastemployeedb;

    private Long severancetype;

    private Long crcid;

    private Long isrehire;

    private Long ispayseverance;

    private String lockerno;

    private LocalDateTime retireddate;

    private Long notpayroll;

    private LocalDateTime signdate;

    private String sysrem;

    private Long lm2_uniformsiz;

    private Long lm2_locker;

    private String color;

    private String bodycolour;

    private String passtype;

    private Long addpayday;

    @Column(columnDefinition = "TEXT")
    private String hospitalname;

    private BigDecimal addpayamt;

    private Long extraMonth;

    private LocalDateTime lastAccept;

    @Column(length = 4000)
    private String metadata;

    private String employeeStatus;

    private LocalDateTime actPassProbDa;

    @Column(length = 4000)
    private String tagsValue;
}
