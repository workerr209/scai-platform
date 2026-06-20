package com.springcore.ai.scaiplatform.chapterly.entity;

import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalMetric;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalPeriod;
import com.springcore.ai.scaiplatform.chapterly.domain.ChapterlyGoalScope;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "chapterly_writing_goal")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ChapterlyWritingGoal extends ChapterlyOwnedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChapterlyWritingGoal_GENERATOR")
    @SequenceGenerator(name = "ChapterlyWritingGoal_GENERATOR", sequenceName = "ChapterlyWritingGoal_ID_GENERATOR", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", foreignKey = @ForeignKey(name = "fk_chapterly_goal_story"))
    private ChapterlyStory story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", foreignKey = @ForeignKey(name = "fk_chapterly_goal_chapter"))
    private ChapterlyChapter chapter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChapterlyGoalScope scope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChapterlyGoalPeriod period;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChapterlyGoalMetric metric;

    @Column(nullable = false)
    private Integer targetValue;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private Boolean active;

    @PrePersist
    void goalPrePersist() {
        if (active == null) {
            active = true;
        }
    }
}
