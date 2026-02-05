package onyx.api.controllers;

import onyx.api.dto.GrupoRequestDTO;
import onyx.api.entities.Grupo;
import onyx.api.entities.Usuario;
import onyx.api.repositories.GrupoRepository;
import onyx.api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<Grupo> getAll() {
        return grupoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grupo> getById(@PathVariable Integer id) {
        return grupoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("usuario/{id}")
    public ResponseEntity<List<Grupo>> getByIdUsuario(@PathVariable Long id) {

        List<Grupo> grupos = grupoRepository.findGruposByUsuarioId(id);

        if (grupos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(grupos);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody GrupoRequestDTO grupo) {
        System.out.println("Recibida petición crear grupo: " + grupo);

        if (grupo.getCreadorId() == null) {
            System.out.println("Error: creadorId es null");
            return ResponseEntity.badRequest().body("El ID del creador es obligatorio");
        }

        Usuario creador = usuarioRepository.findById(grupo.getCreadorId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + grupo.getCreadorId()));

        Grupo newGrupo = new Grupo();
        newGrupo.setNombre(grupo.getNombre());
        newGrupo.setDescripcion(grupo.getDescripcion());
        newGrupo.setCreador(creador);
        newGrupo.getUsuarios().add(creador);

        grupoRepository.save(newGrupo);
        System.out.println("Grupo creado con éxito: " + newGrupo.getId());

        return ResponseEntity.ok(newGrupo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grupo> update(@PathVariable Integer id, @RequestBody Grupo grupo) {
        return grupoRepository.findById(id)
                .map(existing -> {
                    grupo.setId(id);
                    return ResponseEntity.ok(grupoRepository.save(grupo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (grupoRepository.existsById(id)) {
            grupoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
