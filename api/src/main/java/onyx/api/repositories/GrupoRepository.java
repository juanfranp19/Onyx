package onyx.api.repositories;

import onyx.api.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Integer> {
    @Query("SELECT g\n" +
            "    FROM Grupo g\n" +
            "    JOIN g.usuarios u\n" +
            "    WHERE u.id = :userId")
    List<Grupo> findGruposByUsuarioId(@Param("userId") Long userId);
}
