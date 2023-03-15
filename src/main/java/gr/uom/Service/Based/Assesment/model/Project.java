package gr.uom.Service.Based.Assesment.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "gitUrl")
    private String gitUrl;
    @Column(name = "owner")
    private String owner;
    @Column(name = "name")
    private String name;
    @Column(name = "directory")
    private String directory;
    @ElementCollection
    private List<String> SHA = new ArrayList<>();
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectAnalysis> projectAnalysis = new ArrayList<>();
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
                '}';
    }

    public List<String> getSHA() {
        return SHA;
    }

    public void setSHA(List<String> SHA) {
        this.SHA = SHA;
    }

    public List<ProjectAnalysis> getProjectAnalysis() {
        return projectAnalysis;
    }

    public void setProjectAnalysis(List<ProjectAnalysis> projectAnalysis) {
        this.projectAnalysis = projectAnalysis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
