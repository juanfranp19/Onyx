package onyx.api.controllers;

import onyx.api.dto.GrupoRequestDTO;
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
import java.util.Optional;

@RestController
@RequestMapping("/grupos")
public class GrupoController {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TareaRepository tareaRepository;

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

        // lista de IDs de usuarios del grupo
        List<Integer> usuariosId = grupo.getUsuariosId();

        Usuario creador = usuarioRepository.findById(grupo.getCreadorId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + grupo.getCreadorId()));

        Grupo newGrupo = new Grupo();
        newGrupo.setNombre(grupo.getNombre());
        newGrupo.setDescripcion(grupo.getDescripcion());
        newGrupo.setCreador(creador);

        // añade al creador a los usuarios del grupo
        newGrupo.getUsuarios().add(creador);

        if (usuariosId != null) {
            // añade al resto de usuarios
            usuariosId.forEach(userId -> {

                // objeto usuario
                Optional<Usuario> user = usuarioRepository.findById(userId);

                // lo añade a la lista de usuarios
                user.ifPresent(usuario -> newGrupo.getUsuarios().add(usuario));

            });
        }

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

            // grupo con id del path
            Grupo grupo = grupoRepository.findById(id).isPresent() ? grupoRepository.findById(id).get() : null;

            // lanza excepción si grupo es null
            assert grupo != null;

            // tareas del grupo
            List<Tarea> tareas = grupo.getTareas();

            // elimina cada tarea de la lista
            tareaRepository.deleteAll(tareas);

            // elimina el grupo
            grupoRepository.deleteById(id);

            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
