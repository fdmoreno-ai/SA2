package com.proyecto.sa2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.HashMap;

@Controller
public class LoginController {

    // Mostrar la página de login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // apunta a login.html en src/main/resources/templates
    }

    // Procesar el login enviado por JS (fetch)
    @PostMapping("/unnamed/login")
    @ResponseBody
    public Map<String, Object> loginJson(@RequestBody Map<String, String> payload) {
        String usuario = payload.get("usuario");
        String password = payload.get("password");

        Map<String, Object> response = new HashMap<>();

        // Usuario de prueba
        if ("admin".equals(usuario) && "1234".equals(password)) {
            response.put("success", true);
        } else {
            response.put("success", false);
        }

        return response;
    }
}
