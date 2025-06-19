package vn.com.healthcare.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.jcodings.util.Hash;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/

@Entity
@Table(name="sys_group")
@Getter
@Setter
@Builder
public class SysGroup extends AbstractAuditingEntity implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(12)
    private UUID id;

    @Column(name="hospital_id", length = 50)
    private @NotBlank @Size(max = 50) String hospitalId;

    @Column(name="department_id", length=50)
    private @Size(max = 50) String departmentId;

    @Column(name="type", length = 50)
    private @NotBlank @Size(min = 1, max = 50) String type;

    @Column(name="group_name", length = 200, nullable = false)
    private @NotBlank @Size(min = 1, max = 200) String groupName;

    @Column(name="description", length=500, nullable = false)
    private @NotBlank @Size(min = 1, max = 500) String description;

    @JsonIgnore
    @OneToMany(mappedBy = "sysGroup")
    private Set<SysGroupFeaturePermission> sysGroupFeaturePermission = new HashSet();

    public SysGroup() {

    }
}
