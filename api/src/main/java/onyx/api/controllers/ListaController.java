package onyx.api.controllers;

import onyx.api.entities.Lista;
import onyx.api.repositories.ListaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/listas")
public class ListaController {

    @Autowired
    private ListaRepository listaRepository;

    @GetMapping
    public List<Lista> getAll() {
        return listaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lista> getById(@PathVariable Integer id) {
        return listaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Lista create(@RequestBody Lista lista) {
        return listaRepository.save(lista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lista> update(@PathVariable Integer id, @RequestBody Lista lista) {
        return listaRepository.findById(id)
                .map(existing -> {
                    lista.setId(id);
                    return ResponseEntity.ok(listaRepository.save(lista));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (listaRepository.existsById(id)) {
            listaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
