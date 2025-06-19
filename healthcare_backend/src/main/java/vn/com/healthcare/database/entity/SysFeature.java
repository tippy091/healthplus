package vn.com.healthcare.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/

@Entity
@Table(
        name = "sys_feature"
)
@Getter
@Setter
@Builder
public class SysFeature extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(
            name = "display_name",
            length = 200,
            nullable = false
    )
    private @NotBlank @Size(
            min = 1,
            max = 200
    ) String displayName;

    @Column(
            name = "description",
            length = 500,
            nullable = false
    )
    private @NotBlank @Size(
            min = 1,
            max = 500
    ) String description;

    @JsonIgnore
    @OneToMany(mappedBy = "sysFeature")
    @BatchSize(size = 6)
    private Set<SysFeaturePermission> sysFeaturePermission = new HashSet();

    public SysFeature() {

    }
}
