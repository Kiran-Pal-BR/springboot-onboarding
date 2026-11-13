

CREATE TABLE projects (
                          id VARCHAR(255) PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          description TEXT
);

CREATE TABLE tasks (
                       id VARCHAR(255) PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description TEXT,
                       completed BOOLEAN NOT NULL DEFAULT FALSE,
                       project_id VARCHAR(255),
                       CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);