package gr.uom.Service.Based.Assesment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import gr.uom.Service.Based.Assesment.model.Project;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findProjectByGitUrl(String gitUrl);

    Project findProjectByName(String name);

    Project findProjectByOwner(String owner);

    Project findProjectByDirectory(String directory);

    void deleteByGitUrl(String gitUrl);
}
