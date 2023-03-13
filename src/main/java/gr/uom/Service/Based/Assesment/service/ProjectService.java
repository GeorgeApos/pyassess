package gr.uom.Service.Based.Assesment.service;

import gr.uom.Service.Based.Assesment.model.Project;
import gr.uom.Service.Based.Assesment.model.ProjectAnalysis;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class ProjectService {

    @Autowired
    private ProjectAnalysisService projectAnalysisService;

    public void runCommand(String gitUrl) throws Exception {
        Project project = new Project();
        ArrayList<ProjectAnalysis> projectAnalysisList = new ArrayList<ProjectAnalysis>();

        cloneRepository(project.getOwner(), project.getName(), System.getProperty("user.dir"));

        storeProjectUrlOwnerAndName(gitUrl, project);

        String homeDirectory = System.getProperty("user.dir") + File.separator + project.getName();
        project.setDirectory(homeDirectory);

        List<String> SHAs = captureSHAs(gitUrl, homeDirectory);

        project.setSHA(SHAs);

        for(String sha: SHAs){
            projectAnalysisList.add(projectAnalysisService.runCommand(sha, homeDirectory));
        }

        project.setProjectAnalysis(projectAnalysisList);

    }

    private List<String> captureSHAs(String gitUrl, String homeDirectory) {
        return null;
    }

    public static void storeProjectUrlOwnerAndName(String gitUrl, Project project) {
        String[] url = gitUrl.split("/");
        project.setGitUrl(gitUrl);
        project.setName(url[4].split("\\.")[0]);
        project.setOwner(url[3]);
    }

    public void cloneRepository(String owner, String repoName, String cloneDir) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> execution.execute(request, body));

        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repoName;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
        Map<String, Object> json = response.getBody();
        String cloneUrl = (String) json.get("clone_url");

        Git clone = Git.cloneRepository()
                .setURI(cloneUrl)
                .setDirectory(new File(cloneDir))
                .call();
    }

}
