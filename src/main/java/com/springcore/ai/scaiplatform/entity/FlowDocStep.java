package com.springcore.ai.scaiplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "am_flowdocstep")
@Getter
@Setter
public class FlowDocStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actiondate")
    private Date actionDate;

    @Column(name = "actiontype")
    private String actionType;

    @Column(name = "actortype")
    private String actorType;

    private Long emadm;
    private Long emdeg;
    private Long emman;

    @Transient
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Employee emmanInfo;

    @Column(nullable = false)
    private BigDecimal isend;

    private String langadm;
    private String langdeg;
    private String langman;

    private String mailadm;
    private String maildeg;
    private String mailman;

    @Column(nullable = false)
    private BigDecimal mailstat;

    @Column(nullable = false)
    private int stepno;

    @Column(name = "isActive")
    private Integer isActive;

    @Column(name = "reqCancel")
    private Integer reqCancel;

    // เชื่อมกลับไปยัง Parent
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private FlowDoc flowDoc;
}