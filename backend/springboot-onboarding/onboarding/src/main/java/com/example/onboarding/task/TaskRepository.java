package com.example.onboarding.task;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task>findByProjectId(String projectId);
    List<Task>findByCompleted(boolean completed);
    Page<Task> findByCompleted(boolean completed, Pageable pageable);
    boolean existsByTitle(String title);
}