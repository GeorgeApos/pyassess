package gr.uom.Service.Based.Assesment.controllers;

import gr.uom.Service.Based.Assesment.model.Project;
import gr.uom.Service.Based.Assesment.model.ProjectFile;
import gr.uom.Service.Based.Assesment.repository.ProjectFileRepository;
import gr.uom.Service.Based.Assesment.repository.ProjectRepository;
import gr.uom.Service.Based.Assesment.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
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

    @PostMapping("/")
    public ResponseEntity<Project> handleSimpleRequest() throws IOException, ExecutionException, InterruptedException {
        Project savedProject = projectRepository.save(appService.runCommand());
        return ResponseEntity.ok(savedProject);
    }

    @GetMapping("/")
    public List<Project> getAllProjects() {
        return appService.getAllProjects();
    }

    @GetMapping("/files")
    public List<ProjectFile> getAllProjectFiles() {
        return appService.getAllProjectFiles();
    }
}
