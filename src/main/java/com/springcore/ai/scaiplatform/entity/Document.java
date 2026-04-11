package com.springcore.ai.scaiplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.springcore.ai.scaiplatform.domain.constant.DocumentStatus;
import com.springcore.ai.scaiplatform.domain.deserialiize.LookupItemToLongDeserializer;
import com.springcore.ai.scaiplatform.domain.extend.GenericPersistentObject;
import com.springcore.ai.scaiplatform.dto.LookupItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "am_doc")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Document extends GenericPersistentObject {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Document_GENERATOR")
    @SequenceGenerator(name = "Document_GENERATOR", sequenceName = "Document_ID_GENERATOR", allocationSize = 1)
    private Long id;

    private String documentNo;

    private String documentType;

    private DocumentStatus documentStatus;

    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    @Column(name = "emId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long emId;

    @Column(name = "reasonId")
    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long reasonId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emId", referencedColumnName = "id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Employee employee;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reasonId", referencedColumnName = "id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Reason reason;

    @JsonProperty("emId")
    public LookupItem getEmLookup() {
        if (this.emId == null) return null;
        return LookupItem.builder()
                .id(this.emId)
                .code(employee != null ? employee.getCode() : null)
                .name(employee != null ? employee.getName() : null)
                .build();
    }

    @JsonProperty("reasonId")
    public LookupItem getReasonLookup() {
        if (this.reasonId == null) return null;
        return LookupItem.builder()
                .id(this.reasonId)
                .code(reason != null ? reason.getCode() : null)
                .name(reason != null ? reason.getName() : null)
                .build();
    }

    @JsonProperty("documentStatusLabel")
    public String getDocumentStatusLabel() {
        return documentStatus.getLabel();
    }

    @JsonProperty("documentStatusSeverity")
    public String getDocumentStatusSeverity() {
        return documentStatus.getSeverity();
    }

    private Date dateWork;

    private Date dateTo;

    private Date punI_D;

    private Date punI_T;

    private Date punO_D;

    private Date punO_T;

    private String remark;

    public List<DocumentFile> getAttachment() {
        return attachment != null ? attachment : Collections.emptyList();
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "docId")
    private List<DocumentFile> attachment;

    @Transient
    FlowDoc flowDoc;

}
