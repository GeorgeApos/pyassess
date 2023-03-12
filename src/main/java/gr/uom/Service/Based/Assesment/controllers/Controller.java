package gr.uom.Service.Based.Assesment.controllers;

import gr.uom.Service.Based.Assesment.model.Project;
import gr.uom.Service.Based.Assesment.model.ProjectFile;
import gr.uom.Service.Based.Assesment.repository.ProjectFileRepository;
import gr.uom.Service.Based.Assesment.repository.ProjectRepository;
import gr.uom.Service.Based.Assesment.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/projects")
public class Controller {

    @Autowired
    private Service appService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectFileRepository projectFileRepository;

    @CrossOrigin(origins = "*")
    @PostMapping("/")
    public ResponseEntity<Project> handleSimpleRequest(@RequestParam("gitUrl") String gitUrl) throws Exception {
        Project savedProject = appService.runCommand(gitUrl);
        appService.saveProject(savedProject);
        return ResponseEntity.ok(savedProject);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/")
    public ResponseEntity<String> handleDeleteRequest(@RequestParam("projectId") Long projectId) {
        appService.deleteProject(projectId);
        return ResponseEntity.ok("Project with ID " + projectId + " has been deleted");
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/")
    public ResponseEntity<Project> getProjectByGitUrl(@RequestParam("gitUrl") String gitUrl) {
        Optional<Project> project = appService.getProjectByGitUrl(gitUrl);
        List<ProjectFile> files = appService.getProjectFilesByName(project.get().getName());
        project.get().setFiles(files);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/all")
    public List<Project> getAllProjects() {
        return appService.getAllProjects();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/files")
    public List<ProjectFile> getAllProjectFiles() {
        return appService.getAllProjectFiles();
    }
}
