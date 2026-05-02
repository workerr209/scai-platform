package com.springcore.ai.scaiplatform.entity.InkQuest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.springcore.ai.scaiplatform.domain.constant.InkQuest.InkChapterStatus;
import com.springcore.ai.scaiplatform.domain.deserialiize.LookupItemToLongDeserializer;
import com.springcore.ai.scaiplatform.domain.extend.GenericPersistentObject;
import com.springcore.ai.scaiplatform.dto.LookupItem;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.util.Date;

@Entity
@Table(name = "iq_chapter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class InkChapter extends GenericPersistentObject {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InkChapter_GENERATOR")
    @SequenceGenerator(name = "InkChapter_GENERATOR", sequenceName = "InkChapter_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    @Column(name = "emId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long emId;

    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    @Column(name = "projectId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long projectId;

    private Long chapterNo;

    private String title;

    @Enumerated(EnumType.STRING)
    private InkChapterStatus status;

    private Long goalWords;

    private Long writtenWords;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private Date updatedAt;

    @JsonProperty("emId")
    public LookupItem getEmLookup() {
        if (this.emId == null) return null;
        return LookupItem.builder()
                .id(this.emId)
                .build();
    }

    @JsonProperty("projectId")
    public LookupItem getProjectLookup() {
        if (this.projectId == null) return null;
        return LookupItem.builder()
                .id(this.projectId)
                .build();
    }

    @JsonProperty("statusLabel")
    public String getStatusLabel() {
        return status != null ? status.getLabel() : null;
    }

    @JsonProperty("statusSeverity")
    public String getStatusSeverity() {
        return status != null ? status.getSeverity() : null;
    }
}
