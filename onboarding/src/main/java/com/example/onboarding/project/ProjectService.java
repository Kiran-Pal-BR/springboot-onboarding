package com.example.onboarding.project;

import com.example.onboarding.project.dto.ProjectResponseDto;
import com.example.onboarding.project.dto.ProjectCreateRequestDto;
import com.example.onboarding.project.dto.ProjectUpdateRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<ProjectResponseDto> getAllProjects() {
        log.info("Service: Fetching all projects");
        return projectRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<ProjectResponseDto> getProjectById(String id) {
        log.info("Service: Fetching project by ID: {}", id);
        return projectRepository.findById(id).map(this::mapToResponseDto);
    }

    public ProjectResponseDto createProject(ProjectCreateRequestDto requestDto) {
        log.info("Service: Creating project: {}", requestDto.name());
        Project project = new Project();
        project.setName(requestDto.name());
        project.setDescription(requestDto.description());

        Project savedProject = projectRepository.save(project);
        return mapToResponseDto(savedProject);
    }


    public Optional<ProjectResponseDto> updateProject(String id, ProjectUpdateRequestDto requestDto) {
        log.info("Service: Updating project with ID: {}", id);
        return projectRepository.findById(id)
                .map(existingProject -> {
                    existingProject.setName(requestDto.name());
                    existingProject.setDescription(requestDto.description());
                    Project updatedProject = projectRepository.save(existingProject);
                    return mapToResponseDto(updatedProject);
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

    private ProjectResponseDto mapToResponseDto(Project project) {
        return new ProjectResponseDto(
                project.getId(),
                project.getName(),
                project.getDescription()
        );
    }
}