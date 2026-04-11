package com.springcore.ai.scaiplatform.entity;

import com.springcore.ai.scaiplatform.domain.extend.GenericPersistentObject;
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

@Entity
@Table(name = "am_notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends GenericPersistentObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;      // ผู้รับ
    private String title;
    private String message;
    private String type;
    private Long parentId;
    private String url;

    @Builder.Default
    @Column(name = "isRead")
    private boolean read = false;
}
