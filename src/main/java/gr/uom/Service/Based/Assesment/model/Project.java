package gr.uom.Service.Based.Assesment.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Project {

    private static long counter;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
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
        this.id = counter++;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", directory='" + directory + '\'' +
                ", dependencies=" + dependencies +
                ", dependenciesCounter=" + dependenciesCounter +
                ", files=" + files +
                ", totalCoverage=" + totalCoverage +
                ", totalMiss=" + totalMiss +
                ", totalStmts=" + totalStmts +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

