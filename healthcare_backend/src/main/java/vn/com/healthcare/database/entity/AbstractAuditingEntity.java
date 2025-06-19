package vn.com.healthcare.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/


@Getter
@Setter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="status", length=50, nullable=false)
    private @NotBlank String status;

    @CreatedDate
    @Column(name="created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @CreatedBy
    @Column(name="created_by", length=100, nullable=false, updatable=false)
    private String createdBy;

    @LastModifiedDate
    @Column(name="last_modified_date")
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

    @LastModifiedBy
    @Column(name="last_modified_by", length=100)
    private String lastModifiedBy;

    @Column(name="notes", length=100)
    private String notes;


    public AbstractAuditingEntity() {

    }



}
