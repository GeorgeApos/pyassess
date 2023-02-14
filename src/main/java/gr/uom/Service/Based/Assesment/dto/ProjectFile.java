package gr.uom.Service.Based.Assesment.dto;

import jakarta.persistence.*;

import java.io.File;
import java.util.*;

@Entity
public class ProjectFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    File firstFile;
    String name;
    int stmts;
    int miss;
    int coverage;
    @ElementCollection
    List<String> comments;
    Double rating;
    Double previousRating;
    @ElementCollection
    Map<String, Double> similarity;
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    public ProjectFile(File firstFile, Map<String, Double> similarity){
        this.firstFile = firstFile;
        this.similarity = similarity;
    }

    @Override
    public String toString() {
        return "ProjectFile{" +
                "id=" + id +
                ", firstFile=" + firstFile +
                ", name='" + name + '\'' +
                ", stmts=" + stmts +
                ", miss=" + miss +
                ", coverage=" + coverage +
                ", comments=" + comments +
                ", rating=" + rating +
                ", previousRating=" + previousRating +
                ", similarity=" + similarity +
                ", project=" + project +
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

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) { this.comments = comments; }

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

    public Map<String, Double> getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Map<String, Double> similarity) {
        this.similarity = similarity;
    }
}