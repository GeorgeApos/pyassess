package gr.uom.Service.Based.Assesment.model;

import jakarta.persistence.*;

import java.io.File;
import java.util.*;

@Entity
@Table(name = "project_files")
public class ProjectFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "firstFile")
    File firstFile;
    @Column(name = "name")
    String name;
    @Column(name = "stmts")
    int stmts;
    @Column(name = "miss")
    int miss;
    @Column(name = "coverage")
    int coverage;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;
    @Column(name = "rating")
    Double rating;
    @Column(name = "previousRating")
    Double previousRating;
    @ElementCollection
    Map<String, Double> similarity;
    @ManyToOne
    private Project project;
    @Column(name = "projectName")
    private String projectName;

    public ProjectFile(File firstFile, Map<String, Double> similarity){
        this.firstFile = firstFile;
        this.similarity = similarity;
    }

    public ProjectFile() {
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
                ", projectName=" + projectName +
                '}';
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) { this.comments = comments; }

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