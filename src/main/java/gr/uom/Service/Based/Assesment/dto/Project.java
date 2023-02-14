package gr.uom.Service.Based.Assesment.dto;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String directory;
    @ElementCollection
    private List<String> dependencies;
    private long dependenciesCounter;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectFile> files;
    private long totalCoverage;
    private long totalMiss;
    private long totalStmts;

    public Project() {
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", directory='" + directory + '\'' +
                ", dependencies=" + dependencies +
                ", dependenciesCounter=" + dependenciesCounter +
                ", files=" + files +
                ", totalCoverage=" + totalCoverage +
                ", totalMiss=" + totalMiss +
                ", totalStmts=" + totalStmts +
                '}';
    }

    public long getTotalMiss() {
        return totalMiss;
    }

    public void setTotalMiss(long totalMiss) {
        this.totalMiss = totalMiss;
    }

    public long getTotalStmts() {
        return totalStmts;
    }

    public void setTotalStmts(long totalStmts) {
        this.totalStmts = totalStmts;
    }

    public Project(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public List<ProjectFile> getFiles() {
        return files;
    }

    public long getDependenciesCounter() {
        return dependenciesCounter;
    }

    public void setDependenciesCounter(long dependenciesCounter) {
        this.dependenciesCounter = dependenciesCounter;
    }

    public void setFiles(List<ProjectFile> files) { this.files = files; }

    public long getTotalCoverage() {
        return totalCoverage;
    }

    public void setTotalCoverage(long totalCoverage) {
        this.totalCoverage = totalCoverage;
    }
}

