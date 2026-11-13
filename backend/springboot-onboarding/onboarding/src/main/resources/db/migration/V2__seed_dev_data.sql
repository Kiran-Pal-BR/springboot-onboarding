-- V2__seed_dev_data.sql: Insert initial seed data for development

INSERT INTO projects (id, name, description) VALUES
    ('proj-1', 'Backend Onboarding', 'Spring Boot enterprise architecture tasks and milestones.');


INSERT INTO tasks (id, title, description, completed, project_id) VALUES
                                                                      ('task-1', 'Setup PostgreSQL', 'Migrate from H2 to PostgreSQL and fix peer authentication.', TRUE, 'proj-1'),
                                                                      ('task-2', 'Implement DTO Isolation', 'Ensure zero entity leakage in controllers.', TRUE, 'proj-1'),
                                                                      ('task-3', 'Flyway Migrations', 'Set up versioned migrations and ddl-auto=validate.', FALSE, 'proj-1');