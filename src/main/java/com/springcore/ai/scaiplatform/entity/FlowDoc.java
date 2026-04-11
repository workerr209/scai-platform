package com.springcore.ai.scaiplatform.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springcore.ai.scaiplatform.security.UserContext;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "am_flowdoc")
@Getter
@Setter
public class FlowDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("isActiveStep")
    public boolean getIsActiveStep() {
        Long emId = UserContext.getEmId();
        if (emId == null) {
            return false;
        }

        return steps.stream()
                .filter(step -> step.getStepno() == activeStep)
                .anyMatch(step -> step.getEmman() != null && step.getEmman().compareTo(emId) == 0);
    }

    @JsonProperty("stepActive")
    public FlowDocStep getStepActive() {
        Long emId = UserContext.getEmId();
        if (emId == null) {
            return null;
        }

        return steps.stream()
                .filter(step -> step.getStepno() == activeStep)
                .findFirst()
                .orElse(null);
    }

    @Column(name = "ACTIVESTEP")
    private int activeStep;

    @Column(name = "createddate")
    private Date createdDate;

    @Column(name = "docid")
    private Long docId;

    @Column(name = "docno", unique = true)
    private String docNo;

    @Column(name = "docrec")
    private String docRec;

    @Column(name = "doctype")
    private String docType;

    @Column(name = "em")
    private Long em;

    @Column(name = "flowbatch")
    private String flowBatch;

    @Column(name = "flowcode")
    private String flowCode;

    @Column(name = "INACTIVE")
    private BigDecimal inactive;

    @Column(name = "lastFlowComment", length = 8000)
    private String lastFlowComment;

    @Column(name = "lastflowdate")
    private LocalDateTime lastFlowDate;

    @Column(name = "lastflowstat")
    private String lastFlowStat;

    @Column(name = "LASTSTEP")
    private BigDecimal lastStep;

    @Column(name = "owner")
    private Long owner;

    @Column(name = "requesteddate")
    private Date requestedDate;

    @Column(name = "reqcancel")
    private BigDecimal reqCancel;

    @Column(name = "effDate")
    private Date effDate;

    @Column(name = "tagsFlowContent", length = 1000)
    private String tagsFlowContent;

    @Column(name = "sysrem", length = 2000)
    private String sysRem;

    @OneToMany(mappedBy = "flowDoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FlowDocStep> steps;


}