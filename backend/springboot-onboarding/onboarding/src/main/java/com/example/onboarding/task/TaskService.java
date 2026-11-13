package com.example.onboarding.task;

import com.example.onboarding.exception.DuplicateResourceException;
import com.example.onboarding.project.Project;
import com.example.onboarding.project.ProjectRepository;
import com.example.onboarding.task.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.onboarding.task.dto.TaskCreateRequestDto;
import com.example.onboarding.task.dto.TaskResponseDto;
import com.example.onboarding.task.dto.TaskUpdateRequestDto;
import com.example.onboarding.exception.ProjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;




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
    public Page<TaskResponseDto> getAllTasks(Boolean completed, Pageable pageable) {
        log.info("Service: Fetching paginated tasks from database. Filter completed: {}", completed);

        if(completed != null){
            return taskRepository.findByCompleted(completed, pageable)
                    .map(this::mapToResponseDto);
        }

        return taskRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    // 2. Get Task By ID
    public Optional<TaskResponseDto> getTaskById(String id) {
        log.info("Service: Fetching task by ID: {}", id);
        return taskRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    //3. Get task by filter
    public List<TaskResponseDto>getTaskByFilter(Boolean completed, String projectId ){
        log.info("Service: fetch tasks  with filters - completed: {},projectId:{}",completed,projectId);

        List<Task> tasks;
        if(projectId != null && completed != null){
            tasks = taskRepository.findByProjectId(projectId).stream()
                    .filter(t -> t.isCompleted() == completed)
                    .toList();
        }else if(projectId !=null){
            tasks = taskRepository.findByProjectId(projectId);
        }else if(completed != null){
            tasks = taskRepository.findByCompleted(completed);
        }else{
            tasks = taskRepository.findAll();
        }
        return tasks.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // 4. Create Task (Linked with a Project)
    public TaskResponseDto createTask(TaskCreateRequestDto requestDto) {
        log.info("Service: Creating new task with title: {}", requestDto.title());

        // Checking for duplicate title:
        if (taskRepository.existsByTitle(requestDto.title())){
            throw new DuplicateResourceException("Task with title '" + requestDto.title() + "' already exists.");
        }
        Project project = projectRepository.findById(requestDto.projectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + requestDto.projectId()));


        Task newTask = new Task(
                requestDto.title(),
                requestDto.description(),
                false,
                project
        );

        Task savedTask = taskRepository.save(newTask);
        log.info("Service: Successfully created task with ID: {}", savedTask.getId());

        return mapToResponseDto(savedTask);
    }

    // 5. Update Task Status (Patch)
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

    // 6. Full update (PUT)
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

    // 7. Delete the task
    public boolean deleteTask(String id) {
        log.info("Service: Deleting task ID: {}", id);
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }


    @Transactional
    public void performBulkOperationWithRollBack(){
        log.info("Starting transactional bulk operation..");

        //Saving one task successfully
        Task newTask = new Task ("Sample Task for Rollback","Description",false , null);
        taskRepository.save(newTask);

        //Simulating failure
        boolean simulateError = true;
        if(simulateError){
            throw new RuntimeException("Simulated failure! Transaction must roll back" );
        }
    }

    // Helper method to convert Entity -> DTO (Handling project association safely)
    private TaskResponseDto mapToResponseDto(Task task) {
        String projectId = (task.getProject() != null) ? task.getProject().getId() : null;

        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                projectId

        );
    }


}
