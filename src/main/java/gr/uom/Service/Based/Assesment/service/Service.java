package gr.uom.Service.Based.Assesment.service;

import static gr.uom.Service.Based.Assesment.Parser.*;
import gr.uom.Service.Based.Assesment.dto.Project;
import gr.uom.Service.Based.Assesment.dto.ProjectFile;


import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashMap;


@org.springframework.stereotype.Service
public class Service {

    private int i;
    public void runCommand() throws IOException, InterruptedException, ExecutionException {
        String homeDirectory = "C:\\Users\\geoap\\Documents\\SmoothStream";
        Path dir = Paths.get(homeDirectory);
        File folder = new File(homeDirectory);
        File[] listOfFiles = folder.listFiles();
        Project mainProject = new Project(homeDirectory);
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
                    });
        }

        for (ProjectFile file: fileList){
            executeCommand(mainProject, fileList, "pylint ", String.valueOf(file.getFirstFile()));
        }

        executeCommand(mainProject, fileList,"pytest --cov=", homeDirectory);
        executeCommand(mainProject, fileList,"python3 -W ignore /home/Documents/duplicate-code-detection-tool/duplicate_code_detection.py -d  ", homeDirectory);
        executeCommand(mainProject, fileList,"pipreqs --force", homeDirectory);
        System.out.println("PipReqs counted " + countLineBufferedReader(mainProject,homeDirectory+"/requirements.txt") + " dependencies on this project.");

        System.out.println(mainProject);
    }
    public void executeCommand(Project project, ArrayList<ProjectFile> fileList, String command, String destination) throws IOException, InterruptedException {
        ArrayList<String> similarityResponse = new ArrayList<>();
        ArrayList<String> commentsResponse = new ArrayList<>();
        Process p = Runtime.getRuntime().exec(command + destination);
        InputStream is = p.getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            for (String line : lines) {
                if(command.startsWith("python3 -W ignore /home/Documents/duplicate-code-detection-tool/duplicate_code_detection.py -d  ")){
                    if(line.contains("Code duplication probability for") || line.startsWith(project.getDirectory())){
                        similarityResponse.add(line);
                    }
                }else if(command.startsWith("pylint")){
                    if(line.contains("Your code has been rated at") || line.startsWith("************* Module ") || line.startsWith(project.getDirectory())) {
                        commentsResponse.add(line);
                    }
                }
                System.out.println(line);
                storeDataInObjects(project, fileList, line, command);
            }
            if (similarityResponse.size()>0){
                storeSimilarity(similarityResponse, fileList, project);
            }
            if (commentsResponse.size()>0) {
                storeComments(commentsResponse, fileList, project);
            }
        }

    }





}


