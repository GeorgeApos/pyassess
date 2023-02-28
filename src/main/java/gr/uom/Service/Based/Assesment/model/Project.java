package gr.uom.Service.Based.Assesment.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "gitUrl", unique = true)
    private String gitUrl;
    @Column(name = "SHA")
    private String SHA;
    @Column(name = "owner")
    private String owner;
    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "directory")
    private String directory;
    @ElementCollection
    @Column(name = "dependencies")
    private List<String> dependencies;
    @Column(name = "dependenciesCounter")
    private long dependenciesCounter;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectFile> files;
    @Column(name = "totalCoverage")
    private long totalCoverage;
    @Column(name = "totalMiss")
    private long totalMiss;
    @Column(name = "totalStmts")
    private long totalStmts;

    public Project() {
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", gitUrl='" + gitUrl + '\'' +
                ", SHA='" + SHA + '\'' +
                ", owner='" + owner + '\'' +
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

    public String getSHA() {
        return SHA;
    }

    public void setSHA(String SHA) {
        this.SHA = SHA;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

