package com.springcore.ai.scaiplatform.entity.InkQuest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.springcore.ai.scaiplatform.domain.constant.InkQuest.InkDayQuality;
import com.springcore.ai.scaiplatform.domain.constant.InkQuest.InkWritingFlow;
import com.springcore.ai.scaiplatform.domain.deserialiize.LookupItemToLongDeserializer;
import com.springcore.ai.scaiplatform.domain.extend.GenericPersistentObject;
import com.springcore.ai.scaiplatform.dto.LookupItem;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.util.Date;

@Entity
@Table(
        name = "iq_daily_entry",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_iq_daily_entry_em_date", columnNames = {"emId", "entryDate"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class InkDailyEntry extends GenericPersistentObject {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InkDailyEntry_GENERATOR")
    @SequenceGenerator(name = "InkDailyEntry_GENERATOR", sequenceName = "InkDailyEntry_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    @Column(name = "emId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long emId;

    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    @Column(name = "projectId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long projectId;

    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    @Column(name = "chapterId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long chapterId;

    @Column(name = "entryDate", nullable = false)
    private Date entryDate;

    private Long words;

    private Long focusMinutes;

    private Long sessions;

    @Enumerated(EnumType.STRING)
    private InkWritingFlow flow;

    @Enumerated(EnumType.STRING)
    private InkDayQuality quality;

    @Column(columnDefinition = "TEXT")
    private String note;

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

    @JsonProperty("chapterId")
    public LookupItem getChapterLookup() {
        if (this.chapterId == null) return null;
        return LookupItem.builder()
                .id(this.chapterId)
                .build();
    }

    @JsonProperty("flowLabel")
    public String getFlowLabel() {
        return flow != null ? flow.getLabel() : null;
    }

    @JsonProperty("qualityLabel")
    public String getQualityLabel() {
        return quality != null ? quality.getLabel() : null;
    }
}
