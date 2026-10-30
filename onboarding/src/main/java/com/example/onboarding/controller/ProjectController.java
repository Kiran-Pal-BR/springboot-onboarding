package com.example.onboarding.project;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;




@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository){
        this.projectRepository = projectRepository;
    }

    //get all project
    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    //create a project
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project){
        Project savedProject = projectRepository.save(project);
        return ResponseEntity.ok(savedProject);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Project>getProjectById(@PathVariable String id){
        Project project = projectRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Project not found with the id" + id));
        return ResponseEntity.ok(project);

    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<Project>deleteProject(@PathVariable String id){
        projectRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
