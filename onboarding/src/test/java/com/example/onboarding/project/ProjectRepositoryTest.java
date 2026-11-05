package com.example.onboarding.project;
import com.example.onboarding.task.Task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    // ===================Exist By Name Test ====================

    @Test
    void existsByName_ReturnsTrue_WhenProjectExists() {
        // Arrange
        Project project = new Project();
        project.setName("Enterprise Alpha");
        project.setDescription("Testing uniqueness constraint");
        entityManager.persistAndFlush(project);

        // Act
        boolean exists = projectRepository.existsByName("Enterprise Alpha");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_ReturnsFalse_WhenProjectDoesNotExist() {
        // Act
        boolean exists = projectRepository.existsByName("NonExistent Project");

        // Assert
        assertThat(exists).isFalse();
    }

    // ==================== Find By Id with Tasks (FETCH JOIN) ====================

    @Test
    void findByIdWithTasks_ReturnsProjectWithTasks_WhenValidId() {
        // Arrange
        Project project = new Project();
        project.setName("Project With Tasks");
        project.setDescription("Testing fetch join by ID");



        Project savedProject = entityManager.persistAndFlush(project);
        entityManager.clear(); // Clear cache to ensure query hits database

        // Act
        Optional<Project> foundProject = projectRepository.findByIdWithTasks(savedProject.getId());

        // Assert
        assertThat(foundProject).isPresent();
        assertThat(foundProject.get().getName()).isEqualTo("Project With Tasks");
    }

    // ==================== Find All with Tasks (FETCH JOIN) ====================

    @Test
    void findAllWithTasks_ReturnsListOfProjectsWithTasks() {
        // Arrange
        Project project1 = new Project();
        project1.setName("Project One");
        project1.setDescription("First project");
        entityManager.persist(project1);

        Project project2 = new Project();
        project2.setName("Project Two");
        project2.setDescription("Second project");
        entityManager.persistAndFlush(project2);

        entityManager.clear(); // Clear cache

        // Act
        List<Project> projects = projectRepository.findAllWithTasks();

        // Assert
        assertThat(projects).isNotEmpty();
        assertThat(projects.size()).isGreaterThanOrEqualTo(2);
        assertThat(projects).extracting(Project::getName)
                .contains("Project One", "Project Two");
    }
}
