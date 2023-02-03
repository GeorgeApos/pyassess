package gr.uom.Service.Based.Assesment.dto;

import java.io.File;
import java.util.ArrayList;

public class ProjectFile {
    File firstFile;
    String name;
    int stmts;
    int miss;
    int coverage;
    ArrayList<String> comments;
    Double rating;
    Double previousRating;
    ArrayList<Double> similarity;

    public ProjectFile(File firstFile, ArrayList<Double> similarity){
        this.firstFile = firstFile;
        this.similarity = similarity;
    }

    @Override
    public String toString() {
        return "ProjectFile{" +
                "firstFile=" + firstFile +
                ", name='" + name + '\'' +
                ", stmts=" + stmts +
                ", miss=" + miss +
                ", coverage=" + coverage +
                ", comments='" + comments + '\'' +
                ", rating=" + rating +
                ", previousRating=" + previousRating +
                ", similarity=" + similarity +
                '}';
    }

    public File getFirstFile() {
        return firstFile;
    }

    public void setFirstFile(File firstFile) {
        this.firstFile = firstFile;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getStmts() {
        return stmts;
    }

    public void setStmts(int stmts) {
        this.stmts = stmts;
    }

    public int getMiss() {
        return miss;
    }

    public void setMiss(int miss) {
        this.miss = miss;
    }

    public int getCoverage() {
        return coverage;
    }

    public void setCoverage(int coverage) {
        this.coverage = coverage;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) { this.comments = comments; }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getPreviousRating() {
        return previousRating;
    }

    public void setPreviousRating(Double previousRating) {
        this.previousRating = previousRating;
    }

    public ArrayList<Double> getSimilarity() {
        return similarity;
    }

    public void setSimilarity(ArrayList<Double> similarity) {
        this.similarity = similarity;
    }
}