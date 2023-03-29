package gr.uom.Service.Based.Assesment.repository;

import gr.uom.Service.Based.Assesment.model.ProjectAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;
import java.util.Optional;import java.util.*;


public interface ProjectAnalysisRepository extends JpaRepository<ProjectAnalysis, Long> {
    Optional<ProjectAnalysis> findProjectByGitUrl(String gitUrl);

    ProjectAnalysis findProjectBySHA(String sha);

    boolean existsProjectBySHA(String sha);

    void removeProjectBySHA(String sha);

    ProjectAnalysis findProjectAnalysisByProjectName(String name);
}
