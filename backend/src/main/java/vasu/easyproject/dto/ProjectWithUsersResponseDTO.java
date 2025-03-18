package vasu.easyproject.dto;

import java.util.List;
import vasu.easyproject.model.Project;
import vasu.easyproject.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProjectWithUsersResponseDTO {
    private Project project;
    private List<UserProjectWithRoleDTO> users;
}
