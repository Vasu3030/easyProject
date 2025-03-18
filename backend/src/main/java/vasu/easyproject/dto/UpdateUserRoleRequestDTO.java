package vasu.easyproject.dto;

import lombok.Data;
import vasu.easyproject.model.Role;

@Data
public class UpdateUserRoleRequestDTO {
    private Long userId;
    private Long projectId;
    private Role role;
}
