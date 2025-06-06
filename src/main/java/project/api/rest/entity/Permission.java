package project.api.rest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permissions")
public class Permission {
    @Id
    @Column(name = "permission_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "permission_name", length = 50)
    private String permissionName;

}