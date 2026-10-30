package com.example.onboarding.task;

import com.example.onboarding.project.Project;
import com.example.onboarding.project.ProjectRepository;
import com.example.onboarding.task.dto.TaskCreateRequestDto;
import com.example.onboarding.task.dto.TaskResponseDto;
import com.example.onboarding.task.dto.TaskUpdateRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    // Constructor Injection for Spring Data Repositories
    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    // 1. Get All Tasks (with optional filter & mapping to DTO)
    public List<TaskResponseDto> getAllTasks(Boolean completed) {
        log.info("Service: Fetching all tasks from database. Filter completed: {}", completed);

        List<Task> tasks = taskRepository.findAll();

        return tasks.stream()
                .filter(task -> completed == null || task.isCompleted() == completed)
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // 2. Get Task By ID
    public Optional<TaskResponseDto> getTaskById(String id) {
        log.info("Service: Fetching task by ID: {}", id);
        return taskRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    // 3. Create Task (Linked with a Project)
    public TaskResponseDto createTask(TaskCreateRequestDto requestDto) {
        log.info("Service: Creating new task with title: {}", requestDto.title());

        // Ensure Project exists before creating task
        Project project = projectRepository.findById(requestDto.projectId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + requestDto.projectId()));

        String id = UUID.randomUUID().toString();
        Task newTask = new Task(
                id,
                requestDto.title(),
                requestDto.description(),
                false,
                project
        );

        Task savedTask = taskRepository.save(newTask);
        log.info("Service: Successfully created task with ID: {}", id);

        return mapToResponseDto(savedTask);
    }

    // 4. Update Task Status (Patch)
    public Optional<TaskResponseDto> patchTask(String id, Map<String, Object> updates) {
        log.info("Service: Patching task ID: {}", id);

        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            return Optional.empty();
        }

        Task existingTask = optionalTask.get();

        // Extract values safely from the map
        String newTitle = (String) updates.get("title");
        String newDescription = (String) updates.get("description");
        Boolean completedObj = (Boolean) updates.get("completed");

        // Update fields if present in map
        if (newTitle != null) existingTask.setTitle(newTitle);
        if (newDescription != null) existingTask.setDescription(newDescription);
        if (completedObj != null) existingTask.setCompleted(completedObj);

        Task updatedTask = taskRepository.save(existingTask);
        return Optional.of(mapToResponseDto(updatedTask));
    }

    // 5. Full update (PUT)
    public Optional<TaskResponseDto> updateTaskFull(String id, TaskUpdateRequestDto requestDto) {
        log.info("Service: Fully updating task ID: {}", id);

        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            return Optional.empty();
        }

        Task existingTask = optionalTask.get();
        existingTask.setTitle(requestDto.title());
        existingTask.setDescription(requestDto.description());
        existingTask.setCompleted(requestDto.completed());

        Task updatedTask = taskRepository.save(existingTask);
        return Optional.of(mapToResponseDto(updatedTask));
    }

    // 6. Delete the task
    public boolean deleteTask(String id) {
        log.info("Service: Deleting task ID: {}", id);
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Helper method to convert Entity -> DTO (Handling project association safely)
    private TaskResponseDto mapToResponseDto(Task task) {
        String projectId = (task.getProject() != null) ? task.getProject().getId() : null;

        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted()
                // Agar aapke TaskResponseDto mein projectId bhi hai, toh yahan pass kar sakte hain
        );
    }
}