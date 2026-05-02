package com.springcore.ai.scaiplatform.entity.InkQuest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.springcore.ai.scaiplatform.domain.deserialiize.LookupItemToLongDeserializer;
import com.springcore.ai.scaiplatform.domain.extend.GenericPersistentObject;
import com.springcore.ai.scaiplatform.dto.LookupItem;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;

@Entity
@Table(
        name = "iq_settings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_iq_settings_em", columnNames = {"emId"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class InkSettings extends GenericPersistentObject {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InkSettings_GENERATOR")
    @SequenceGenerator(name = "InkSettings_GENERATOR", sequenceName = "InkSettings_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    @Column(name = "emId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long emId;

    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    @Column(name = "defaultProjectId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long defaultProjectId;

    private Boolean wordGoalReminder;

    private String reminderTime;

    private Boolean autoLogStreak;

    private Boolean showWordCountInMenu;

    @JsonProperty("emId")
    public LookupItem getEmLookup() {
        if (this.emId == null) return null;
        return LookupItem.builder()
                .id(this.emId)
                .build();
    }

    @JsonProperty("defaultProjectId")
    public LookupItem getDefaultProjectLookup() {
        if (this.defaultProjectId == null) return null;
        return LookupItem.builder()
                .id(this.defaultProjectId)
                .build();
    }
}
