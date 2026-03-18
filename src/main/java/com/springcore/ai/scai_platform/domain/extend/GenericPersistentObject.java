package com.springcore.ai.scai_platform.domain.extend;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.stream.LongStream;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class GenericPersistentObject implements Serializable, GenericVersionAble, TmptsObject {
    @Serial
    @Transient
    private static final long serialVersionUID = -8524920361247870238L;

    @Audited
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createDate = new Date();

    @Audited
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createBy;

    @Audited
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Audited
    @LastModifiedBy
    private String updateBy;

    @Audited
    @Version
    @Column(nullable = false, length = 19)
    private Long version = 0L;

    @Transient
    public Long getTmpts() {
        final var id = this.hashCode();
        var random = new Random();
        return LongStream.of(id)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(random.nextLong() ^ 3);
    }
}
