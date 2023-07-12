package gr.uom.Service.Based.Assesment.controllers;

import gr.uom.Service.Based.Assesment.model.Project;
import gr.uom.Service.Based.Assesment.model.ProjectAnalysis;
import gr.uom.Service.Based.Assesment.model.ProjectFile;
import gr.uom.Service.Based.Assesment.repository.ProjectFileRepository;
import gr.uom.Service.Based.Assesment.repository.ProjectAnalysisRepository;
import gr.uom.Service.Based.Assesment.repository.ProjectRepository;
import gr.uom.Service.Based.Assesment.service.ProjectAnalysisService;
import gr.uom.Service.Based.Assesment.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/project_analysis")
public class ProjectAnalysisController {

    @Autowired
    private ProjectRepository ProjectRepository;

    @Autowired
    private ProjectService appProjectService;

    @Autowired
    private ProjectAnalysisService appProjectAnalysisService;

    @CrossOrigin(origins = "*")
    @PostMapping("/")
    public ResponseEntity<Project> handleSimpleRequest(@RequestParam("gitUrl") String gitUrl, @RequestParam("branch") String branch) throws Exception {
        Project resultProject = appProjectService.runCommand(gitUrl, branch,true);
        appProjectService.saveProject(resultProject);
        return ResponseEntity.ok(resultProject);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/")
    public ResponseEntity<String> handleDeleteRequest(@RequestParam("projectId") Long projectId) {
        appProjectAnalysisService.deleteProject(projectId);
        return ResponseEntity.ok("Project with ID " + projectId + " has been deleted");
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/")
    public ResponseEntity<ProjectAnalysis> getProjectByGitUrl(@RequestParam("gitUrl") String gitUrl) {
        Optional<ProjectAnalysis> project = appProjectAnalysisService.getProjectByGitUrl(gitUrl);
        List<ProjectFile> files = appProjectAnalysisService.getProjectFilesByName(project.get().getName());
        project.get().setFiles(files);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/all")
    public List<ProjectAnalysis> getAllProjects() {
        return appProjectAnalysisService.getAllProjects();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/files")
    public List<ProjectFile> getAllProjectFiles() {
        return appProjectAnalysisService.getAllProjectFiles();
    }
}
