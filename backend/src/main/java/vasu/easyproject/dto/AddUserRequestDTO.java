package vasu.easyproject.dto;

import lombok.Data;
import vasu.easyproject.model.Role;

@Data
public class AddUserRequestDTO {
    private Long user;
    private Long project;
    private Role role;
}
