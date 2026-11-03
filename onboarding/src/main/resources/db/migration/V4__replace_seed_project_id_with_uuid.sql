-- Replace the original non-UUID seed ID while keeping task references intact.
INSERT INTO projects (id, name, description, created_at)
SELECT 'f19d73a8-2c6e-4a51-9d37-8f0c6b2a4e91', name, description, created_at
FROM projects
WHERE id = 'proj-1'
ON CONFLICT (id) DO NOTHING;

UPDATE tasks
SET project_id = 'f19d73a8-2c6e-4a51-9d37-8f0c6b2a4e91'
WHERE project_id = 'proj-1';

DELETE FROM projects
WHERE id = 'proj-1';
