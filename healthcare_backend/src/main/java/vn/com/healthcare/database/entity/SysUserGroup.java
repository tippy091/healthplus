package vn.com.healthcare.database.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Getter
@Setter
@Builder
@Table(name="sys_user")
public class SysUserGroup extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(12)
    private UUID id;

    @Column(name="group_id", nullable = false)
    @JdbcTypeCode(12)
    private @NotNull UUID groupId;


    @Column(name="user_id", nullable = false)
    @JdbcTypeCode(12)
    private @NotNull UUID userId;

    public SysUserGroup() {

    }

}
