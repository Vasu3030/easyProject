package vasu.easyproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vasu.easyproject.model.Project;
import vasu.easyproject.repository.ProjectRepository;

@Service
public class ProjectService {
    
     @Autowired
    private ProjectRepository projectRepository;

    public Project create(Project project) {
        return projectRepository.save(project);
    }
}
