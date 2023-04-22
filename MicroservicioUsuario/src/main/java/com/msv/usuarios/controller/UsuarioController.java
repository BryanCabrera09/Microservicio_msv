package com.msv.usuarios.controller;

import com.msv.usuarios.models.entity.Usuario;
import com.msv.usuarios.services.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class UsuarioController {

    @Autowired
    IUsuarioService usuarioService;

    @GetMapping
    public List<Usuario> listar() {

        return usuarioService.findAll();
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {

        Optional<Usuario> usuario = usuarioService.findById(id);

        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result) {

        if (result.hasErrors()) {
            return validar(result);
        }

        if (usuarioService.findByEmail(usuario.getEmail()).isPresent()) {

            return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", "Correo Electronico Existente"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.save(usuario));
    }

    @PutMapping("/crear/{id}")
    public ResponseEntity<?> actualizar(@Valid @RequestBody Usuario usuario, @PathVariable Long id, BindingResult result) {

        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> usuarioOptional = usuarioService.findById(id);

        if (usuarioOptional.isPresent()) {
            Usuario user = usuarioOptional.get();

            if (usuarioService.findByEmail(usuario.getEmail()).isPresent() && !usuario.getEmail().equalsIgnoreCase(user.getEmail())) {

                return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje", "Correo Electronico Existente"));
            }

            user.setNombre(usuario.getNombre());
            user.setEmail(usuario.getEmail());
            user.setPassword(usuario.getPassword());

            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.save(user));
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Optional<Usuario> usuario = usuarioService.findById(id);

        if (usuario.isPresent()) {

            usuarioService.delete(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    private static ResponseEntity<Map<String, String>> validar(BindingResult result) {

        Map<String, String> errores = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El Campo " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errores);
    }
}
