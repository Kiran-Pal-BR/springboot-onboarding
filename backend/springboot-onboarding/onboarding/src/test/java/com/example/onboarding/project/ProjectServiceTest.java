package com.example.onboarding.project;

import com.example.onboarding.exception.DuplicateResourceException;
import com.example.onboarding.project.dto.ProjectCreateRequestDto;
import com.example.onboarding.project.dto.ProjectResponseDto;
import com.example.onboarding.project.dto.ProjectUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project sampleProject;

    @BeforeEach
    void setUp() {
        sampleProject = new Project();
        sampleProject.setId("proj-123");
        sampleProject.setName("Enterprise Alpha");
        sampleProject.setDescription("Enterprise onboarding project");
    }

    @Test
    void getAllProjectsReturnsMappedPage() {
        var pageable = PageRequest.of(0, 10);
        Page<Project> projects = new PageImpl<>(List.of(sampleProject), pageable, 1);
        when(projectRepository.findAll(pageable)).thenReturn(projects);
        Page<ProjectResponseDto> result = projectService.getAllProjects(pageable);
        assertEquals(1, result.getTotalElements());
        assertEquals("proj-123", result.getContent().get(0).id());
        assertEquals("Enterprise Alpha", result.getContent().get(0).name());
        verify(projectRepository).findAll(pageable);
    }

    @Test
    void getProjectByIdReturnsMappedProject() {
        when(projectRepository.findById("proj-123")).thenReturn(Optional.of(sampleProject));
        Optional<ProjectResponseDto> result = projectService.getProjectById("proj-123");
        assertTrue(result.isPresent());
        assertEquals("Enterprise Alpha", result.orElseThrow().name());
    }

    @Test
    void getProjectByIdReturnsEmptyWhenMissing() {
        when(projectRepository.findById("missing")).thenReturn(Optional.empty());
        assertTrue(projectService.getProjectById("missing").isEmpty());
    }

    @Test
    void createProjectSavesAndReturnsProject() {
        var request = new ProjectCreateRequestDto("Enterprise Alpha", "Enterprise onboarding project");
        when(projectRepository.existsByName(request.name())).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(sampleProject);
        ProjectResponseDto result = projectService.createProject(request);
        assertEquals("proj-123", result.id());
        assertEquals("Enterprise Alpha", result.name());
        verify(projectRepository).save(argThat(p -> p.getName().equals(request.name())
                && p.getDescription().equals(request.description())));
    }

    @Test
    void createProjectRejectsDuplicateName() {
        var request = new ProjectCreateRequestDto("Enterprise Alpha", "Duplicate");
        when(projectRepository.existsByName(request.name())).thenReturn(true);
        var exception = assertThrows(DuplicateResourceException.class,
                () -> projectService.createProject(request));
        assertEquals("Project with name 'Enterprise Alpha' already exists.", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void updateProjectUpdatesAndReturnsProject() {
        var request = new ProjectUpdateRequestDto("Updated name", "Updated description");
        when(projectRepository.findById("proj-123")).thenReturn(Optional.of(sampleProject));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Optional<ProjectResponseDto> result = projectService.updateProject("proj-123", request);
        assertTrue(result.isPresent());
        assertEquals("Updated name", result.orElseThrow().name());
        assertEquals("Updated description", result.orElseThrow().description());
    }

    @Test
    void updateProjectReturnsEmptyWhenMissing() {
        when(projectRepository.findById("missing")).thenReturn(Optional.empty());
        assertTrue(projectService.updateProject("missing",
                new ProjectUpdateRequestDto("Name", "Description")).isEmpty());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void deleteProjectDeletesExistingProject() {
        when(projectRepository.existsById("proj-123")).thenReturn(true);
        assertTrue(projectService.deleteProject("proj-123"));
        verify(projectRepository).deleteById("proj-123");
    }

    @Test
    void deleteProjectReturnsFalseWhenMissing() {
        when(projectRepository.existsById("missing")).thenReturn(false);
        assertFalse(projectService.deleteProject("missing"));
        verify(projectRepository, never()).deleteById(anyString());
    }

    @Test
    void performProjectRollbackTestThrowsAfterSaving() {
        when(projectRepository.save(any(Project.class))).thenReturn(sampleProject);
        var exception = assertThrows(RuntimeException.class, projectService::performProjectRollbackTest);
        assertEquals("Simulated failure! Project transaction must roll back.", exception.getMessage());
        verify(projectRepository).save(any(Project.class));
    }
}
