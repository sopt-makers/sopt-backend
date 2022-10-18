package org.sopt.app.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "RGSTT_DTM", updatable = false)
    private LocalDateTime regDate;

    @Column(name = "RGSTT_ID", updatable = false, length = 20)
    private String regId;

    @LastModifiedDate
    @Column(name = "EDIT_DTM")
    private LocalDateTime editDate;

    @Column(name = "EDIT_ID", length = 20)
    private String editId;

}
