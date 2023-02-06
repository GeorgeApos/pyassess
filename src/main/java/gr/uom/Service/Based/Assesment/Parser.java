package gr.uom.Service.Based.Assesment;

import gr.uom.Service.Based.Assesment.dto.Project;
import gr.uom.Service.Based.Assesment.dto.ProjectFile;
import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryBasedSynchronizationStrategy;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Parser {



    public static long countLineBufferedReader(Project project, String fileName) {
        ArrayList<String> dependencies = new ArrayList<>();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                dependencies.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        storeDepsInProject(project, dependencies, dependencies.size());
        return dependencies.size();

    }


    public static void storeDataInObjects(Project project,ArrayList<ProjectFile> fileList, String response, String command) {

        if(command.startsWith("pytest --cov=")) {
            if(response.startsWith(project.getDirectory())){
                Pattern filePattern = Pattern.compile(regexPattern(command,"file"));
                Matcher fileMatcher = filePattern.matcher(response);
                Boolean fileFind = fileMatcher.find();

                Pattern covPattern = Pattern.compile(regexPattern(command,"cov"));
                Matcher covMatcher = covPattern.matcher(response);
                Boolean covFind = covMatcher.find();

                Pattern missPattern = Pattern.compile(regexPattern(command,"miss"));
                Matcher missMatcher = missPattern.matcher(response);
                Boolean missFind = missMatcher.find();

                Pattern stmtsPattern = Pattern.compile(regexPattern(command,"stmts"));
                Matcher stmtsMatcher = stmtsPattern.matcher(response);
                Boolean stmtsFind = stmtsMatcher.find();

                for(int i=0; i < fileList.size(); i++) {
                    if (fileFind) {
                        String regexFileName = fileMatcher.group(1);
                        if (fileList.get(i).getName().equals(regexFileName)) {
                            if (covFind) {
                                String Coverage = covMatcher.group(1);
                                fileList.get(i).setCoverage(Integer.parseInt(Coverage));
                            }
                            if (missFind) {
                                String Miss = missMatcher.group(1);
                                fileList.get(i).setMiss(Integer.parseInt(Miss));
                            }
                            if (stmtsFind) {
                                String Statements = stmtsMatcher.group(1);
                                fileList.get(i).setStmts(Integer.parseInt(Statements));
                            }
                        }
                    }
                }
            } else if (response.startsWith("TOTAL")){
                Pattern totalStmtsPattern = Pattern.compile(regexPattern(command,"totalStmts"));
                Matcher totalStmtsMatcher = totalStmtsPattern.matcher(response);
                Boolean totalStmtsFind = totalStmtsMatcher.find();

                Pattern totalMissPattern = Pattern.compile(regexPattern(command,"totalMiss"));
                Matcher totalMissMatcher = totalMissPattern.matcher(response);
                Boolean totalMissFind = totalMissMatcher.find();

                Pattern totalCovPattern = Pattern.compile(regexPattern(command,"totalCov"));
                Matcher totalCovMatcher = totalCovPattern.matcher(response);
                Boolean totalCovFind = totalCovMatcher.find();
                if(totalStmtsFind){
                    Long stmts = Long.parseLong(totalStmtsMatcher.group(1));
                    project.setTotalStmts(stmts);
                }
                if(totalMissFind){
                    Long miss = Long.parseLong(totalMissMatcher.group(1));
                    project.setTotalMiss(miss);
                }
                if(totalCovFind){
                    Long cov = Long.parseLong(totalCovMatcher.group(1));
                    project.setTotalCoverage(cov);
                }
            }
        } else if(command.startsWith("pylint")) {

        }

    }

    public static void storeDepsInProject(Project project, ArrayList<String> dependencies, int dependenciesCounter){
        project.setDependenciesCounter(dependenciesCounter);
        project.setDependencies(dependencies);
    }

    public static void storeSimilarity(ArrayList<String> similarityResponse, ArrayList<ProjectFile> fileList, Project project){
        String mainFile = "";
        int position = 0;
        HashMap<String, HashMap<String, Double>> similarityMap = new HashMap<>();

        for(int i=0; i < similarityResponse.size(); i++) {
            if (similarityResponse.get(i).contains("Code duplication probability for")) {
                fileList.get(position).setSimilarity(similarityMap.get(mainFile));
                mainFile = "";
                Pattern mainFilePattern = Pattern.compile(regexPattern("duplication", "mainFile"));
                Matcher mainFileMatcher = mainFilePattern.matcher(similarityResponse.get(i));
                Boolean fileFind = mainFileMatcher.find();

                if (fileFind) {
                    mainFile = mainFileMatcher.group(1);
                    for (int j = 0; j < fileList.size(); j++) {
                        if (fileList.get(j).getName().equals(mainFile)) {
                            position = j;
                        }
                    }
                    similarityMap.put(mainFile, new HashMap<>());
                }
            } else if (similarityResponse.get(i).startsWith(project.getDirectory())) {
                Pattern filePattern = Pattern.compile(regexPattern("duplication", "file"));
                Matcher fileMatcher = filePattern.matcher(similarityResponse.get(i));
                Boolean fileFind = fileMatcher.find();

                Pattern similarityPattern = Pattern.compile(regexPattern("duplication", "similarity"));
                Matcher similarityMatcher = similarityPattern.matcher(similarityResponse.get(i));
                Boolean similarityFind = similarityMatcher.find();
                if (fileFind) {
                    String regexFileName = fileMatcher.group(1);

                    if (!similarityMap.containsKey(mainFile)) {
                        similarityMap.put(mainFile, new HashMap<>());
                    }
                    Double similarity = 0.0;
                    if (similarityFind) {
                        similarity = Double.valueOf(similarityMatcher.group(1));
                    }
                    similarityMap.get(mainFile).put(regexFileName, similarity);
                }
            }
        }
        fileList.get(position).setSimilarity(similarityMap.get(mainFile));
    }


    public static void storeComments(ArrayList<String> commentsResponse, ArrayList<ProjectFile> fileList, Project project) {
        String mainFile = "";
        ArrayList<String> comments = new ArrayList<>();
        ProjectFile currentProjectFile = null;

        for (int i = 0; i < commentsResponse.size(); i++) {
            String currentLine = commentsResponse.get(i);
            if (currentLine.startsWith("************* Module")) {

                Pattern filePattern = Pattern.compile(regexPattern("pylint", "file"));
                Matcher fileMatcher = filePattern.matcher(currentLine);
                Boolean fileFind = fileMatcher.find();
                if (fileFind) {
                    mainFile = fileMatcher.group(1);
                    currentProjectFile = findProjectFile(mainFile, fileList);
                }
            } else if (currentLine.startsWith(project.getDirectory())) {
                comments.add(currentLine.replace("'", "\\'"));
            } else if (currentLine.contains("Your code has been rated at")) {
                if (currentProjectFile != null) {
                    currentProjectFile.setComments(comments);
                    comments = new ArrayList<>();
                    Pattern ratingPattern = Pattern.compile(regexPattern("pylint", "rating"));
                    Matcher ratingMatcher = ratingPattern.matcher(currentLine);
                    Boolean ratingFind = ratingMatcher.find();

                    Pattern previousRatingPattern = Pattern.compile(regexPattern("pylint", "previousRating"));
                    Matcher previousRatingMatcher = previousRatingPattern.matcher(currentLine);
                    Boolean previousRatingFind = previousRatingMatcher.find();

                    if (ratingFind) {
                        Double rating = Double.valueOf(ratingMatcher.group(1));
                        currentProjectFile.setRating(rating);
                    }
                    if (previousRatingFind) {
                        Double previousRating = Double.valueOf(previousRatingMatcher.group(1));
                        currentProjectFile.setPreviousRating(previousRating);
                    }
                }
            }
        }
    }

    private static String regexPattern(String command, String request){
        Boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        if(isWindows){
            if(command.startsWith("pytest")){
                if(request.equals("file")){
                    return "([^\\\\]+.py)";
                }else if(request.equals("cov")) {
                    return ".*[\\s]+([0-9]+)";
                }else if(request.equals("miss")) {
                    return ".*[\\s]+([0-9]+)[\\s]+[0-9]+";
                }else if(request.equals("stmts")) {
                    return "^.*?[^\\s]+[\\s]+([0-9]+)[\\s]+[0-9]+[\\s]+[0-9]+";
                }else if(request.equals("totalCov")) {
                    return "^TOTAL\\s+\\d+\\s+\\d+\\s+([0-9]+).";
                }else if(request.equals("totalMiss")) {
                    return "^TOTAL\\s+\\d+\\s+([0-9]+)";
                }else if(request.equals("totalStmts")) {
                    return "^TOTAL\\s+([0-9]+)";
                }
            }else if (command.startsWith("duplication")){
                if(request.equals("mainFile")){
                    return "([^\\\\]+.py)";
                }else if(request.equals("file")) {
                    return "([^\\\\]+.py)";
                }else if(request.equals("similarity")) {
                    return "m([+-]?[0-9]*\\.?[0-9]+(?:[eE][+-]?[0-9]+)?)";
                }
            }else if(command.startsWith("pylint")){
                if(request.equals("file")){
                    return "\\b(\\w+)$";
                }else if(request.equals("rating")) {
                    return "at (\\b[0-9]+\\.[0-9]+)/10";
                }else if(request.equals("previousRating")) {
                    return "run: (\\b[0-9]+\\.[0-9]+)/10";
                }
            }
        }else {
            if(command.startsWith("pytest")){
                if(request.equals("file")){
                    return "([^/]+.py)";
                }else if(request.equals("cov")) {
                    return ".*\\s+\\d+\\s+\\d+\\s+(\\d+)%";
                }else if(request.equals("miss")) {
                    return ".*\\s+\\d+\\s+(\\d+)\\s+\\d+%";
                }else if(request.equals("stmts")) {
                    return ".*\\s+(\\d+)\\s+\\d+\\s+\\d+%";
                }else if(request.equals("totalCov")) {
                    return "^TOTAL\\s+\\d+\\s+\\d+\\s+([0-9]+).";
                }else if(request.equals("totalMiss")) {
                    return "^TOTAL\\s+\\d+\\s+([0-9]+)";
                }else if(request.equals("totalStmts")) {
                    return "^TOTAL\\s+([0-9]+)";
                }
            }else if (command.startsWith("duplication")){
                if(request.equals("mainFile")){
                    return "([^/]+.py)";
                }else if(request.equals("file")) {
                    return "([^/]+.py)";
                }else if(request.equals("similarity")) {
                    return "([0-9]+\\.[0-9]+)";
                }
            }else if(command.startsWith("pylint")){
                if(request.equals("file")){
                    return "\\b(\\w+)$";
                }else if(request.equals("rating")) {
                    return "at (\\b[0-9]+\\.[0-9]+)/10";
                }else if(request.equals("previousRating")) {
                    return "run: (\\b[0-9]+\\.[0-9]+)/10";
                }
            }
        }

        return "";
    }

    private static ProjectFile findProjectFile(String fileName, ArrayList<ProjectFile> fileList) {
        for (ProjectFile file : fileList) {
            if (file.getName().contains(fileName)) {
                return file;
            }
        }
        return null;
    }

}
