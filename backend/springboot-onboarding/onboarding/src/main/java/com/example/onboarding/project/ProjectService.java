package com.example.onboarding.project;

import com.example.onboarding.exception.DuplicateResourceException;
import com.example.onboarding.project.dto.ProjectCreateRequestDto;
import com.example.onboarding.project.dto.ProjectResponseDto;
import com.example.onboarding.project.dto.ProjectUpdateRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Page<ProjectResponseDto> getAllProjects(Pageable pageable) {
        log.info("Service: Fetching projects with pagination");
        return projectRepository.findAll(pageable).map(this::mapToResponseDto);
    }

    public Optional<ProjectResponseDto> getProjectById(String id) {
        log.info("Service: Fetching project by ID: {}", id);
        return projectRepository.findById(id).map(this::mapToResponseDto);
    }

    public ProjectResponseDto createProject(ProjectCreateRequestDto requestDto) {
        log.info("Service: Creating project: {}", requestDto.name());
        if (projectRepository.existsByName(requestDto.name())) {
            throw new DuplicateResourceException("Project with name '" + requestDto.name() + "' already exists.");
        }

        Project project = new Project();
        project.setName(requestDto.name());
        project.setDescription(requestDto.description());
        return mapToResponseDto(projectRepository.save(project));
    }

    public Optional<ProjectResponseDto> updateProject(String id, ProjectUpdateRequestDto requestDto) {
        log.info("Service: Updating project with ID: {}", id);
        return projectRepository.findById(id).map(existingProject -> {
            existingProject.setName(requestDto.name());
            existingProject.setDescription(requestDto.description());
            return mapToResponseDto(projectRepository.save(existingProject));
        });
    }

    public boolean deleteProject(String id) {
        log.info("Service: Deleting project with ID: {}", id);
        if (!projectRepository.existsById(id)) {
            return false;
        }
        projectRepository.deleteById(id);
        return true;
    }

    @Transactional
    public void performProjectRollbackTest() {
        log.info("Starting transactional rollback test for project...");
        Project project = new Project();
        project.setName("Roll bak test Project");
        project.setDescription("Testing roll back behaviour");
        projectRepository.save(project);
        throw new RuntimeException("Simulated failure! Project transaction must roll back.");
    }

    private ProjectResponseDto mapToResponseDto(Project project) {
        return new ProjectResponseDto(project.getId(), project.getName(), project.getDescription());
    }
}
