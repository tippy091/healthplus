package vn.com.healthcare.database.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.jcodings.util.Hash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author tippy091
 * @created 17/06/2025
 * @project healthcare_backend
 **/


@Entity
@Table(name="sys_user")
@Getter
@Setter
@Builder
public class SysUser extends AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(12)
    private UUID id;

    @Column(name="first_name", length=20)
    private String firstName;

    @Column(name="last_name", length=20)
    private String lastName;

    @Column(name="gender", length=10)
    private String gender;

    @Column(name="dob", length=20)
    private Date dob;

    @Column(name="phone_number", length=12)
    private String phoneNumber;

    @Column(name="email", length=50)
    private String email;

    @Column(name="address", length=100)
    private String address;

    @Column(name="nation", length=20)
    private String nation;

    @Column(name = "login_date")
    private LocalDateTime loginDate;

    @Column(name = "logout_date")
    private LocalDateTime logoutDate;

    @ManyToOne
    @JoinColumn(name="group_id")
    private SysUserGroup group;
    public SysUser() {

    }
}
