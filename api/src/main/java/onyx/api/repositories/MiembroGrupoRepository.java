package onyx.api.repositories;

import onyx.api.entities.MiembroGrupo;
import onyx.api.entities.MiembroGrupoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MiembroGrupoRepository extends JpaRepository<MiembroGrupo, MiembroGrupoId> {
}
