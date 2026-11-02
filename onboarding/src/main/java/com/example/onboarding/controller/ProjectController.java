package com.example.onboarding.controller;

import com.example.onboarding.project.ProjectService;
import com.example.onboarding.project.dto.ProjectCreateRequestDto;
import com.example.onboarding.project.dto.ProjectResponseDto;
import com.example.onboarding.project.dto.ProjectUpdateRequestDto;

import jakarta.validation.Valid;

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

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // 1. Get all projects
    @GetMapping
    public List<ProjectResponseDto> getAllProjects() {
        log.info("Controller: GET /api/projects called");
        return projectService.getAllProjects();
    }

    // 2. Create a project
    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(@RequestBody ProjectCreateRequestDto requestDto) {
        log.info("Controller: POST /api/projects called");
        ProjectResponseDto savedProject = projectService.createProject(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProject);
    }

    // 3. Get project by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable String id) {
        log.info("Controller: GET /api/projects/{} called", id);
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    //4. Update Project
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto>updateProject(@PathVariable String id, @Valid @RequestBody ProjectUpdateRequestDto requestDto){
        log.info("Controller: GET/api/projects{} called",id );
        return projectService.updateProject(id, requestDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build() );
    }


    // 5. Delete project
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        log.info("Controller: DELETE /api/projects/{} called", id);
        boolean deleted = projectService.deleteProject(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.noContent().build();
    }
}
