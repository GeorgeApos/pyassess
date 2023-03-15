package gr.uom.Service.Based.Assesment.controllers;

import gr.uom.Service.Based.Assesment.model.ProjectAnalysis;
import gr.uom.Service.Based.Assesment.model.ProjectFile;
import gr.uom.Service.Based.Assesment.repository.ProjectFileRepository;
import gr.uom.Service.Based.Assesment.repository.ProjectAnalysisRepository;
import gr.uom.Service.Based.Assesment.service.ProjectAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/project_analysis")
public class ProjectAnalysisController {

    @Autowired
    private ProjectAnalysisService appProjectService;

    @Autowired
    private ProjectAnalysisRepository projectAnalysisRepository;

    @Autowired
    private ProjectFileRepository projectFileRepository;


//    @CrossOrigin(origins = "*")
//    @PostMapping("/")
//    public ResponseEntity<ProjectAnalysis> handleSimpleRequest(@RequestParam("gitUrl") String gitUrl) throws Exception {
//        ProjectAnalysis savedProjectAnalysis = appProjectService.runCommand(null, gitUrl);
//        appProjectService.saveProject(savedProjectAnalysis);
//        return ResponseEntity.ok(savedProjectAnalysis);
//    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/")
    public ResponseEntity<String> handleDeleteRequest(@RequestParam("projectId") Long projectId) {
        appProjectService.deleteProject(projectId);
        return ResponseEntity.ok("Project with ID " + projectId + " has been deleted");
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/")
    public ResponseEntity<ProjectAnalysis> getProjectByGitUrl(@RequestParam("gitUrl") String gitUrl) {
        Optional<ProjectAnalysis> project = appProjectService.getProjectByGitUrl(gitUrl);
        List<ProjectFile> files = appProjectService.getProjectFilesByName(project.get().getName());
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
        return appProjectService.getAllProjects();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/files")
    public List<ProjectFile> getAllProjectFiles() {
        return appProjectService.getAllProjectFiles();
    }
}
