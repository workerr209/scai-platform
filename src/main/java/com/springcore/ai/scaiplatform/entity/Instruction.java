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
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "llm_dataset")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instruction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instruction", columnDefinition = "TEXT")
    private String instruction;

    @Column(name = "output", columnDefinition = "LONGTEXT")
    private String output;

    @Column(name = "topic")
    private String topic;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

}
