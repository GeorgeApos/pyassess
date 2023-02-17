package gr.uom.Service.Based.Assesment.repository;

import gr.uom.Service.Based.Assesment.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
