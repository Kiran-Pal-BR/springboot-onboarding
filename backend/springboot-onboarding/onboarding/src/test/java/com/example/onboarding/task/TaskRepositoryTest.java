package com.example.onboarding.task;

import com.example.onboarding.project.Project;
import com.example.onboarding.project.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // ====================Find Project By Id =====================================================================

    @Test
    void findByProjectId_ReturnsListOfTasks_WhenTasksExistForProject() {
        // Arrange
        Project project = new Project();
        project.setName("Alpha Project");
        project.setDescription("Project description");
        Project savedProject = entityManager.persistAndFlush(project);
        Task task1 = new Task();
        task1.setTitle("Task One");
        task1.setDescription("Desc 1");
        task1.setCompleted(false);
        task1.setProject(savedProject);
        entityManager.persist(task1);
        Task task2 = new Task();
        task2.setTitle("Task Two");
        task2.setDescription("Desc 2");
        task2.setCompleted(true);
        task2.setProject(savedProject);
        entityManager.persistAndFlush(task2);
        entityManager.clear();

        // Act
        List<Task> tasks = taskRepository.findByProjectId(savedProject.getId());

        // Assert
        assertThat(tasks).hasSize(2);
        assertThat(tasks).extracting(Task::getTitle).contains("Task One", "Task Two");
    }

    // ==================== Find By Completed (List) ====================

    @Test
    void findByCompleted_ReturnsListOfTasks_MatchingStatus() {
        // Arrange
        Project project = new Project();
        project.setName("Beta Project");
        Project savedProject = entityManager.persistAndFlush(project);
        Task task1 = new Task();
        task1.setTitle("Pending Task");
        task1.setCompleted(false);
        task1.setProject(savedProject);
        entityManager.persist(task1);
        Task task2 = new Task();
        task2.setTitle("Done Task");
        task2.setCompleted(true);
        task2.setProject(savedProject);
        entityManager.persistAndFlush(task2);
        entityManager.clear();

        // Act
        List<Task> completedTasks = taskRepository.findByCompleted(true);

        // Assert
        assertThat(completedTasks).hasSize(1);
        assertThat(completedTasks.get(0).getTitle()).isEqualTo("Done Task");
    }

    // ==================== Find By Completed (Paginated) ====================

    @Test
    void findByCompleted_ReturnsPaginatedTasks_MatchingStatus() {
        // Arrange
        Project project = new Project();
        project.setName("Gamma Project");
        Project savedProject = entityManager.persistAndFlush(project);
        Task task = new Task();
        task.setTitle("Incomplete Paginated Task");
        task.setCompleted(false);
        task.setProject(savedProject);
        entityManager.persistAndFlush(task); // keeping reference
        Pageable pageable = PageRequest.of(0, 5);

        // Act
        Page<Task> taskPage = taskRepository.findByCompleted(false, pageable);

        // Assert
        assertThat(taskPage).isNotEmpty();
        assertThat(taskPage.getContent()).extracting(Task::getTitle)
                .contains("Incomplete Paginated Task");
    }

    // ==================== 4. EXISTS BY TITLE ====================

    @Test
    void existsByTitle_ReturnsTrue_WhenTitleExists() {
        // Arrange
        Project project = new Project();
        project.setName("Delta Project");
        Project savedProject = entityManager.persistAndFlush(project);
        Task task = new Task();
        task.setTitle("Unique Task Title");
        task.setCompleted(false);
        task.setProject(savedProject);
        entityManager.persistAndFlush(task);

        // Act
        boolean exists = taskRepository.existsByTitle("Unique Task Title");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByTitle_ReturnsFalse_WhenTitleDoesNotExist() {
        // Act
        boolean exists = taskRepository.existsByTitle("Non-Existent Title");

        // Assert
        assertThat(exists).isFalse();
    }
}
