package vasu.easyproject.controller;

import java.util.Optional;

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
import vasu.easyproject.dto.AddUserRequestDTO;
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

    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody AddUserRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long adminId = (Long) authentication.getDetails(); // Récupère l'ID utilisateur connecté

        // Controlle si user est admin sur le projet
        Role userRole = userProjectService.getProjectByUserAndProjectId(adminId, request.getProject()).getRole();
        
        if (userRole != role.ADMIN){
            return ResponseEntity.status(403).body("User not permitted to add users");
        }

        // Récupère le user à ajouter
        Optional<User> user = userService.getUserById(request.getUser());
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        // Récupère le projet
        Optional<Project> project = projectService.getProjectById(request.getProject());
        if (project.isEmpty()) {
            return ResponseEntity.status(404).body("Project not found");
        }
        // Check si le user appartient déjà au projet
        UserProject checkUserProjectExist = userProjectService.getProjectByUserAndProjectId(request.getUser(), request.getProject());
        if (checkUserProjectExist != null){
            return ResponseEntity.status(400).body("User already belongs to project");
        }

        UserProject addedUserProject = userProjectService.create(user.get(), project.get(), request.getRole());
        
        return ResponseEntity.status(201).body(addedUserProject);
    }
}
