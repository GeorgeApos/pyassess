package gr.uom.Service.Based.Assesment.service;

import gr.uom.Service.Based.Assesment.model.Project;
import gr.uom.Service.Based.Assesment.model.ProjectAnalysis;
import gr.uom.Service.Based.Assesment.model.ProjectFile;
import gr.uom.Service.Based.Assesment.repository.ProjectFileRepository;
import gr.uom.Service.Based.Assesment.repository.ProjectAnalysisRepository;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gr.uom.Service.Based.Assesment.Parser.*;


@Service
public class ProjectAnalysisService {
    @Autowired
    private ProjectAnalysisRepository projectAnalysisRepository;
    @Autowired
    private ProjectFileRepository projectFileRepository;

    private int i;
    public ProjectAnalysis runCommand(Project mainProject, String sha, String gitUrl) throws Exception {
        ProjectAnalysis mainProjectAnalysis = new ProjectAnalysis();

        mainProjectAnalysis.setSHA(sha);
        mainProjectAnalysis.setGitUrl(gitUrl);
        mainProjectAnalysis.setOwner(mainProject.getOwner());
        mainProjectAnalysis.setName(mainProject.getName());
        mainProjectAnalysis.setDirectory(mainProject.getDirectory());

//        mainProjectAnalysis.setSHA(findSHA(mainProjectAnalysis.getOwner(), mainProjectAnalysis.getName()));

        if (projectAnalysisRepository.existsProjectBySHA(mainProjectAnalysis.getSHA())) {
        if (mainProjectAnalysis.getSHA().equals(projectAnalysisRepository.findProjectBySHA(mainProjectAnalysis.getSHA()).getSHA())){
            throw new Exception("This commit has already been analyzed");
        } else if (projectAnalysisRepository.existsProjectBySHA(mainProjectAnalysis.getSHA())) {
            projectAnalysisRepository.removeProjectBySHA(mainProjectAnalysis.getSHA());
            }
        }

        Path dir = Paths.get(mainProjectAnalysis.getDirectory());
        File folder = new File(mainProjectAnalysis.getDirectory());
        File[] listOfFiles = folder.listFiles();
        ArrayList<ProjectFile> fileList = new ArrayList<ProjectFile>();
        HashMap<String, Double> fileSimilarityLIst = new HashMap<String, Double>(listOfFiles.length);
        try (Stream<Path> stream = Files.walk(dir)){
            stream
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".py"))
            .forEach(file -> {
            ProjectFile newFile = new ProjectFile(file.toFile(), fileSimilarityLIst);
            newFile.setName(file.toFile().getName());
            fileList.add(newFile);
            newFile.setProjectName(mainProjectAnalysis.getName());
            mainProjectAnalysis.getFiles().add(newFile);
        });
        }

        mainProjectAnalysis.setFiles(fileList);


        int fileListSize = fileList.size()/4;
        List<ArrayList<ProjectFile>> chunkedLists = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int startIndex = i * fileListSize;
            int endIndex = startIndex + fileListSize;
            if (i == 3) {
                endIndex = fileList.size();
            }
            chunkedLists.add(new ArrayList<>(fileList.subList(startIndex, endIndex)));
        }

        Thread firstThread = new Thread(testsRunnable(mainProjectAnalysis, fileList, mainProjectAnalysis.getDirectory()));
        Thread secondThread = new Thread(pylintRunnable(chunkedLists, mainProjectAnalysis, 0));
        Thread thirdThread = new Thread(pylintRunnable(chunkedLists, mainProjectAnalysis, 1));
        Thread fourthThread = new Thread(pylintRunnable(chunkedLists, mainProjectAnalysis, 2));
        Thread fifthTread = new Thread(pylintRunnable(chunkedLists, mainProjectAnalysis, 3));

        firstThread.start();
        secondThread.start();
        thirdThread.start();
        fourthThread.start();
        fifthTread.start();


        firstThread.join();
        secondThread.join();
        thirdThread.join();
        fourthThread.join();
        fifthTread.join();

        return mainProjectAnalysis;
    }

    public String findSHA(String owner, String repoName) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> execution.execute(request, body));

        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repoName + "/commits";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map[]> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map[].class);
        Map<String, Object> json = response.getBody()[0];
        String sha = (String) json.get("sha");

        return sha;
    }

    public void executeCommand(ProjectAnalysis projectAnalysis, ArrayList<ProjectFile> fileList, String command, String destination) throws IOException, InterruptedException {
        ArrayList<String> similarityResponse = new ArrayList<>();
        ArrayList<String> commentsResponse = new ArrayList<>();
        Process p = Runtime.getRuntime().exec(command + destination);
        InputStream is = p.getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            for (String line : lines) {
                if(command.startsWith("python3 -W ignore " + System.getProperty("user.dir")+ File.separator + "duplicate-code-detection-tool/duplicate_code_detection.py -d  ")){
                    if(line.contains("Code duplication probability for") || line.startsWith(projectAnalysis.getDirectory())){
                        similarityResponse.add(line);
                    }
                }else if(command.startsWith("pylint")){
                    if(line.contains("Your code has been rated at") || line.startsWith("************* Module ") || line.startsWith(projectAnalysis.getName())) {
                        commentsResponse.add(line);
                    }
                }else if(command.startsWith("pytest")){
                    if(line.startsWith(projectAnalysis.getName()) || line.startsWith("TOTAL")){
                        storeDataInObjects(projectAnalysis, fileList, line, command);
                    }
                }
                System.out.println(line);
            }
            if (similarityResponse.size()>0){
                storeSimilarity(similarityResponse, fileList, projectAnalysis);
            }
            if (commentsResponse.size()>0) {
                storeComments(commentsResponse, fileList, projectAnalysis);
            }
        }

    }


    public Runnable pylintRunnable(List<ArrayList<ProjectFile>> chunkedLists, ProjectAnalysis mainProjectAnalysis, int index){
        return () -> {
            for (ProjectFile file : chunkedLists.get(index))
                try {
                    executeCommand(mainProjectAnalysis, chunkedLists.get(index), "pylint ", String.valueOf(file.getFirstFile()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        };
    }

    public Runnable testsRunnable(ProjectAnalysis mainProjectAnalysis, ArrayList<ProjectFile> fileList, String homeDirectory) {
        return () -> {
            try {
                executeCommand(mainProjectAnalysis, fileList, "pytest --cov=", homeDirectory);
                executeCommand(mainProjectAnalysis, fileList, "python3 -W ignore " + System.getProperty("user.dir")+ File.separator + "duplicate-code-detection-tool/duplicate_code_detection.py -d  ", homeDirectory);
                do{
                    executeCommand(mainProjectAnalysis, fileList, "pipreqs --force ", homeDirectory);
                }while (!(new File(homeDirectory + "/requirements.txt")).exists());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("PipReqs counted " + countLineBufferedReader(mainProjectAnalysis, homeDirectory + "/requirements.txt") + " dependencies on this project.");
            new File(homeDirectory + "/requirements.txt").delete();
        };
    }

    public List<ProjectAnalysis> getAllProjects() {
        return projectAnalysisRepository.findAll();
    }

    public List<ProjectFile> getAllProjectFiles() {
        return projectFileRepository.findAll();
    }

    public Optional<ProjectAnalysis> getProjectByGitUrl(String gitUrl) {
        return projectAnalysisRepository.findProjectByGitUrl(gitUrl);
    }

    public void deleteProject(Long projectId) {
        projectAnalysisRepository.deleteById(projectId);
    }

    public void saveProject(ProjectAnalysis savedProjectAnalysis) {
        projectAnalysisRepository.save(savedProjectAnalysis);
    }

    public List<ProjectFile> getProjectFilesByName(String projectName) {
        return projectFileRepository.findProjectFilesByProjectName(projectName);
    }

    public List<ProjectAnalysis> getProjectAnalysisByProjectName(String name) {
        List<ProjectAnalysis> projectAnalysisList = new ArrayList<>();
        for (ProjectAnalysis projectAnalysis : projectAnalysisRepository.findAll())
            if (projectAnalysis.getName().equals(name)){
                projectAnalysisList.add(projectAnalysis);
                List<ProjectFile> files = getProjectFilesByName(projectAnalysis.getName());
                projectAnalysis.setFiles(files);
            }

        return projectAnalysisList;
    }

}


