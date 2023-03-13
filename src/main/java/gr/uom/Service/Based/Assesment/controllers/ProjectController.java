package gr.uom.Service.Based.Assesment.controllers;

import gr.uom.Service.Based.Assesment.model.Project;
import gr.uom.Service.Based.Assesment.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    private ProjectService appProjectService;

    @CrossOrigin(origins = "*")
    @PostMapping("/")
    public ResponseEntity<Project> startAnalysis(@RequestParam("gitUrl") String gitUrl) throws Exception {
        appProjectService.runCommand(gitUrl);
        return ResponseEntity.ok().build();
    }

}
