package onyx.api.controllers;

import onyx.api.entities.Comentario;
import onyx.api.repositories.ComentarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @GetMapping
    public List<Comentario> getAll() {
        return comentarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comentario> getById(@PathVariable Integer id) {
        return comentarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Comentario create(@RequestBody Comentario comentario) {
        return comentarioRepository.save(comentario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comentario> update(@PathVariable Integer id, @RequestBody Comentario comentario) {
        return comentarioRepository.findById(id)
                .map(existing -> {
                    comentario.setId(id);
                    return ResponseEntity.ok(comentarioRepository.save(comentario));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (comentarioRepository.existsById(id)) {
            comentarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
