package vn.com.healthcare.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/

@Entity
@Table(
        name = "sys_group_feature_permission"
)
@Getter
@Setter
@Builder
public class SysGroupFeaturePermission extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    @JdbcTypeCode(12)
    private UUID id;

    @Column(
            name = "user_id"
    )
    @JdbcTypeCode(12)
    private UUID userId;

    @Column(name = "group_id")
    @JdbcTypeCode(12)
    private UUID groupId;

    @Column(
            name = "feature_permission_id"
    )
    private String featurePermissionId;

    @Column(
            name = "feature_data_id"
    )
    @JdbcTypeCode(12)
    private UUID featureDataId;

    @Column(
            name = "description",
            length = 500
    )
    private @Size(
            min = 1,
            max = 500
    ) String description;

    @JsonIgnore
    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(
            name = "group_id"
    )
    private SysGroup sysGroup;
    @JsonIgnore
    @ManyToOne
    @MapsId("featurePermissionId")
    @JoinColumn(
            name = "feature_permission_id"
    )
    private SysFeaturePermission sysFeaturePermission;

}
