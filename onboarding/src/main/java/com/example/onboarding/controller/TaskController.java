package com.example.onboarding.controller;

import com.example.onboarding.task.TaskService;
import com.example.onboarding.task.dto.TaskCreateRequestDto;
import com.example.onboarding.task.dto.TaskResponseDto;
import com.example.onboarding.task.dto.TaskUpdateRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    // Constructor Injection (Spring Boot automatically injects TaskService)
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponseDto> getAllTasks(@RequestParam(required = false) Boolean completed) {
        log.info("Controller: GET /api/tasks called");
        return taskService.getAllTasks(completed);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable String id) {
        log.info("Controller: GET /api/tasks/{} called", id);
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskCreateRequestDto requestDto) {
        log.info("Controller: POST /api/tasks called");
        TaskResponseDto createdTask = taskService.createTask(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTaskFull(
            @PathVariable String id,
            @Valid @RequestBody TaskUpdateRequestDto requestDto
    ){
        log.info("Controller : PUT /api/tasks/{} called",id);
        return taskService.updateTaskFull(id,requestDto)
                .map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponseDto> patchTask(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates
    ) {
        log.info("Controller: PATCH /api/tasks/{} called", id);

        if (updates == null || updates.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return taskService.patchTask(id, updates)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteTask(@PathVariable String id){
        log.info("Controller: DELETE /api/tasks/{} called ",id );
        boolean deleted = taskService.deleteTask(id);
        if(!deleted){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }
}