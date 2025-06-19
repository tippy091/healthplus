package vn.com.healthcare.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/

@Entity
@Table(
        name = "sys_feature_permission"
)
@Getter
@Setter
@Builder
public class SysFeaturePermission extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(
            name = "feature_id",
            nullable = false
    )
    private @NotBlank String featureId;

    @Column(
            name = "permission",
            length = 50,
            nullable = false
    )
    private @NotBlank @Size(
            min = 1,
            max = 50
    ) String permission;

    @Column(
            name = "display_name",
            length = 200,
            nullable = false
    ) @NotBlank @Size(
            min = 1,
            max = 200
    ) String displayName;

    private @NotBlank @Size(
            min = 1,
            max = 500
    ) String description;

    @JsonIgnore
    @ManyToOne
    @MapsId("featureId")
    @JoinColumn(name = "feature_id")
    private SysFeature sysFeature;

    public SysFeaturePermission() {

    }
}
