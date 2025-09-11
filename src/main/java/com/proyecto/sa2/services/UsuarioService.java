package com.proyecto.sa2.services;

import com.proyecto.sa2.models.Rol;
import com.proyecto.sa2.models.Usuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {

    private List<Usuario> usuarios = new ArrayList<>();

    public UsuarioService() {
        // Crear usuarios de prueba
        usuarios.add(new Usuario("admin", "1234", Rol.ADMIN));
        usuarios.add(new Usuario("usuario1", "abcd", Rol.USUARIO));
    }

    // Método de autenticación
    public Usuario autenticar(String nombre, String password) {
        for (Usuario u : usuarios) {
            if (u.getNombre().equals(nombre) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null; // login fallido
    }
}
