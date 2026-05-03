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
        name = "iq_writing_goal",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_iq_writing_goal_em", columnNames = {"emId"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class InkWritingGoal extends GenericPersistentObject {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InkWritingGoal_GENERATOR")
    @SequenceGenerator(name = "InkWritingGoal_GENERATOR", sequenceName = "InkWritingGoal_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    @Column(name = "emId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long emId;

    private Integer dailyWords;

    private Integer monthlyWords;

    private Integer dailyFocus;

    private Integer streakTarget;

    @JsonProperty("emId")
    public LookupItem getEmLookup() {
        if (this.emId == null) return null;
        return LookupItem.builder()
                .id(this.emId)
                .build();
    }
}
