package gr.uom.Service.Based.Assesment.controllers;

import gr.uom.Service.Based.Assesment.model.Project;
import gr.uom.Service.Based.Assesment.model.ProjectAnalysis;
import gr.uom.Service.Based.Assesment.model.ProjectFile;
import gr.uom.Service.Based.Assesment.service.ProjectAnalysisService;
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

    @Autowired
    private ProjectAnalysisService appProjectAnalysisService;

    @CrossOrigin(origins = "*")
    @PostMapping("/")
    public ResponseEntity<Project> startAnalysis(@RequestParam("gitUrl") String gitUrl) throws Exception {
        Project resultProject = appProjectService.runCommand(gitUrl, null,true);
        appProjectService.saveProject(resultProject);
        return ResponseEntity.ok(resultProject);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/")
    public ResponseEntity<Project> getProjectByGitUrl(@RequestParam("gitUrl") String gitUrl) {
        Optional<Project> project = appProjectService.getProjectByGitUrl(gitUrl);
        List<ProjectAnalysis> projectAnalysis = appProjectAnalysisService.getProjectAnalysisByProjectName(project.get().getName());
        project.get().setProjectAnalysis(projectAnalysis);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/")
    public ResponseEntity<String> deleteProject(@RequestParam("gitUrl") String gitUrl) {
        appProjectService.deleteProject(gitUrl);
        return ResponseEntity.ok("Project with Git URL " + gitUrl + " has been deleted");
    }

    @CrossOrigin(origins = "*")
    @PutMapping("/")
    public ResponseEntity<Project> updateProject(@RequestParam("projectId") Long projectId, @RequestParam("gitUrl") String gitUrl) throws Exception {
        Optional<Project> project = appProjectService.getProjectById(projectId);
        if (project.isPresent()) {
            Project updatedProject = appProjectService.runCommand(gitUrl, "main",true);
            return ResponseEntity.ok(updatedProject);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}