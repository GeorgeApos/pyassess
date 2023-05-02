package gr.uom.Service.Based.Assesment.controllers;

import com.sun.tools.jconsole.JConsoleContext;
import gr.uom.Service.Based.Assesment.model.Comment;
import gr.uom.Service.Based.Assesment.model.Project;
import gr.uom.Service.Based.Assesment.model.ProjectAnalysis;
import gr.uom.Service.Based.Assesment.model.ProjectFile;
import gr.uom.Service.Based.Assesment.service.ProjectAnalysisService;
import gr.uom.Service.Based.Assesment.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.*;


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
        Project resultProject = appProjectService.runCommand(gitUrl, null, true);
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
            Project updatedProject = appProjectService.runCommand(gitUrl, null, true);
            return ResponseEntity.ok(updatedProject);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/ratings")
    public ResponseEntity<ArrayList<Double>> getRatings(@RequestParam("gitUrl") String gitUrl, @RequestParam("fileName") String fileName) {
        ArrayList<Double> responseRatingFiles = new ArrayList<>();
        Optional<Project> project = appProjectService.getProjectByGitUrl(gitUrl);
        List<ProjectFile> files = appProjectAnalysisService.getProjectFilesByName(project.get().getName());
        for (ProjectFile file : files) {
            if (file.getName().equals(fileName)) {
                responseRatingFiles.add(file.getRating());
            }
        }
        if (responseRatingFiles.size() > 0) {
            return ResponseEntity.ok(responseRatingFiles);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/coverage")
    public ResponseEntity<ArrayList<Integer>> getCoverage(@RequestParam("gitUrl") String gitUrl, @RequestParam("fileName") String fileName) {
        ArrayList<Integer> responseCoverageFiles = new ArrayList<>();
        Optional<Project> project = appProjectService.getProjectByGitUrl(gitUrl);
        List<ProjectFile> files = appProjectAnalysisService.getProjectFilesByName(project.get().getName());
        for (ProjectFile file : files) {
            if (file.getName().equals(fileName)) {
                responseCoverageFiles.add(file.getCoverage());
            }
        }
        if (responseCoverageFiles.size() > 0) {
            return ResponseEntity.ok(responseCoverageFiles);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @CrossOrigin(origins = "*")
    @GetMapping("/stmts")
    public ResponseEntity<ArrayList<Integer>> getStmts(@RequestParam("gitUrl") String gitUrl, @RequestParam("fileName") String fileName) {
        ArrayList<Integer> responseStmtsFiles = new ArrayList<>();
        Optional<Project> project = appProjectService.getProjectByGitUrl(gitUrl);
        List<ProjectFile> files = appProjectAnalysisService.getProjectFilesByName(project.get().getName());
        for (ProjectFile file : files) {
            if (file.getName().equals(fileName)) {
                responseStmtsFiles.add(file.getStmts());
            }
        }
        if (responseStmtsFiles.size() > 0) {
            return ResponseEntity.ok(responseStmtsFiles);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/miss")
    public ResponseEntity<ArrayList<Integer>> getMiss(@RequestParam("gitUrl") String gitUrl, @RequestParam("fileName") String fileName) {
        ArrayList<Integer> responseMissFiles = new ArrayList<>();
        Optional<Project> project = appProjectService.getProjectByGitUrl(gitUrl);
        List<ProjectFile> files = appProjectAnalysisService.getProjectFilesByName(project.get().getName());
        for (ProjectFile file : files) {
            if (file.getName().equals(fileName)) {
                responseMissFiles.add(file.getMiss());
            }
        }
        if (responseMissFiles.size() > 0) {
            return ResponseEntity.ok(responseMissFiles);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/dependencies")
    public ResponseEntity<ArrayList<Long>> getDep(@RequestParam("gitUrl") String gitUrl) {
        ArrayList<Long> responseDepFiles = new ArrayList<>();
        Optional<Project> project = appProjectService.getProjectByGitUrl(gitUrl);
        List<ProjectAnalysis> projectAnalysis = appProjectAnalysisService.getProjectAnalysisByProjectName(project.get().getName());
        for (ProjectAnalysis analysis : projectAnalysis) {
            responseDepFiles.add(analysis.getDependenciesCounter());
        }
        if (responseDepFiles.size() > 0) {
            return ResponseEntity.ok(responseDepFiles);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/totalcoverage")
    public ResponseEntity<ArrayList<Long>> getMiss(@RequestParam("gitUrl") String gitUrl) {
        ArrayList<Long> responseDepFiles = new ArrayList<>();
        Optional<Project> project = appProjectService.getProjectByGitUrl(gitUrl);
        List<ProjectAnalysis> projectAnalysis = appProjectAnalysisService.getProjectAnalysisByProjectName(project.get().getName());
        for (ProjectAnalysis analysis : projectAnalysis) {
            responseDepFiles.add(analysis.getDependenciesCounter());
        }
        if (responseDepFiles.size() > 0) {
            return ResponseEntity.ok(responseDepFiles);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/comments")
    public ResponseEntity<ArrayList<List<Comment>>> getComments(@RequestParam("gitUrl") String gitUrl, @RequestParam("fileName") String fileName) {
        ArrayList<List<Comment>> responseComFiles = new ArrayList<>();
        Optional<Project> project = appProjectService.getProjectByGitUrl(gitUrl);
        List<ProjectAnalysis> projectAnalysis = appProjectAnalysisService.getProjectAnalysisByProjectName(project.get().getName());
        List<ProjectFile> files = appProjectAnalysisService.getProjectFilesByName(project.get().getName());
        for (ProjectAnalysis analysis : projectAnalysis) {
            int number = 0;
            for (ProjectFile file : files) {
                if (file.getName().equals(fileName)) {
                    responseComFiles.add(file.getComments());
                    number++;
                }
            }
            System.out.println(number);
        }
        if (responseComFiles.size() > 0) {
            return ResponseEntity.ok(responseComFiles);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //get similarities
    @CrossOrigin(origins = "*")
    @GetMapping("/similarities")
    public ResponseEntity<ArrayList<Map<String, Double>>> getSimilarities(@RequestParam("gitUrl") String gitUrl, @RequestParam("fileName") String fileName) {
        ArrayList<Map<String, Double>> responseSimilaritiesFiles = new ArrayList<>();
        Optional<Project> project = appProjectService.getProjectByGitUrl(gitUrl);
        List<ProjectFile> files = appProjectAnalysisService.getProjectFilesByName(project.get().getName());
        for (ProjectFile file : files) {
            if (file.getName().equals(fileName)) {
                responseSimilaritiesFiles.add(file.getSimilarity());
            }
        }
        if (responseSimilaritiesFiles.size() > 0) {
            return ResponseEntity.ok(responseSimilaritiesFiles);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
