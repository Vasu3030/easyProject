package vasu.easyproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import vasu.easyproject.model.Project;
import vasu.easyproject.model.Role;
import vasu.easyproject.model.User;
import vasu.easyproject.model.UserProject;
import vasu.easyproject.service.ProjectService;
import vasu.easyproject.service.UserProjectService;
import vasu.easyproject.service.UserService;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserProjectService userProjectService;
    @Autowired
    private UserService userService;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userService.getUserByUsername(authentication.getName());

        Project savedProject = projectService.create(project);

        
        UserProject savedUserProject = userProjectService.create(user, savedProject, role.ADMIN);
        
        return ResponseEntity.status(201).body(savedUserProject);
    }
}
