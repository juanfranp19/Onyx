package onyx.api.controllers;

import onyx.api.dto.LoginRequestDTO;
import onyx.api.dto.RegisterRequestDTO;
import onyx.api.dto.UsuarioRequestDTO;
import onyx.api.entities.Usuario;
import onyx.api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<Usuario> getAll() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{nombreUsuario}")
    public ResponseEntity<Usuario> getById(@PathVariable String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RegisterRequestDTO registerRequest) {
        if (usuarioRepository.findByNombreUsuario(registerRequest.getNombreUsuario()).isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe");
        }
        
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(registerRequest.getNombreUsuario());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(registerRequest.getPasswordHash()));
        
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Integer id, @RequestBody UsuarioRequestDTO usuarioRequest) {
        return usuarioRepository.findById(id)
                .map(existing -> {

                    Usuario newUsuario = new Usuario();
                    newUsuario.setId(id);
                    newUsuario.setNombreUsuario(usuarioRequest.getNombreUsuario());
                    newUsuario.setEmail(usuarioRequest.getEmail());
                    newUsuario.setFechaRegistro(existing.getFechaRegistro());

                    if (!usuarioRequest.getPasswordHash().isEmpty()) {
                        newUsuario.setPasswordHash(passwordEncoder.encode(usuarioRequest.getPasswordHash()));
                    }

                    return ResponseEntity.ok(usuarioRepository.save(newUsuario));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        return usuarioRepository.findByNombreUsuario(loginRequest.getNombreUsuario())
                .filter(user -> passwordEncoder.matches(loginRequest.getPasswordHash(), user.getPasswordHash()))
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.status(401).build());
    }
}
