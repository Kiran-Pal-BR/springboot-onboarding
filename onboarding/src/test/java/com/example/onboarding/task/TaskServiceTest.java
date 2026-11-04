package com.example.onboarding.task;

import com.example.onboarding.exception.DuplicateResourceException;
import com.example.onboarding.exception.ProjectNotFoundException;
import com.example.onboarding.project.Project;
import com.example.onboarding.project.ProjectRepository;
import com.example.onboarding.task.dto.TaskCreateRequestDto;
import com.example.onboarding.task.dto.TaskResponseDto;
import com.example.onboarding.task.dto.TaskUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private Project sampleProject;
    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleProject = new Project();
        sampleProject.setName("Test Project");
        sampleProject.setDescription("Description");
        sampleTask = new Task("Sample Task", "Sample Description", false, sampleProject);
    }

    // ==================== CREATE TESTS ====================

    @Test
    void createTask_Success() {
        TaskCreateRequestDto requestDto = new TaskCreateRequestDto(
                "New Task",
                "Task Description",
                sampleProject.getId()
        );
        when(taskRepository.existsByTitle(requestDto.title())).thenReturn(false);
        when(projectRepository.findById(requestDto.projectId())).thenReturn(Optional.of(sampleProject));
        Task savedTask = new Task(requestDto.title(), requestDto.description(), false, sampleProject);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        TaskResponseDto response = taskService.createTask(requestDto);
        assertNotNull(response);
        assertEquals("New Task", response.title());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_ThrowsDuplicateResourceException_WhenTitleExists() {
        TaskCreateRequestDto requestDto = new TaskCreateRequestDto(
                "Existing Task",
                "Description",
                sampleProject.getId()
        );
        when(taskRepository.existsByTitle(requestDto.title())).thenReturn(true);
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> taskService.createTask(requestDto)
        );
        assertEquals("Task with title 'Existing Task' already exists.", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_ThrowsProjectNotFoundException_WhenProjectMissing() {
        TaskCreateRequestDto requestDto = new TaskCreateRequestDto(
                "New Task",
                "Description",
                "invalid-project-id"
        );

        when(taskRepository.existsByTitle(requestDto.title())).thenReturn(false);
        when(projectRepository.findById(requestDto.projectId())).thenReturn(Optional.empty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> taskService.createTask(requestDto)
        );
    }

    // ==================== PUT TEST ====================

    @Test
    void updateTaskFull_Success() {
        String taskId = "task-id-123";
        TaskUpdateRequestDto requestDto = new TaskUpdateRequestDto(
                "Updated Title",
                "Updated Description",
                true
        );
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
        Optional<TaskResponseDto> response = taskService.updateTaskFull(taskId, requestDto);
        assertTrue(response.isPresent());
        assertEquals("Updated Title", response.get().title());
        assertTrue(response.get().completed());
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    void updateTaskFull_ReturnsEmpty_WhenTaskNotFound() {
        String taskId = "non-existent-id";
        TaskUpdateRequestDto requestDto = new TaskUpdateRequestDto(
                "Title",
                "Desc",
                true
        );
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        Optional<TaskResponseDto> response = taskService.updateTaskFull(taskId, requestDto);
        assertTrue(response.isEmpty());
        verify(taskRepository, never()).save(any(Task.class));
    }

    // ==================== PATCH TEST ====================

    @Test
    void patchTask_Success() {
        String taskId = "task-id-123";
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "Partially Updated Title");
        updates.put("completed", true);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);
        Optional<TaskResponseDto> response = taskService.patchTask(taskId, updates);
        assertTrue(response.isPresent());
        assertEquals("Partially Updated Title", response.get().title());
        assertTrue(response.get().completed());
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    void patchTask_ReturnsEmpty_WhenTaskNotFound() {
        String taskId = "non-existent-id";
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", "New Title");
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        Optional<TaskResponseDto> response = taskService.patchTask(taskId, updates);

        assertTrue(response.isEmpty());
        verify(taskRepository, never()).save(any(Task.class));
    }

    // ==================== DELETE TESTS ====================

    @Test
    void deleteTask_Success() {
        String taskId = "task-id-123";
        when(taskRepository.existsById(taskId)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(taskId);
        boolean deleted = taskService.deleteTask(taskId);
        assertTrue(deleted);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void deleteTask_ReturnsFalse_WhenTaskNotFound() {
        String taskId = "non-existent-id";
        when(taskRepository.existsById(taskId)).thenReturn(false);
        boolean deleted = taskService.deleteTask(taskId);
        assertFalse(deleted);
        verify(taskRepository, never()).deleteById(anyString());
    }
}