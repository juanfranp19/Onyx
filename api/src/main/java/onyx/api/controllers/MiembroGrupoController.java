package onyx.api.controllers;

import onyx.api.entities.MiembroGrupo;
import onyx.api.entities.MiembroGrupoId;
import onyx.api.repositories.MiembroGrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/miembros-grupos")
public class MiembroGrupoController {

    @Autowired
    private MiembroGrupoRepository miembroGrupoRepository;

    @GetMapping
    public List<MiembroGrupo> getAll() {
        return miembroGrupoRepository.findAll();
    }

    @GetMapping("/{usuarioId}/{grupoId}")
    public ResponseEntity<MiembroGrupo> getById(@PathVariable Integer usuarioId, @PathVariable Integer grupoId) {
        MiembroGrupoId id = new MiembroGrupoId(usuarioId, grupoId);
        return miembroGrupoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MiembroGrupo create(@RequestBody MiembroGrupo miembro) {
        return miembroGrupoRepository.save(miembro);
    }

    @DeleteMapping("/{usuarioId}/{grupoId}")
    public ResponseEntity<Void> delete(@PathVariable Integer usuarioId, @PathVariable Integer grupoId) {
        MiembroGrupoId id = new MiembroGrupoId(usuarioId, grupoId);
        if (miembroGrupoRepository.existsById(id)) {
            miembroGrupoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
