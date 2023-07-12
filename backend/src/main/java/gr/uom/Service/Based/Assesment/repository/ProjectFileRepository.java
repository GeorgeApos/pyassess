package gr.uom.Service.Based.Assesment.repository;

import gr.uom.Service.Based.Assesment.model.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {

    List<ProjectFile> findProjectFilesByProjectName(String projectName);

}
