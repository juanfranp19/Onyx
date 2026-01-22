package onyx.api.repositories;

import onyx.api.entities.AsignacionTarea;
import onyx.api.entities.AsignacionTareaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionTareaRepository extends JpaRepository<AsignacionTarea, AsignacionTareaId> {
}
