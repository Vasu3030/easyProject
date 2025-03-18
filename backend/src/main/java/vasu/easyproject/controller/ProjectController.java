package vasu.easyproject.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import vasu.easyproject.dto.AddUserRequestDTO;
import vasu.easyproject.dto.ProjectWithUsersResponseDTO;
import vasu.easyproject.dto.UserProjectWithRoleDTO;
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

        // Crée le projet
        Project savedProject = projectService.create(project);

        // Associe l'utilisateur au projet avec son rôle
        UserProject savedUserProject = userProjectService.create(user, savedProject, Role.ADMIN);

        // Appel à la méthode getProjectWithUsers pour obtenir la réponse complète avec les utilisateurs et leurs rôles
        return getProjectWithUsers(savedProject.getId());  // Passer directement l'ID du projet
    }


    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestBody AddUserRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long adminId = (Long) authentication.getDetails(); // Récupère l'ID utilisateur connecté

        // Contrôle si l'utilisateur est admin sur le projet
        Role userRole = userProjectService.getProjectByUserAndProjectId(adminId, request.getProject()).getRole();

        if (userRole != Role.ADMIN) {
            return ResponseEntity.status(403).body("User not permitted to add users");
        }

        // Récupère l'utilisateur à ajouter
        Optional<User> user = userService.getUserById(request.getUser());
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Récupère le projet
        Optional<Project> project = projectService.getProjectById(request.getProject());
        if (project.isEmpty()) {
            return ResponseEntity.status(404).body("Project not found");
        }

        // Vérifie si l'utilisateur appartient déjà au projet
        UserProject checkUserProjectExist = userProjectService.getProjectByUserAndProjectId(request.getUser(), request.getProject());
        if (checkUserProjectExist != null) {
            return ResponseEntity.status(400).body("User already belongs to project");
        }

        // Ajoute l'utilisateur au projet avec son rôle
        UserProject addedUserProject = userProjectService.create(user.get(), project.get(), request.getRole());

        // Appel à la méthode getProjectWithUsers pour obtenir la réponse complète avec les utilisateurs et leurs rôles
        return getProjectWithUsers(project.get().getId());  // Passer directement l'ID du projet
    }



    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectWithUsers(@PathVariable Long projectId) {
        // Récupère le projet
        Project project = projectService.getProjectById(projectId).orElse(null);
        
        if (project == null) {
            return ResponseEntity.status(404).body(null);
        }

        // Récupère la liste des UserProject associés à ce projet avec le role
        List<UserProjectWithRoleDTO> userRoleDTOs = userProjectService.getUsersByProjectId(projectId).stream()
                .map(userProject -> new UserProjectWithRoleDTO(
                        userProject.getUser().getId(),
                        userProject.getUser().getUsername(),
                        userProject.getUser().getEmail(),
                        userProject.getRole()))  
                .collect(Collectors.toList());

        ProjectWithUsersResponseDTO response = new ProjectWithUsersResponseDTO(project, userRoleDTOs);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/myProjects")
    public ResponseEntity<?> getMyProjects() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Récupérer l'utilisateur connecté
        User user = userService.getUserByUsername(authentication.getName());

        // Récupérer les UserProject associés à cet utilisateur
        List<UserProject> userProjects = userProjectService.getProjectsByUserId(user.getId());

        // Si l'utilisateur n'a aucun projet, renvoyer une réponse vide
        if (userProjects.isEmpty()) {
            return ResponseEntity.status(404).body("No projects found for this user");
        }

        // Mapper les UserProject pour renvoyer les informations du projet et du rôle
        List<ProjectWithUsersResponseDTO> projectDtos = userProjects.stream()
                .map(userProject -> {
                    Project project = userProject.getProject();
                    // Utiliser la méthode getProjectWithUsers pour chaque projet auquel l'utilisateur appartient
                    return new ProjectWithUsersResponseDTO(project, 
                            userProjectService.getUsersByProjectId(project.getId()).stream()
                                    .map(up -> new UserProjectWithRoleDTO(
                                            up.getUser().getId(),
                                            up.getUser().getUsername(),
                                            up.getUser().getEmail(),
                                            up.getRole()))
                                    .collect(Collectors.toList()));
                })
                .collect(Collectors.toList());

        // Retourner la liste des projets
        return ResponseEntity.ok(projectDtos);
    }
}
