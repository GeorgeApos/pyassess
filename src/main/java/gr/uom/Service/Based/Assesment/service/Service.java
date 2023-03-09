package gr.uom.Service.Based.Assesment.service;

import gr.uom.Service.Based.Assesment.model.Project;
import gr.uom.Service.Based.Assesment.model.ProjectFile;
import gr.uom.Service.Based.Assesment.repository.ProjectFileRepository;
import gr.uom.Service.Based.Assesment.repository.ProjectRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.util.FileSystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


@org.springframework.stereotype.Service
public class Service {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectFileRepository projectFileRepository;

    @Value("${github.token}")
    private String githubToken;

    public void cloneRepository(String owner, String repoName, String cloneDir) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + githubToken);
            return execution.execute(request, body);
        });

        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repoName;
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
        Map<String, Object> json = response.getBody();
        String cloneUrl = (String) json.get("clone_url");

        Git.cloneRepository()
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, ""))
                .setURI(cloneUrl)
                .setDirectory(new File(cloneDir))
                .call();
    }

    private int i;
    public Project runCommand(String gitUrl) throws Exception {
        Project mainProject = new Project();

        storeUrlOwnerAndName(gitUrl, mainProject);

        String homeDirectory = System.getProperty("user.dir") + File.separator + mainProject.getName();

        mainProject.setSHA(findSHA(homeDirectory, mainProject.getOwner(), mainProject.getName()));

        if (projectRepository.existsProjectBySHA(mainProject.getSHA())) {
            if (mainProject.getSHA().equals(projectRepository.findProjectBySHA(mainProject.getSHA()).getSHA())){
                throw new Exception("This commit has already been analyzed");
            } else if (projectRepository.existsProjectBySHA(mainProject.getSHA())) {
                projectRepository.removeProjectBySHA(mainProject.getSHA());
            }
        }
        
        mainProject.setDirectory(homeDirectory);

        cloneRepository(mainProject.getOwner(), mainProject.getName(), homeDirectory);
        
        Path dir = Paths.get(homeDirectory);
        File folder = new File(homeDirectory);
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
                        mainProject.setFiles(fileList);
                        newFile.setProjectName(mainProject.getName());
                    });
        }

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

        Thread firstThread = new Thread(testsRunnable(mainProject, fileList, homeDirectory));
        Thread secondThread = new Thread(pylintRunnable(chunkedLists, mainProject, 0));
        Thread thirdThread = new Thread(pylintRunnable(chunkedLists, mainProject, 1));
        Thread fourthThread = new Thread(pylintRunnable(chunkedLists, mainProject, 2));
        Thread fifthTread = new Thread(pylintRunnable(chunkedLists, mainProject, 3));

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

        FileSystemUtils.deleteRecursively(new File(homeDirectory));
        return mainProject;
    }

    private String findSHA(String cloneDir, String owner, String repoName) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + githubToken);
            return execution.execute(request, body);
        });

        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repoName + "/commits";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map[]> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map[].class);
        Map<String, Object> json = response.getBody()[0];
        String sha = (String) json.get("sha");

        return sha;
    }

    public void executeCommand(Project project, ArrayList<ProjectFile> fileList, String command, String destination) throws IOException, InterruptedException {
        ArrayList<String> similarityResponse = new ArrayList<>();
        ArrayList<String> commentsResponse = new ArrayList<>();
        Process p = Runtime.getRuntime().exec(command + destination);
        InputStream is = p.getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            for (String line : lines) {
                if(command.startsWith("python3 -W ignore " + System.getProperty("user.dir")+ File.separator + "duplicate-code-detection-tool/duplicate_code_detection.py -d  ")){
                    if(line.contains("Code duplication probability for") || line.startsWith(project.getDirectory())){
                        similarityResponse.add(line);
                    }
                }else if(command.startsWith("pylint")){
                    if(line.contains("Your code has been rated at") || line.startsWith("************* Module ") || line.startsWith(project.getName())) {
                        commentsResponse.add(line);
                    }
                }else if(command.startsWith("pytest")){
                    if(line.contains(project.getDirectory()) || line.startsWith("TOTAL")){
                        storeDataInObjects(project, fileList, line, command);
                    }
                }
                System.out.println(line);
            }
            if (similarityResponse.size()>0){
                storeSimilarity(similarityResponse, fileList, project);
            }
            if (commentsResponse.size()>0) {
                storeComments(commentsResponse, fileList, project);
            }
        }

    }


    public Runnable pylintRunnable( List<ArrayList<ProjectFile>> chunkedLists, Project mainProject, int index){
        return () -> {
            for (ProjectFile file : chunkedLists.get(index))
                try {
                    executeCommand(mainProject, chunkedLists.get(index), "pylint ", String.valueOf(file.getFirstFile()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        };
    }

    public Runnable testsRunnable(Project mainProject, ArrayList<ProjectFile> fileList, String homeDirectory) {
        return () -> {
            try {
                executeCommand(mainProject, fileList, "pytest --cov=", homeDirectory);
                executeCommand(mainProject, fileList, "python3 -W ignore " + System.getProperty("user.dir")+ File.separator + "duplicate-code-detection-tool/duplicate_code_detection.py -d  ", homeDirectory);
                do{
                    executeCommand(mainProject, fileList, "pipreqs --force ", homeDirectory);
                }while (!(new File(homeDirectory + "/requirements.txt")).exists());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("PipReqs counted " + countLineBufferedReader(mainProject, homeDirectory + "/requirements.txt") + " dependencies on this project.");
        };
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<ProjectFile> getAllProjectFiles() {
        return projectFileRepository.findAll();
    }
}


