package vasu.easyproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vasu.easyproject.model.Role;

@Getter
@Setter
@AllArgsConstructor
public class UserProjectWithRoleDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;
}
