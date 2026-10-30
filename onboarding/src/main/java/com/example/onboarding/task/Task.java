package com.example.onboarding.task;
import com.example.onboarding.project.Project;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "tasks")


public class Task {

    @Id
    private String id;

    @Column(nullable = false, length=100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean completed;

    //Many task belongs to one project.

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Task() {}

    public Task(String id, String title, String description, Boolean completed, Project project){
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.project = project;
    }

    //getters and setters

    public String getId(){return id; }
    public void setId(String id) {this.id = id;}

    public String getTitle() {return  title;}
    public void  setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public  boolean isCompleted() {return  completed;}
    public void setCompleted(boolean completed) {this.completed = completed;}






}
