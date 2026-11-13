package com.example.onboarding.project;
import com.example.onboarding.controller.ProjectController;
import com.example.onboarding.exception.DuplicateResourceException;
import com.example.onboarding.project.dto.ProjectCreateRequestDto;
import com.example.onboarding.project.dto.ProjectResponseDto;
import com.example.onboarding.project.dto.ProjectUpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import  org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;
    private ProjectResponseDto sampleResponseDto;


    @BeforeEach
    void setUp(){
        sampleResponseDto = new ProjectResponseDto(
                "proj-123",
                "Enterprise Alpha",
                "Enterprise onboarding project"
        );
    }

    // ======================Get All Projects======================================
    @Test
    void getAllProjects_ReturnsOk_WithPaginatedList() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProjectResponseDto> projectPage = new PageImpl<>(List.of(sampleResponseDto), pageable, 1);
        when(projectService.getAllProjects(any(Pageable.class))).thenReturn(projectPage);
        mockMvc.perform(get("/api/projects")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("proj-123"))
                .andExpect(jsonPath("$.content[0].name").value("Enterprise Alpha"));

        verify(projectService, times(1)).getAllProjects(any(Pageable.class));
    }

    //==========================Create Project========================================

    @Test
    void createProject_ReturnsCreated_WhenValidRequest() throws Exception {
        ProjectCreateRequestDto requestDto = new ProjectCreateRequestDto(
                "Enterprise Alpha",
                "Enterprise onboarding project"
        );
        when(projectService.createProject(any(ProjectCreateRequestDto.class))).thenReturn(sampleResponseDto);
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("proj-123"))
                .andExpect(jsonPath("$.name").value("Enterprise Alpha"));

        verify(projectService, times(1)).createProject(any(ProjectCreateRequestDto.class));
    }

    @Test
    void createProject_ReturnsConflict_WhenDuplicateName() throws Exception {
        ProjectCreateRequestDto requestDto = new ProjectCreateRequestDto(
                "Enterprise Alpha",
                "Duplicate description"
        );
        when(projectService.createProject(any(ProjectCreateRequestDto.class)))
                .thenThrow(new DuplicateResourceException("Project with name 'Enterprise Alpha' already exists."));

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict());
        verify(projectService, times(1)).createProject(any(ProjectCreateRequestDto.class));
    }

    //===========================Get Project By ID==================================================================

    @Test
    void getProjectById_ReturnsOk_WhenFound() throws Exception {
        String projectId = "proj-123";
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(sampleResponseDto));

        mockMvc.perform(get("/api/projects/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("proj-123"))
                .andExpect(jsonPath("$.name").value("Enterprise Alpha"));

        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    void getProjectById_ReturnsNotFound_WhenMissing() throws Exception {
        String projectId = "non-existent";
        when(projectService.getProjectById(projectId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/{id}", projectId))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).getProjectById(projectId);
    }

    //=============================Update Project============================================================================
    @Test
    void updateProject_ReturnsOk_WhenValid() throws Exception {
        String projectId = "proj-123";
        ProjectUpdateRequestDto requestDto = new ProjectUpdateRequestDto(
                "Updated Name",
                "Updated Desc"
        );
        ProjectResponseDto updatedDto = new ProjectResponseDto(projectId, "Updated Name", "Updated Desc");
        when(projectService.updateProject(eq(projectId), any(ProjectUpdateRequestDto.class)))
                .thenReturn(Optional.of(updatedDto));
        mockMvc.perform(put("/api/projects/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));

        verify(projectService, times(1)).updateProject(eq(projectId), any(ProjectUpdateRequestDto.class));
    }

    @Test
    void updateProject_ReturnsNotFound_WhenMissing() throws Exception {
        String projectId = "non-existent";
        ProjectUpdateRequestDto requestDto = new ProjectUpdateRequestDto("Name", "Desc");

        when(projectService.updateProject(eq(projectId), any(ProjectUpdateRequestDto.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/projects/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).updateProject(eq(projectId), any(ProjectUpdateRequestDto.class));
    }

    //==========================Delete Project===================================================================================================
    @Test
    void deleteProject_ReturnsNoContent_WhenSuccessful() throws Exception {
        String projectId = "proj-123";
        when(projectService.deleteProject(projectId)).thenReturn(true);

        mockMvc.perform(delete("/api/projects/{id}", projectId))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).deleteProject(projectId);
    }

    @Test
    void deleteProject_ReturnsNotFound_WhenMissing() throws Exception {
        String projectId = "non-existent";
        when(projectService.deleteProject(projectId)).thenReturn(false);

        mockMvc.perform(delete("/api/projects/{id}", projectId))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).deleteProject(projectId);
    }

    //========================Test Rollback End-Point==============================================================================
    @Test
    void testRollback_ReturnsOk() throws Exception {
        doNothing().when(projectService).performProjectRollbackTest();

        mockMvc.perform(post("/api/projects/test-rollback"))
                .andExpect(status().isOk());

        verify(projectService, times(1)).performProjectRollbackTest();
    }
}


