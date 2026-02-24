package onyx.api.repositories;

import onyx.api.entities.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Integer> {
    List<Tarea> findByGrupo_Id(Integer id);

    @Modifying
    @Query("DELETE FROM Tarea t WHERE t.grupo.id = :grupoId")
    void deleteByGrupoId(@Param("grupoId") Integer grupoId);
}
