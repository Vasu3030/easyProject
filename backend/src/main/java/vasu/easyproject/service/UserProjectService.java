package vasu.easyproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vasu.easyproject.model.Project;
import vasu.easyproject.model.Role;
import vasu.easyproject.model.User;
import vasu.easyproject.model.UserProject;
import vasu.easyproject.repository.UserProjectRepository;

@Service
public class UserProjectService {
    
     @Autowired
    private UserProjectRepository userProjectRepository;

    public UserProject create(User user, Project project, Role role) {
        UserProject userProject = new UserProject();
        userProject.setUser(user);
        userProject.setProject(project);
        userProject.setRole(role);

        return userProjectRepository.save(userProject);
    }
}
