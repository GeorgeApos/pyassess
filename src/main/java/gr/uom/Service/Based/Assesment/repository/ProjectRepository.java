package gr.uom.Service.Based.Assesment.repository;

import gr.uom.Service.Based.Assesment.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findProjectByGitUrl(String gitUrl);

    Project findProjectBySHA(String sha);

    boolean existsProjectBySHA(String sha);

    void removeProjectBySHA(String sha);
}
