package vasu.easyproject.service;

import java.util.List;

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

    public UserProject getProjectByUserAndProjectId(Long userId, Long projectId) {
        UserProject userProject = userProjectRepository.findByUserIdAndProjectId(userId, projectId);

        if (userProject == null) {
            return null;
        }

        return userProjectRepository.save(userProject);
    }

    public List<UserProject> getUsersByProjectId(Long projectId) {
        return userProjectRepository.findByProjectId(projectId);
    }

    public List<UserProject> getProjectsByUserId(Long userId) {
        return userProjectRepository.findByUserId(userId);
    }
}
