package com.springcore.ai.scaiplatform.entity;

import com.springcore.ai.scaiplatform.domain.extend.GenericPersistentObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class DocumentFile extends GenericPersistentObject {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DocumentFile_GENERATOR")
    @SequenceGenerator(name = "DocumentFile_GENERATOR", sequenceName = "DocumentFile_ID_GENERATOR", allocationSize = 1)
    private Long id;

    private Long docId;
    private String docType;

    @Column(name = "fileLabel", length = 2000)
    private String fileLabel;

    @Column(name = "fileName", length = 2000)
    private String fileName;

    @Column(name = "filePath", length = 3000)
    private String filePath;

    private String fileType;
}
