package com.example.onboarding.project;
import com.example.onboarding.task.Task;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "projects")


public class Project {

    @Id
    private String id;

    @Column(nullable = false, length=100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //One project and have many tasks
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    //default constructor
    public Project(){}

    public Project(String id, String name, String description, LocalDateTime createdAt){
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();

    }

    //getters and setters
    public String getId(){return id;}
    public void setId(String id) {this.id = id; }

    public String getName(){return name;}
    public void setName(String name){this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt(){return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}

    public List<Task>getTasks(){return tasks;}
    public void setTasks(List<Task> tasks){this.tasks = tasks;}

}
