package com.example.onboarding.task;

import com.example.onboarding.task.dto.TaskCreateRequestDto;
import com.example.onboarding.task.dto.TaskResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    // In-memory thread-safe store
    private final Map<String, Task> taskStore = new ConcurrentHashMap<>();

    // 1. Get All Tasks (with optional filter & mapping to DTO)
    public List<TaskResponseDto> getAllTasks(Boolean completed) {
        log.info("Service: Fetching all tasks. Filter completed: {}", completed);

        Collection<Task> tasks = taskStore.values();
        return tasks.stream()
                .filter(task -> completed == null || task.completed() == completed)
                .map(this::mapToResponseDto)
                .toList();
    }

    // 2. Get Task By ID
    public Optional<TaskResponseDto> getTaskById(String id) {
        log.info("Service: Fetching task by ID: {}", id);
        Task task = taskStore.get(id);
        if (task == null) {
            return Optional.empty();
        }
        return Optional.of(mapToResponseDto(task));
    }

    // 3. Create Task
    public TaskResponseDto createTask(TaskCreateRequestDto requestDto) {
        log.info("Service: Creating new task with title: {}", requestDto.title());

        String id = UUID.randomUUID().toString();
        Task newTask = new Task(id, requestDto.title(), requestDto.description(), false);

        taskStore.put(id, newTask);
        log.info("Service: Successfully created task with ID: {}", id);

        return mapToResponseDto(newTask);
    }

    // 4. Update Task Status (Patch)
    public Optional<TaskResponseDto> patchTask(String id, Map<String, Object> updates) {
        log.info("Service: Patching task ID: {}", id);

        Task existingTask = taskStore.get(id);
        if (existingTask == null) {
            return Optional.empty();
        }

        // Extract values safely from the map
        String newTitle = (String) updates.get("title");
        String newDescription = (String) updates.get("description");
        Boolean completedObj = (Boolean) updates.get("completed");

        // Determine updated values (keep existing if field not provided)
        String updatedTitle = (newTitle != null) ? newTitle : existingTask.title();
        String updatedDescription = (newDescription != null) ? newDescription : existingTask.description();

        boolean updatedCompleted = existingTask.completed();
        if (completedObj != null) {
            updatedCompleted = completedObj;
        }

        Task updatedTask = new Task(
                existingTask.id(),
                updatedTitle,
                updatedDescription,
                updatedCompleted
        );

        taskStore.put(id, updatedTask);
        return Optional.of(mapToResponseDto(updatedTask));
    }

    // 5 For the full update (PUT)
    public Optional<TaskResponseDto> updateTaskFull(String id, com.example.onboarding.task.dto.TaskUpdateRequestDto requestDto){
        log.info("Service: Fully updating task ID: {}", id);

        Task existingTask  = taskStore.get(id);
        if(existingTask == null){
            return Optional.empty();
        }
        Task updatedTask = new Task(
                id,
                requestDto.title(),
                requestDto.description(),
                requestDto.completed()
        );
        taskStore.put(id, updatedTask);
        return Optional.of(mapToResponseDto(updatedTask));
    }

    //Delete the task
    public boolean deleteTask(String id){
        log.info("Service : Deleting task ID: {} ",id);
        return taskStore.remove(id)!=null;
    }

    // Helper method to convert Entity -> DTO
    private TaskResponseDto mapToResponseDto(Task task) {
        return new TaskResponseDto(
                task.id(),
                task.title(),
                task.description(),
                task.completed()
        );
    }
}