package onyx.api.controllers;

import onyx.api.dto.TareaRequestDTO;
import onyx.api.entities.Grupo;
import onyx.api.entities.Tarea;
import onyx.api.entities.Usuario;
import onyx.api.repositories.GrupoRepository;
import onyx.api.repositories.TareaRepository;
import onyx.api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tareas")
public class TareaController {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @GetMapping
    public List<Tarea> getAll() {
        return tareaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarea> getById(@PathVariable Integer id) {
        return tareaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Tarea> create(@RequestBody TareaRequestDTO tarea) {

        Usuario creador = usuarioRepository.findById(tarea.getCreador_id())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Grupo grupo = grupoRepository.findById(tarea.getGrupo_id())
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        Tarea newTarea = new Tarea();
        newTarea.setTitulo(tarea.getTitulo());
        newTarea.setDescripcion(tarea.getDescripcion());
        newTarea.setFechaVencimiento(tarea.getFechaVencimiento());
        newTarea.setCreador(creador);
        newTarea.setGrupo(grupo);

        tareaRepository.save(newTarea);

        return ResponseEntity.ok(newTarea);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarea> update(@PathVariable Integer id, @RequestBody Tarea tarea) {
        return tareaRepository.findById(id)
                .map(existing -> {
                    tarea.setId(id);
                    return ResponseEntity.ok(tareaRepository.save(tarea));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (tareaRepository.existsById(id)) {
            tareaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
