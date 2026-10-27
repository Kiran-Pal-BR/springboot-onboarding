package com.example.onboarding.controller;

import com.example.onboarding.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;



@RestController
@RequestMapping("/api/tasks")

public class TaskController {
    public static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final Map<String, Task> taskStore = new ConcurrentHashMap<>();

    @GetMapping
    public List<Task>getAllTasks(@RequestParam(required = false) Boolean completed){
        log.info("Fetching all tasks. Filter completed:{}",completed);

        Collection<Task> tasks = taskStore.values();
        if(completed == null){
            return new ArrayList<>(tasks);
        }
        return tasks.stream()
                .filter(task -> task.completed() == completed)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task>getTaskById(@PathVariable String id){
        log.info("Fetching task by ID: {}",id);
        Task task = taskStore.get(id);

        if(task == null){
            log.warn("Task with ID {} not found",id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Task>createTask(@RequestBody Task newTask) {
        log.info("Creating new task with title: {}",newTask.title());

        String id = newTask.id() != null ? newTask.id() : UUID.randomUUID().toString();

        Task taskToSave = new Task(
                id,
                newTask.title(),
                newTask.description(),
                newTask.completed()
        );
        taskStore.put(id,taskToSave);
        log.info("Successfully created task with ID: {}",id);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskToSave);
    }

}
