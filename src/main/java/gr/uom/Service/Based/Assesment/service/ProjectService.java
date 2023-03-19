package gr.uom.Service.Based.Assesment.service;

import gr.uom.Service.Based.Assesment.model.Project;
import gr.uom.Service.Based.Assesment.model.ProjectAnalysis;
import gr.uom.Service.Based.Assesment.repository.ProjectRepository;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.client.RestTemplate;
import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectAnalysisService projectAnalysisService;

    public Project runCommand(String gitUrl) throws Exception {
        Project project = new Project();
        ArrayList<ProjectAnalysis> projectAnalysisList = new ArrayList<ProjectAnalysis>();

        storeProjectUrlOwnerAndName(gitUrl, project);
        String homeDirectory = System.getProperty("user.dir") + File.separator + project.getName();
        project.setDirectory(homeDirectory);

        Repository repo = new RepositoryBuilder().setGitDir(new File(project.getDirectory() + File.separator + ".git")).build();
        Git git = new Git(repo);

        cloneRepository(git, project.getOwner(), project.getName(), project.getDirectory());

        List<String> SHAs = captureSHAs(gitUrl, project.getOwner(), project.getName());
        List<String> selectedSHAs = new ArrayList<>();

        if(SHAs.size() > 10) {
            selectedSHAs.add(SHAs.get(0));
            int step = SHAs.size() / 9;

            for (int i = 1; i < SHAs.size(); i += step) {
                selectedSHAs.add(SHAs.get(i));
            }
        } else {
            selectedSHAs = SHAs;
        }

        project.setSHA(selectedSHAs);

        for(String sha: selectedSHAs){
            ObjectId commitId = repo.resolve(sha);
            git.checkout().setName(commitId.getName()).call();
            projectAnalysisList.add(projectAnalysisService.runCommand(project, sha, homeDirectory));
        }

        project.setProjectAnalysis(projectAnalysisList);

        repo.close();
        git.close();
        deleteDirectory(new File(project.getDirectory()));
        return project;
    }

    public void deleteDirectory(File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    private List<String> captureSHAs(String gitUrl, String owner, String name) throws Exception {
        List<String> commitSHAs = new ArrayList<>();

        RepositoryService repoService = new RepositoryService();
        CommitService commitService = new CommitService();

        org.eclipse.egit.github.core.Repository repository = repoService.getRepository(owner, name);

        List<RepositoryCommit> commits = commitService.getCommits(repository);

        for (RepositoryCommit commit : commits) {
            commitSHAs.add(commit.getSha());
        }

        return commitSHAs;
    }

    public static void storeProjectUrlOwnerAndName(String gitUrl, Project project) {
        String[] url = gitUrl.split("/");
        project.setGitUrl(gitUrl);
        project.setName(url[4].split("\\.")[0]);
        project.setOwner(url[3]);
    }

    public void cloneRepository(Git git, String owner, String repoName, String cloneDir) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> execution.execute(request, body));

        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repoName;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
        Map<String, Object> json = response.getBody();
        String cloneUrl = (String) json.get("clone_url");

        git.cloneRepository().setURI(cloneUrl).setDirectory(new File(cloneDir)).call();

    }

    public Optional<Project> getProjectByGitUrl(String gitUrl) {
        return projectRepository.findProjectByGitUrl(gitUrl);
    }

    public void saveProject(Project resultProject) {
        projectRepository.save(resultProject);
    }

}
