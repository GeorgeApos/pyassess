package gr.uom.Service.Based.Assesment;

import gr.uom.Service.Based.Assesment.model.Comment;
import gr.uom.Service.Based.Assesment.model.ProjectAnalysis;
import gr.uom.Service.Based.Assesment.model.ProjectFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.sshd.common.util.GenericUtils.length;

public class Parser {

    private static final Map<String, Map<String, String>> REGEX_PATTERNS = new HashMap<>();

    static {
        Map<String, String> windowsPatterns = new HashMap<>();
        Map<String, String> unixPatterns = new HashMap<>();

        windowsPatterns.put("file", "([^\\\\]+.py)");
        windowsPatterns.put("cov", ".*[\\s]+([0-9]+)");
        windowsPatterns.put("miss", ".*[\\s]+([0-9]+)[\\s]+[0-9]+");
        windowsPatterns.put("stmts", "^.*?[^\\s]+[\\s]+([0-9]+)[\\s]+[0-9]+[\\s]+[0-9]+");
        windowsPatterns.put("totalCov", "^TOTAL\\s+\\d+\\s+\\d+\\s+([0-9]+).");
        windowsPatterns.put("totalMiss", "^TOTAL\\s+\\d+\\s+([0-9]+)");
        windowsPatterns.put("totalStmts", "^TOTAL\\s+([0-9]+)");

        windowsPatterns.put("mainFile", "([^\\\\]+.py)");
        windowsPatterns.put("file", "([^\\\\]+.py)");
        windowsPatterns.put("similarity", "m([+-]?[0-9]*\\.?[0-9]+(?:[eE][+-]?[0-9]+)?)");

        windowsPatterns.put("file", "\\b(\\w+)$");
        windowsPatterns.put("rating", "at (\\b[0-9]+\\.[0-9]+)/10");
        windowsPatterns.put("previousRating", "run: (\\b[0-9]+\\.[0.9]+)/10");

        unixPatterns.put("file", "([^/]+.py)");
        unixPatterns.put("cov", ".*\\s+\\d+\\s+\\d+\\s+(\\d+)%");
        unixPatterns.put("miss", ".*\\s+\\d+\\s+(\\d+)\\s+\\d+%");
        unixPatterns.put("stmts", ".*\\s+(\\d+)\\s+\\d+\\s+\\d+%");
        unixPatterns.put("totalCov", "^TOTAL\\s+\\d+\\s+\\d+\\s+([0-9]+).");
        unixPatterns.put("totalMiss", "^TOTAL\\s+\\d+\\s+([0-9]+)");
        unixPatterns.put("totalStmts", "^TOTAL\\s+([0-9]+)");

        unixPatterns.put("mainFile", "([^/]+.py)");
        unixPatterns.put("file", "([^/]+.py)");
        unixPatterns.put("similarity", "([0-9]+\\.[0-9]+)");

        unixPatterns.put("file", "\\b(\\w+)$");
        unixPatterns.put("rating", "at (\\b[0-9]+\\.[0-9]+)/10");
        unixPatterns.put("previousRating", "run: (\\b[0-9]+\\.[0.9]+)/10");

        REGEX_PATTERNS.put("Windows", windowsPatterns);
        REGEX_PATTERNS.put("Unix", unixPatterns);
    }

    public static void storeUrlOwnerAndName(String gitUrl, ProjectAnalysis mainProjectAnalysis) {
        String[] url = gitUrl.split("/");
        mainProjectAnalysis.setGitUrl(gitUrl);
        mainProjectAnalysis.setName(url[4].split("\\.")[0]);
        mainProjectAnalysis.setOwner(url[3]);
    }
    public static long countLineBufferedReader(ProjectAnalysis projectAnalysis, String fileName) {
        ArrayList<String> dependencies = new ArrayList<>();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                dependencies.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        storeDepsInProject(projectAnalysis, dependencies, dependencies.size());
        return dependencies.size();

    }

    public static void storeDataInObjects(ProjectAnalysis projectAnalysis, ArrayList<ProjectFile> fileList, String response, String command) {

        if(command.startsWith("pytest --cov=")) {
            if(response.startsWith(projectAnalysis.getName())){
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
            }
            else if (response.startsWith("TOTAL")){
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
                    projectAnalysis.setTotalStmts(stmts);
                }
                if(totalMissFind){
                    Long miss = Long.parseLong(totalMissMatcher.group(1));
                    projectAnalysis.setTotalMiss(miss);
                }
                if(totalCovFind){
                    Long cov = Long.parseLong(totalCovMatcher.group(1));
                    projectAnalysis.setTotalCoverage(cov);
                }
            }
        }
    }

    public static void storeDepsInProject(ProjectAnalysis projectAnalysis, ArrayList<String> dependencies, int dependenciesCounter){
        projectAnalysis.setDependenciesCounter(dependenciesCounter);
        projectAnalysis.setDependencies(dependencies);
    }

    public static void storeSimilarity(ArrayList<String> similarityResponse, ArrayList<ProjectFile> fileList, ProjectAnalysis projectAnalysis){
        String mainFile = "";
        int position = 0;
        Map<String, HashMap<String, Double>> similarityMap = new HashMap<>();

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
            } else if (similarityResponse.get(i).startsWith(projectAnalysis.getDirectory())) {
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


    public static void storeComments(ArrayList<String> commentsResponse, ArrayList<ProjectFile> fileList, ProjectAnalysis projectAnalysis) {
        String mainFile = "";
        List<Comment> comments = new ArrayList<>();
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
            } else if (currentLine.startsWith(projectAnalysis.getName())) {
                comments.add(new Comment(currentLine.replace("'", "\\'")));
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

    private static String regexPattern(String command, String request) {
        Boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        Map<String, String> patterns = REGEX_PATTERNS.get(isWindows ? "Windows" : "Unix");

        if (patterns != null && patterns.containsKey(request)) {
            return patterns.get(request);
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





