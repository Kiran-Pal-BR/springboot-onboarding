package com.example.onboarding.controller;

import com.example.onboarding.project.Project;
import com.example.onboarding.project.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    // 1. Get all projects
    @GetMapping
    public List<Project> getAllProjects() {
        log.info("Controller: GET /api/projects called");
        return projectRepository.findAll();
    }

    // 2. Create a project
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        log.info("Controller: POST /api/projects called");
        Project savedProject = projectRepository.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProject);
    }

    // 3. Get project by ID
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable String id) {
        log.info("Controller: GET /api/projects/{} called", id);
        return projectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 4. Delete project
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        log.info("Controller: DELETE /api/projects/{} called", id);
        if (!projectRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        projectRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}