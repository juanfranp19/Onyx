package onyx.api.controllers;

import onyx.api.entities.AsignacionTarea;
import onyx.api.entities.AsignacionTareaId;
import onyx.api.repositories.AsignacionTareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asignaciones-tareas")
public class AsignacionTareaController {

    @Autowired
    private AsignacionTareaRepository asignacionTareaRepository;

    @GetMapping
    public List<AsignacionTarea> getAll() {
        return asignacionTareaRepository.findAll();
    }

    @GetMapping("/{usuarioId}/{tareaId}")
    public ResponseEntity<AsignacionTarea> getById(@PathVariable Integer usuarioId, @PathVariable Integer tareaId) {
        AsignacionTareaId id = new AsignacionTareaId(usuarioId, tareaId);
        return asignacionTareaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AsignacionTarea create(@RequestBody AsignacionTarea asignacion) {
        return asignacionTareaRepository.save(asignacion);
    }

    @DeleteMapping("/{usuarioId}/{tareaId}")
    public ResponseEntity<Void> delete(@PathVariable Integer usuarioId, @PathVariable Integer tareaId) {
        AsignacionTareaId id = new AsignacionTareaId(usuarioId, tareaId);
        if (asignacionTareaRepository.existsById(id)) {
            asignacionTareaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
