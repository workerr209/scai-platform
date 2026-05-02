package com.springcore.ai.scaiplatform.entity.InkQuest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.springcore.ai.scaiplatform.domain.deserialiize.LookupItemToLongDeserializer;
import com.springcore.ai.scaiplatform.domain.extend.GenericPersistentObject;
import com.springcore.ai.scaiplatform.dto.LookupItem;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "iq_note")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class InkNote extends GenericPersistentObject {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InkNote_GENERATOR")
    @SequenceGenerator(name = "InkNote_GENERATOR", sequenceName = "InkNote_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    @Column(name = "emId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long emId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(
            name = "iq_note_tag",
            joinColumns = @JoinColumn(name = "noteId", referencedColumnName = "id",
                    foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    )
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    private Date createdAt;

    private Date updatedAt;

    @JsonProperty("emId")
    public LookupItem getEmLookup() {
        if (this.emId == null) return null;
        return LookupItem.builder()
                .id(this.emId)
                .build();
    }
}
