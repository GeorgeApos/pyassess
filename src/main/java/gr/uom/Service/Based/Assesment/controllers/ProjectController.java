package gr.uom.Service.Based.Assesment.controllers;

import gr.uom.Service.Based.Assesment.model.Project;
import gr.uom.Service.Based.Assesment.model.ProjectAnalysis;
import gr.uom.Service.Based.Assesment.model.ProjectFile;
import gr.uom.Service.Based.Assesment.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    private ProjectService appProjectService;

    @CrossOrigin(origins = "*")
    @PostMapping("/")
    public ResponseEntity<Project> startAnalysis(@RequestParam("gitUrl") String gitUrl) throws Exception {
        Project resultProject = appProjectService.runCommand(gitUrl);
        appProjectService.saveProject(resultProject);
        return ResponseEntity.ok(resultProject);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/")
    public ResponseEntity<Project> getProjectByGitUrl(@RequestParam("gitUrl") String gitUrl) {
        Optional<Project> project = appProjectService.getProjectByGitUrl(gitUrl);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    

}
