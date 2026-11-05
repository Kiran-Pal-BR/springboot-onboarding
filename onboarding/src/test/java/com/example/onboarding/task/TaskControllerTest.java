package com.example.onboarding.task;
import com.example.onboarding.controller.TaskController;
import com.example.onboarding.exception.DuplicateResourceException;
import com.example.onboarding.task.dto.TaskCreateRequestDto;
import com.example.onboarding.task.dto.TaskResponseDto;
import com.example.onboarding.task.dto.TaskUpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    private TaskResponseDto sampleResponseDto;

    @BeforeEach
    void setUp() {
        sampleResponseDto = new TaskResponseDto(
                "task-123",
                "Sample Task",
                "Task Description",
                false,
                "proj-123"
        );
    }

    // ================ GET ALL TASKS (WITH FILTER & PAGINATION) ===============================================

    @Test
    void getAllTasks_ReturnsOk_WithPaginatedList() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskResponseDto> taskPage = new PageImpl<>(List.of(sampleResponseDto), pageable, 1);
        when(taskService.getAllTasks(eq(false), any(Pageable.class))).thenReturn(taskPage);
        mockMvc.perform(get("/api/tasks")
                        .param("completed", "false")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("task-123"))
                .andExpect(jsonPath("$.content[0].title").value("Sample Task"));
        verify(taskService, times(1)).getAllTasks(eq(false), any(Pageable.class));
    }

    // ====================GET TASK BY ID ========================================================================

    @Test
    void getTaskById_ReturnsOk_WhenFound() throws Exception {
        String taskId = "task-123";
        when(taskService.getTaskById(taskId)).thenReturn(Optional.of(sampleResponseDto));
        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("task-123"))
                .andExpect(jsonPath("$.title").value("Sample Task"));
        verify(taskService, times(1)).getTaskById(taskId);
    }

    @Test
    void getTaskById_ReturnsNotFound_WhenMissing() throws Exception {
        String taskId = "non-existent";
        when(taskService.getTaskById(taskId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTaskById(taskId);
    }

    // ================= CREATE TASK ==========================================================================

    @Test
    void createTask_ReturnsCreated_WhenValidRequest() throws Exception {
        TaskCreateRequestDto requestDto = new TaskCreateRequestDto(
                "Sample Task",
                "Task Description",
                "proj-123"
        );
        when(taskService.createTask(any(TaskCreateRequestDto.class))).thenReturn(sampleResponseDto);
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("task-123"))
                .andExpect(jsonPath("$.title").value("Sample Task"));
        verify(taskService, times(1)).createTask(any(TaskCreateRequestDto.class));
    }

    @Test
    void createTask_ReturnsConflict_WhenDuplicateTitle() throws Exception {
        TaskCreateRequestDto requestDto = new TaskCreateRequestDto(
                "Sample Task",
                "Description",
                "proj-123"
        );
        when(taskService.createTask(any(TaskCreateRequestDto.class)))
                .thenThrow(new DuplicateResourceException("Task with title 'Sample Task' already exists."));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict());

        verify(taskService, times(1)).createTask(any(TaskCreateRequestDto.class));
    }

    // ==================== UPDATE TASK (PUT) =======================================================================

    @Test
    void updateTaskFull_ReturnsOk_WhenValid() throws Exception {
        String taskId = "task-123";
        TaskUpdateRequestDto requestDto = new TaskUpdateRequestDto(
                "Updated Title",
                "Updated Desc",
                true
        );
        TaskResponseDto updatedDto = new TaskResponseDto(taskId, "Updated Title", "Updated Desc", true, "proj-123");
        when(taskService.updateTaskFull(eq(taskId), any(TaskUpdateRequestDto.class)))
                .thenReturn(Optional.of(updatedDto));
        mockMvc.perform(put("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.completed").value(true));
        verify(taskService, times(1)).updateTaskFull(eq(taskId), any(TaskUpdateRequestDto.class));
    }

    @Test
    void updateTaskFull_ReturnsNotFound_WhenMissing() throws Exception {
        String taskId = "non-existent";
        TaskUpdateRequestDto requestDto = new TaskUpdateRequestDto("Title", "Desc", true);
        when(taskService.updateTaskFull(eq(taskId), any(TaskUpdateRequestDto.class)))
                .thenReturn(Optional.empty());
        mockMvc.perform(put("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
        verify(taskService, times(1)).updateTaskFull(eq(taskId), any(TaskUpdateRequestDto.class));
    }

    // =================== PATCH TASK =================================================================================

    @Test
    void patchTask_ReturnsOk_WhenValidUpdates() throws Exception {
        String taskId = "task-123";
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "Patched Title");
        when(taskService.patchTask(eq(taskId), any(Map.class)))
                .thenReturn(Optional.of(sampleResponseDto));
        mockMvc.perform(patch("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk());
        verify(taskService, times(1)).patchTask(eq(taskId), any(Map.class));
    }

    @Test
    void patchTask_ReturnsBadRequest_WhenEmptyMap() throws Exception {
        String taskId = "task-123";
        mockMvc.perform(patch("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
        verify(taskService, never()).patchTask(anyString(), any(Map.class));
    }

    // ==================== DELETE TASK =============================================================================

    @Test
    void deleteTask_ReturnsNoContent_WhenSuccessful() throws Exception {
        String taskId = "task-123";
        when(taskService.deleteTask(taskId)).thenReturn(true);

        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());
        verify(taskService, times(1)).deleteTask(taskId);
    }

    @Test
    void deleteTask_ReturnsNotFound_WhenMissing() throws Exception {
        String taskId = "non-existent";
        when(taskService.deleteTask(taskId)).thenReturn(false);
        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNotFound());
        verify(taskService, times(1)).deleteTask(taskId);
    }

    // ===================TEST ROLLBACK ENDPOINT =====================================================================

    @Test
    void testRollback_ReturnsOk() throws Exception {
        doNothing().when(taskService).performBulkOperationWithRollBack();
        mockMvc.perform(post("/api/tasks/test-rollback"))
                .andExpect(status().isOk());
        verify(taskService, times(1)).performBulkOperationWithRollBack();
    }
}