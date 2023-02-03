package gr.uom.Service.Based.Assesment;

import gr.uom.Service.Based.Assesment.dto.Project;
import gr.uom.Service.Based.Assesment.dto.ProjectFile;

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
                Pattern filePattern = Pattern.compile("([^\\\\]+.py)");
                Matcher fileMatcher = filePattern.matcher(response);
                Boolean fileFind = fileMatcher.find();

                Pattern covPattern = Pattern.compile(".*[\\s]+([0-9]+)");
                Matcher covMatcher = covPattern.matcher(response);
                Boolean covFind = covMatcher.find();

                Pattern missPattern = Pattern.compile(".*[\\s]+([0-9]+)[\\s]+[0-9]+");
                Matcher missMatcher = missPattern.matcher(response);
                Boolean missFind = missMatcher.find();

                Pattern stmtsPattern = Pattern.compile("^.*?[^\\s]+[\\s]+([0-9]+)[\\s]+[0-9]+[\\s]+[0-9]+");
                Matcher stmtsMatcher = stmtsPattern.matcher(response);
                Boolean stmtsFind = stmtsMatcher.find();

                for(int i=0; i < fileList.size(); i++) {
                    if (fileFind && covFind && missFind && stmtsFind) {
                        String regexFileName = fileMatcher.group(1);
                        String Statements = stmtsMatcher.group(1);
                        String Miss = missMatcher.group(1);
                        String Coverage = covMatcher.group(1);

                        if (fileList.get(i).getName().equals(regexFileName)) {
                            fileList.get(i).setStmts(Integer.parseInt(Statements));
                            fileList.get(i).setMiss(Integer.parseInt(Miss));
                            fileList.get(i).setCoverage(Integer.parseInt(Coverage));
                        }
                    }
                }
            } else if (response.startsWith("TOTAL")){
                Pattern totalStmtsPattern = Pattern.compile("^TOTAL\\s+([0-9]+)");
                Matcher totalStmtsMatcher = totalStmtsPattern.matcher(response);
                Boolean totalStmtsFind = totalStmtsMatcher.find();

                Pattern totalMissPattern = Pattern.compile("^TOTAL\\s+\\d+\\s+([0-9]+)");
                Matcher totalMissMatcher = totalMissPattern.matcher(response);
                Boolean totalMissFind = totalMissMatcher.find();

                Pattern totalCovPattern = Pattern.compile("^TOTAL\\s+\\d+\\s+\\d+\\s+([0-9]+).");
                Matcher totalCovMatcher = totalCovPattern.matcher(response);
                Boolean totalCovFind = totalCovMatcher.find();
                if(totalStmtsFind && totalMissFind && totalCovFind){
                    Long stmts = Long.parseLong(totalStmtsMatcher.group(1));
                    Long miss = Long.parseLong(totalMissMatcher.group(1));
                    Long cov = Long.parseLong(totalCovMatcher.group(1));

                    project.setTotalStmts(stmts);
                    project.setTotalMiss(miss);
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
        HashMap<String, ArrayList<Double>> similarityMap = new HashMap<>();

        for(int i=0; i < similarityResponse.size(); i++) {
            if (similarityResponse.get(i).contains("Code duplication probability for")) {
                fileList.get(position).setSimilarity(similarityMap.get(mainFile));
                mainFile = "";
                Pattern mainFilePattern = Pattern.compile("([^\\\\]+.py)");
                Matcher mainFileMatcher = mainFilePattern.matcher(similarityResponse.get(i));
                Boolean fileFind = mainFileMatcher.find();

                if (fileFind) {
                    mainFile = mainFileMatcher.group(1);
                    for (int j = 0; j < fileList.size(); j++) {
                        if (fileList.get(j).getName().equals(mainFile)) {
                            position = j;
                        }
                    }
                    similarityMap.put(mainFile, new ArrayList<Double>());
                }
            } else if (similarityResponse.get(i).startsWith(project.getDirectory())) {
                Pattern filePattern = Pattern.compile("([^\\\\]+.py)");
                Matcher fileMatcher = filePattern.matcher(similarityResponse.get(i));
                Boolean fileFind = fileMatcher.find();

                Pattern similarityPattern = Pattern.compile("m([+-]?[0-9]*\\.?[0-9]+(?:[eE][+-]?[0-9]+)?)");
                Matcher similarityMatcher = similarityPattern.matcher(similarityResponse.get(i));
                Boolean similarityFind = similarityMatcher.find();
                if (fileFind && similarityFind) {
                    String regexFileName = fileMatcher.group(1);
                    Double similarity = Double.valueOf(similarityMatcher.group(1));
                    similarityMap.get(mainFile).add(similarity);
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

                Pattern filePattern = Pattern.compile("\\b(\\w+)$");
                Matcher fileMatcher = filePattern.matcher(currentLine);
                Boolean fileFind = fileMatcher.find();
                if (fileFind) {
                    mainFile = fileMatcher.group(1);
                    currentProjectFile = findProjectFile(mainFile, fileList);
                }
            } else if (currentLine.startsWith(project.getDirectory())) {
                comments.add(currentLine);
            } else if (currentLine.contains("Your code has been rated at")) {
                if (currentProjectFile != null) {
                    currentProjectFile.setComments(comments);
                    comments = new ArrayList<>();
                    Pattern ratingPattern = Pattern.compile("at (\\b[0-9]+\\.[0-9]+)/10");
                    Matcher ratingMatcher = ratingPattern.matcher(currentLine);
                    Boolean ratingFind = ratingMatcher.find();

                    Pattern previousRatingPattern = Pattern.compile("run: (\\b[0-9]+\\.[0-9]+)/10");
                    Matcher previousRatingMatcher = previousRatingPattern.matcher(currentLine);
                    Boolean previousRatingFind = previousRatingMatcher.find();

                    if (ratingFind && previousRatingFind) {
                        Double rating = Double.valueOf(ratingMatcher.group(1));
                        Double previousRating = Double.valueOf(previousRatingMatcher.group(1));

                        currentProjectFile.setRating(rating);
                        currentProjectFile.setPreviousRating(previousRating);
                    }
                }
            }
        }
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
